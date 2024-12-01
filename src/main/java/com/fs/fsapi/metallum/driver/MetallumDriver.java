package com.fs.fsapi.metallum.driver;

import java.net.URI;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

// why 'wait for load' in 'searchSongs'?
// - is even necessary?

@Service
public class MetallumDriver {

  private final CustomWebDriver driver;

  // COOKIES
  private final String COOKIE_NAME = "cf_clearance";

  private final String DEFAULT_COOKIE_VALUE = "MSmv8ktFlsRVVJVOAuxKdxlzUkdr87cvyv6KBDGmGI4-1726663642-1.2.1.1-tkKDIfpP63429xOiaE4spluoP1yS0CV46K_xrlcXPtzNhUrnn41ILUrPTb1woMdTKpJSb1bT8Et66bMaUZaLkVsjY0Uf0LhXqs0dgmJEiLV0w72mLZzzd.uHjKIW8CXX15YaH6AS.H2QuwT__DpZAveg2gKt__MYPfRSphLApHFeASxnNZoUuzhhA7AuWCrquTuXDD8JMq6G7E7W7qHz8iH5wUrdhTLK89cCWtbNyo1rL2uB627GdI72217jav.EVHh7fyOiqfR7by9.PZZWo2jWXy05LaB6L72Fb71ob20hFBsh4CSNRZS8LHMI5z_tJri04bcmHDFJpQ9Ib8T7.XGeYI5T9h_YNr9aFu1m91PobYQLz05njTFH2SgsOnkDa9wwrN0Za7RGArBkseSoXoAe.UZjDUdItiHlSPMQijO8cr_9a_ObIMq1vIfWXD70";

  public MetallumDriver(CustomWebDriver driver) {
    this.driver = driver;

    this.setBypassCookie(DEFAULT_COOKIE_VALUE);
  }

  public void setBypassCookie(String value) {
    this.driver.setBaseUrlCookie(COOKIE_NAME, value);
  }

  // LOADING PAGES
  
  private void loadSearchPage(String artist, String title) {
    final URI uri = UriComponentsBuilder
      .fromPath("/search/advanced/searching/albums")
      .queryParam("bandName", artist)
      .queryParam("releaseTitle", title)
      .build()
      .toUri();

    driver.get(uri);
  }

  private void loadArtistPage(String artistId) {
    // only artist id seems to be required,
    // artist can be empty...
    final URI uri = UriComponentsBuilder
      .fromPath("/bands/{artist}/{artistId}")
      .build("", artistId);

    driver.get(uri);
  }

  private void loadTitlePage(String titleId) {
    // only title id seems to be required,
    // artist and title can be empty...
    final URI uri = UriComponentsBuilder
      .fromPath("/albums/{artist}/{title}/{titleId}")
      .build("", "", titleId);

    driver.get(uri);
  }

  // LOADING ELEMENTS

  public String getSearchTableBody(String artist, String title) {
    loadSearchPage(artist, title);

    // find search results table body
    final WebElement tbody = findTableBody(
      By.cssSelector("#searchResultsAlbum > tbody > tr")
    );

    return tbody.getAttribute("outerHTML");
  }

  public String getSongsContainer(String titleId) {
    loadTitlePage(titleId);
    driver.waitForLoad();

    return driver.getPageSource();
  }

  public String getLyricsContainer(String titleId, String songId) {
    loadTitlePage(titleId);

    // find song table body
    final WebElement tbody = findTableBody(
      By.cssSelector(".table_lyrics > tbody > tr")
    );

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
        final WebElement lyricsElement = driver.findElement(By.xpath(
          "//td[@id='lyrics_" + songId + "' and not(text()='(loading lyrics...)')]"
        ));

        return lyricsElement.getText();
      }
    }

    // lyrics not available or instrumental
    return lastTd.getText();
  }

  private WebElement findTableBody(By tableBodyRowLocator) {
    // wait for the element
    final WebElement firstTr = driver.findElement(tableBodyRowLocator);

    // select the songs table body
    return firstTr.findElement(By.xpath("./parent::tbody"));
  }

  /**
   * Get the url where the artist logo image can be found.
   * 
   * @param artistId  the artist id
   * @return the image url
   */
  public String getArtistLogoUrl(String artistId) {
    loadArtistPage(artistId);
    driver.waitForLoad();

    final WebElement img = driver.findElement(
      By.cssSelector("#logo > img")
    );

    return img.getAttribute("src");
  }

  /**
   * Get the url where the release title cover image can be found.
   * 
   * @param titleId  the release title id
   * @return the image url
   */
  public String getTitleCoverUrl(String titleId) {
    loadTitlePage(titleId);
    driver.waitForLoad();

    final WebElement img = driver.findElement(
      By.cssSelector("#cover > img")
    );

    return img.getAttribute("src");
  }
}
