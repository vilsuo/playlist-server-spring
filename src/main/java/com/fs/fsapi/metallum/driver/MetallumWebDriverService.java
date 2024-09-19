package com.fs.fsapi.metallum.driver;

import java.net.URI;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.parser.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.parser.LyricsResult;
import com.fs.fsapi.metallum.parser.SongResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetallumWebDriverService {
  
  private final CustomChromeDriver driver;

  private final MetallumPageParser parser;

  public static final String COOKIE_NAME = "cf_clearance";

  // expires in 1 year
  private String cookieValue = "MSmv8ktFlsRVVJVOAuxKdxlzUkdr87cvyv6KBDGmGI4-1726663642-1.2.1.1-tkKDIfpP63429xOiaE4spluoP1yS0CV46K_xrlcXPtzNhUrnn41ILUrPTb1woMdTKpJSb1bT8Et66bMaUZaLkVsjY0Uf0LhXqs0dgmJEiLV0w72mLZzzd.uHjKIW8CXX15YaH6AS.H2QuwT__DpZAveg2gKt__MYPfRSphLApHFeASxnNZoUuzhhA7AuWCrquTuXDD8JMq6G7E7W7qHz8iH5wUrdhTLK89cCWtbNyo1rL2uB627GdI72217jav.EVHh7fyOiqfR7by9.PZZWo2jWXy05LaB6L72Fb71ob20hFBsh4CSNRZS8LHMI5z_tJri04bcmHDFJpQ9Ib8T7.XGeYI5T9h_YNr9aFu1m91PobYQLz05njTFH2SgsOnkDa9wwrN0Za7RGArBkseSoXoAe.UZjDUdItiHlSPMQijO8cr_9a_ObIMq1vIfWXD70";

  public void setCookieValue(String value) {
    this.cookieValue = value;
  }

  public String getCookieValue() {
    return cookieValue;
  }

  // LOCATORS FOR WAITING METALLUM PAGE LOADING

  private final By LOCATOR_SEARCH_TABLE_BODY_FIRST_ROW = By.cssSelector(
    "#searchResultsAlbum > tbody > tr"
  );

  private final By LOCATOR_SONG_TABLE_BODY_ROW = By.cssSelector(
    ".table_lyrics > tbody > tr"
  );

  /**
   * Search for basic release information.
   * 
   * @param artist  the artist name
   * @param title  the release title
   * @return search result
   */
  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title) {
    URI uri = UriComponentsBuilder
      .fromHttpUrl("https://www.metal-archives.com")
      .path("/search/advanced/searching/albums")
      .queryParam("bandName", artist)
      .queryParam("releaseTitle", title)
      .build()
      .toUri();

    driver.get(uri.toString());
    driver.manage().addCookie(new Cookie("cf_clearance", cookieValue));

    // wait for search results
    WebElement firstTr = driver.findElement(LOCATOR_SEARCH_TABLE_BODY_FIRST_ROW);

    // select the search results table body
    WebElement tBody = (WebElement) ((JavascriptExecutor) driver)
      .executeScript("return arguments[0].parentNode;", firstTr);

    // convert table body element to string
    final String html = tBody.getAttribute("outerHTML");
    return parser.parseSearchResults(html).get(0);
  }

  /**
   * Search songs of a release.
   * 
   * @param titleId  the release title id
   * @return release song list
   */
  public List<SongResult> searchSongs(String titleId) {
    // only title id seems to be required,
    // artist and title can be empty...
    URI uri = UriComponentsBuilder
      .fromHttpUrl("https://www.metal-archives.com")
      .path("/albums/{artist}/{title}/{titleId}")
      .build("", "", titleId);

    driver.get(uri.toString());
    driver.manage().addCookie(new Cookie("cf_clearance", cookieValue));

    // wait for songs
    WebElement firstTr = driver.findElement(LOCATOR_SONG_TABLE_BODY_ROW);

    // select the songs table body
    WebElement tBody = (WebElement) ((JavascriptExecutor) driver)
      .executeScript("return arguments[0].parentNode;", firstTr);

    // convert table body element to string
    final String html = tBody.getAttribute("outerHTML");
    return parser.parseSongs(html);
  }

  /**
   * Search song lyrics by song id.
   * 
   * @param songId  the song id
   * @return html string containing the lyrics, or html string describing
   *         the lyrics were not found
   */
  public LyricsResult searchSongLyrics(String titleId, String songId) {
    // only title id seems to be required,
    // artist and title can be empty...
    URI uri = UriComponentsBuilder
      .fromHttpUrl("https://www.metal-archives.com")
      .path("/albums/{artist}/{title}/{titleId}")
      .build("", "", titleId);

    driver.get(uri.toString());
    driver.manage().addCookie(new Cookie("cf_clearance", cookieValue));

    // wait for songs table to have a song
    WebElement firstTr = driver.findElement(LOCATOR_SONG_TABLE_BODY_ROW);

    // select the songs table body
    WebElement tbody = (WebElement) ((JavascriptExecutor) driver)
      .executeScript("return arguments[0].parentNode;", firstTr);

    WebElement lastTd = tbody.findElement(By.xpath(
      "//*[@name=" + songId + "]/parent::td/parent::tr/td[last()]"
    ));

    log.info("td element: " + lastTd.getAttribute("outerHTML"));
    log.info("child count: " + driver.childrenCount(lastTd));

    switch (driver.childrenCount(lastTd)) {
      case 0: // lyrics not available
        return new LyricsResult("Lyrics not available");

      case 1:
        // lyrics available or instrumental
        WebElement e = lastTd.findElement(By.cssSelector("td > :first-child"));

        switch (e.getTagName()) {
          case "a": // lyrics available
            // click show lyrics
            final By lyricsBtnSelector = By.cssSelector("#lyricsButton" + songId);
            WebElement lyricsBtn = tbody.findElement(lyricsBtnSelector);
            lyricsBtn.click();

            // wait for lyrics to appear
            final String LOADING_TEXT = "(loading lyrics...)";
            WebElement lyrics = driver.findElement(By.xpath(
              "//td[@id='lyrics_" + songId + "' and not(text()='" + LOADING_TEXT + "')]"
            ));

            final String html = lyrics.getText();
            //log.info("lyrics found html: " + html);

            return parser.parseLyrics(html);
        
          case "em": // instrumental
            return new LyricsResult("Instrumental");

          default:
            throw new CustomMetallumScrapingException("Unexpected children type");
        }
    
      default:
        throw new CustomMetallumScrapingException("Unexpected number of children");
    }
  }
}
