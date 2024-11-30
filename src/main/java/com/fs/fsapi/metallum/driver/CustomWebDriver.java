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

public class CustomWebDriver {

  private final ChromeDriver driver;

  private final String BASE_URL;

  private final Duration DEFAULT_TIMEOUT_SECONDS = Duration.ofSeconds(15);

  private Duration timeout;

  public CustomWebDriver(String baseUrl, ChromeOptions options) {
    this.driver = new ChromeDriver(options);

    this.BASE_URL = baseUrl;

    this.timeout = DEFAULT_TIMEOUT_SECONDS;
    implicitlyWait(timeout);
  }

  public void setBaseUrlCookie(String name, String value) {
    // Visit so cookie can be replaced
    driver.get(BASE_URL);
    driver.manage().deleteCookieNamed(name);

    final Cookie cookie = new Cookie(
      name,
      value,
      BASE_URL.split("://")[1], // domain
      "/",
      null
    );

    driver.manage().addCookie(cookie);
  }

  public int childrenCount(WebElement element) {
    // Lower implicitly wait time
    implicitlyWait(Duration.ofMillis(100));

    final List<WebElement> children = element.findElements(
      By.xpath("child::*")
    );

    // Rise back implicitly wait time
    implicitlyWait(timeout);

    return children.size();
  }

  public void get(URI uri) {
    driver.get(BASE_URL + uri.toString());
  }

  public String getPageSource() {
    return driver.getPageSource();
  }

  public WebElement findElement(By locator) {
    return this.driver.findElement(locator);
  }

  public void waitForLoad() {
    final ExpectedCondition<Boolean> pageLoadCondition
      = new ExpectedCondition<Boolean>() {

        public Boolean apply(WebDriver wd) {
          // This will tell if page is loaded
          return "complete"
            .equals(((JavascriptExecutor) wd)
            .executeScript("return document.readyState"));
        }
      };

    final WebDriverWait wait = new WebDriverWait(driver, timeout);

    // Wait for page complete
    wait.until(pageLoadCondition);
  }

  private void implicitlyWait(Duration duration) {
    driver.manage().timeouts().implicitlyWait(duration);
  }
}
