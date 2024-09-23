package com.fs.fsapi.metallum.driver;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CustomChromeDriver extends ChromeDriver {

  // TIMEOUTS
  private final Duration DEFAULT_TIMEOUT_SECONDS = Duration.ofSeconds(15);
  private Duration timeout = DEFAULT_TIMEOUT_SECONDS;

  // COOKIES
  private final String COOKIE_NAME = "cf_clearance";
  private final String COOKIE_DOMAIN = "www.metal-archives.com";
  private final String DEFAULT_COOKIE_VALUE = "MSmv8ktFlsRVVJVOAuxKdxlzUkdr87cvyv6KBDGmGI4-1726663642-1.2.1.1-tkKDIfpP63429xOiaE4spluoP1yS0CV46K_xrlcXPtzNhUrnn41ILUrPTb1woMdTKpJSb1bT8Et66bMaUZaLkVsjY0Uf0LhXqs0dgmJEiLV0w72mLZzzd.uHjKIW8CXX15YaH6AS.H2QuwT__DpZAveg2gKt__MYPfRSphLApHFeASxnNZoUuzhhA7AuWCrquTuXDD8JMq6G7E7W7qHz8iH5wUrdhTLK89cCWtbNyo1rL2uB627GdI72217jav.EVHh7fyOiqfR7by9.PZZWo2jWXy05LaB6L72Fb71ob20hFBsh4CSNRZS8LHMI5z_tJri04bcmHDFJpQ9Ib8T7.XGeYI5T9h_YNr9aFu1m91PobYQLz05njTFH2SgsOnkDa9wwrN0Za7RGArBkseSoXoAe.UZjDUdItiHlSPMQijO8cr_9a_ObIMq1vIfWXD70";
  private String cookieValue = DEFAULT_COOKIE_VALUE;

  public CustomChromeDriver(ChromeOptions options) {
    super(options);
    manage().timeouts().implicitlyWait(timeout);

    // Visit so cookie can be added
    super.get("https://" + COOKIE_DOMAIN);
    manage().addCookie(createCookie(cookieValue));
  }

  private Cookie createCookie(String value) {
    return new Cookie(
      COOKIE_NAME,
      value,
      COOKIE_DOMAIN,
      "/",
      null
    );
  }

  public void replaceCookie(String value) {
    cookieValue = value;

    // Visit so cookie can be replaced
    super.get("https://" + COOKIE_DOMAIN);
    manage().deleteCookieNamed(COOKIE_NAME);
    manage().addCookie(createCookie(cookieValue) );
  }

  public int childrenCount(WebElement element) {
    // Lower implicitly wait time
    manage().timeouts().implicitlyWait(Duration.ofMillis(100));

    List<WebElement> children = element.findElements(By.xpath(
      "child::*"
    ));

    // Rise back implicitly wait time
    manage().timeouts().implicitlyWait(timeout);

    return children.size();
  }

  /*
  public boolean isElementPresent(SearchContext context, By by) {
    boolean isPresent = true;
    waitForLoad();
    // Search for elements and check if list is empty
    if (context.findElements(by).isEmpty()) {
      isPresent = false;
    }

    // Rise back implicitly wait time
    this.manage().timeouts().implicitlyWait(timeout);
    return isPresent;
  }

  public boolean isElementPresent(By by) {
    return isElementPresent(this, by);
  }
  */

  public void get(URI uri) {
    super.get(uri.toString());
  }

  public void waitForLoad() {
    ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver wd) {
        // This will tel if page is loaded
        return "complete".equals(((JavascriptExecutor) wd).executeScript("return document.readyState"));
      }
    };

    WebDriverWait wait = new WebDriverWait(this, timeout);

    // Wait for page complete
    wait.until(pageLoadCondition);

    // Lower implicitly wait time
    //this.manage().timeouts().implicitlyWait(Duration.ofMillis(100));
  }
}
