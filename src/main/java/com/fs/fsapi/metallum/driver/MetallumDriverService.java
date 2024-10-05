package com.fs.fsapi.metallum.driver;

import java.net.URI;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fs.fsapi.metallum.MetallumService;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.result.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.result.LyricsResult;
import com.fs.fsapi.metallum.result.SongResult;

import lombok.RequiredArgsConstructor;

// why wait for load in 'searchSongs'?
// - is even necessary?

@Service
@RequiredArgsConstructor
public class MetallumDriverService implements MetallumService {
  
  private final CustomChromeDriver driver;

  private final MetallumDriverParser parser;

  private final ArtistTitleSearchCache cache;

  // LOCATORS FOR WAITING METALLUM PAGE LOADING

  private final By LOCATOR_SEARCH_TABLE_BODY_FIRST_ROW = By.cssSelector(
    "#searchResultsAlbum > tbody > tr"
  );

  private final By LOCATOR_SONG_TABLE_BODY_FIRST_ROW = By.cssSelector(
    ".table_lyrics > tbody > tr"
  );

  @Override
  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title) {
    // check if cached
    final var cached = cache.get(artist, title);
    if (cached.isPresent()) {
      //return cached.get();
    }

    loadSearchPage(artist, title);

    // find search results table body
    final WebElement tbody = findTableBody(LOCATOR_SEARCH_TABLE_BODY_FIRST_ROW);

    final List<ArtistTitleSearchResult> results = parser
      .parseSearchResults(tbody.getAttribute("outerHTML"));
    final ArtistTitleSearchResult result = results.get(0);

    // update cache
    cache.put(artist, title, result);

    // return the "best" result...
    return result;
  }

  @Override
  public List<SongResult> searchSongs(String titleId) {
    loadTitlePage(titleId);
    driver.waitForLoad();

    return parser.parseSongs(driver.getPageSource());
  }
  
  @Override
  public LyricsResult searchSongLyrics(String titleId, String songId) {
    loadTitlePage(titleId);

    // find song table body
    final WebElement tbody = findTableBody(LOCATOR_SONG_TABLE_BODY_FIRST_ROW);

    // find the last data element of the correct row
    final WebElement lastTd = tbody.findElement(
      By.xpath("//*[@name=" + songId + "]/parent::td/parent::tr/td[last()]")
    );

    if (driver.childrenCount(lastTd) == 1) {
      final WebElement lyricsInfoElement = lastTd.findElement(
        By.cssSelector("td > :first-child")
      );

      final boolean lyricsAvailable = lyricsInfoElement.getTagName().equals("a");
      if (lyricsAvailable) {
        // toggle show lyrics
        final By lyricsBtnSelector = By.cssSelector("#lyricsButton" + songId);
        tbody.findElement(lyricsBtnSelector).click();

        // wait for lyrics to appear
        final String loadingText = "(loading lyrics...)";
        final WebElement lyricsElement = driver.findElement(By.xpath(
          "//td[@id='lyrics_" + songId + "' and not(text()='" + loadingText + "')]"
        ));

        return parser.parseLyrics(lyricsElement.getText());
      }
    }

    // lyrics not available or instrumental
    return parser.parseLyrics(lastTd.getText());
  }

  private void loadSearchPage(String artist, String title) {
    final URI uri = UriComponentsBuilder
      .fromHttpUrl("https://www.metal-archives.com")
      .path("/search/advanced/searching/albums")
      .queryParam("bandName", artist)
      .queryParam("releaseTitle", title)
      .build()
      .toUri();

    driver.get(uri);
  }

  private void loadTitlePage(String titleId) {
    // only title id seems to be required,
    // artist and title can be empty...
    final URI uri = UriComponentsBuilder
      .fromHttpUrl("https://www.metal-archives.com")
      .path("/albums/{artist}/{title}/{titleId}")
      .build("", "", titleId);

    driver.get(uri);
  }

  public void replaceCookie(String value) {
    driver.replaceCookie(value);
  }

  private WebElement findTableBody(By tableBodyRowLocator) {
    // wait for the element
    final WebElement firstTr = driver.findElement(tableBodyRowLocator);

    // select the songs table body
    return firstTr.findElement(By.xpath("./parent::tbody"));
  }
}
