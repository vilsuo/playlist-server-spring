package com.fs.fsapi.metallum.driver;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fs.fsapi.bookmark.parser.LinkElement;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.parser.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.parser.MetallumParser;
import com.fs.fsapi.metallum.parser.SongResult;
import com.fs.fsapi.metallum.response.AaDataValue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetallumWebDriverService {
  
  private final WebDriver driver;

  private final MetallumParser parser;

  public static final String COOKIE_NAME = "cf_clearance";
  private String cookieValue = "MSmv8ktFlsRVVJVOAuxKdxlzUkdr87cvyv6KBDGmGI4-1726663642-1.2.1.1-tkKDIfpP63429xOiaE4spluoP1yS0CV46K_xrlcXPtzNhUrnn41ILUrPTb1woMdTKpJSb1bT8Et66bMaUZaLkVsjY0Uf0LhXqs0dgmJEiLV0w72mLZzzd.uHjKIW8CXX15YaH6AS.H2QuwT__DpZAveg2gKt__MYPfRSphLApHFeASxnNZoUuzhhA7AuWCrquTuXDD8JMq6G7E7W7qHz8iH5wUrdhTLK89cCWtbNyo1rL2uB627GdI72217jav.EVHh7fyOiqfR7by9.PZZWo2jWXy05LaB6L72Fb71ob20hFBsh4CSNRZS8LHMI5z_tJri04bcmHDFJpQ9Ib8T7.XGeYI5T9h_YNr9aFu1m91PobYQLz05njTFH2SgsOnkDa9wwrN0Za7RGArBkseSoXoAe.UZjDUdItiHlSPMQijO8cr_9a_ObIMq1vIfWXD70";

  public void setCookieValue(String value) {
    this.cookieValue = value;
  }

  public String getCookieValue() {
    return cookieValue;
  }

  private final By LOCATOR_HOME_LINK = By.xpath("//a[@id='MA_logo']");

  private final By LOCATOR_SEARCH_BODY = By.cssSelector(
    "table[id='searchResultsAlbum'] > tbody > tr"
  );

  /**
   * Search basic release information. Contains links for the artist page and
   * the release title page. Caches results to increase performance.
   * 
   * @param artist  the artist name
   * @param title  the release title
   * @return basic search result
   * @throws JsonProcessingException 
   * @throws JsonMappingException 
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

    WebElement tbody = driver.findElement(LOCATOR_SEARCH_BODY);

    final String html = driver.getPageSource();

    return parseSearchResults(html).get(0);
  }

  public List<ArtistTitleSearchResult> parseSearchResults(String html) {
    Document doc = Jsoup.parse(html);

    Element tbody = doc.select("#searchResultsAlbum > tbody").first();
    if (tbody == null) {
      throw new CustomMetallumScrapingException(
        "Song table was not found"
      );
    }

    List<ArtistTitleSearchResult> songs = new ArrayList<>();
    tbody.children().stream()
      .forEach(tr -> {
        if (tr.hasClass("even") || tr.hasClass("odd")) {
          Elements tds = tr.children();

          if (tds.size() == 1) {
            final String message = tds.get(0).ownText();
            throw new CustomDataNotFoundException(message);
          }

          final String band = tds.get(0).html();
          final String album = tds.get(1).html();
          final String releaseType = tds.get(2).ownText();

          AaDataValue aa = new AaDataValue(List.of(
            band, album, releaseType
          ));

          songs.add(parseSearchData(aa));
        }
      });

    return songs;
  }

  private ArtistTitleSearchResult parseSearchData(AaDataValue data) {
    return new ArtistTitleSearchResult(
      parseSearchDataElementOuterHtml(data.getArtistLinkElementOuterHtml()),
      parseSearchDataElementOuterHtml(data.getTitleLinkElementOuterHtml()),
      data.getReleaseType()
    );
  }

  private LinkElement parseSearchDataElementOuterHtml(String html) {
    final Element e = Jsoup.parse(html).selectFirst("a");

    if (e == null) {
      throw new CustomMetallumScrapingException(
        "Expected data '" + html + "' to contain a 'a' element"
      );
    } else if (!e.hasAttr("href")) {
      throw new CustomMetallumScrapingException(
        "Expected data element '" + e.toString() + "' to have 'href' attribute"
      );
    }

    return new LinkElement(e);
  }

  /**
   * Search songs by artist name and release title.
   * 
   * @param titleId  the release title id
   * @return  a list containing the details of each song
   * @throws InterruptedException 
   * @implNote {@code path} songs are search from release title page, same as
   * {@link ArtistTitleSearchResult#getTitleHref}
   */
  public List<SongResult> searchSongs(String titleId) throws InterruptedException {
    // only title id seems to be required,
    // artist and title can be empty...
    URI uri = UriComponentsBuilder
      .fromHttpUrl("https://www.metal-archives.com")
      .path("/albums/{artist}/{title}/{titleId}")
      .build("", "", titleId);

    driver.get(uri.toString());

    driver.manage().addCookie(new Cookie("cf_clearance", cookieValue));

    /*
    WebElement checkbox = driver
      .findElement(By.cssSelector("input[type='checkbox']"));

    checkbox.click();
    */

    driver.findElement(LOCATOR_HOME_LINK);

    final String html = driver.getPageSource();

    return parser.parseSongs(html);
  }
}
