package com.fs.fsapi.metallum.driver;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CustomChromeDriver extends ChromeDriver {

  public static final Duration DEFAULT_TIMEOUT_SECONDS = Duration.ofSeconds(20);

  private Duration timeout = DEFAULT_TIMEOUT_SECONDS;

  public CustomChromeDriver(ChromeOptions options) {
    super(options);
    this.manage().timeouts().implicitlyWait(timeout);
  }

  //public Duration getTimeout() {
  //  return timeout;
  //}
//
  //public void setTimeout(Duration timeout) {
  //  this.timeout = timeout;
  //}

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

  private void waitForLoad() {
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
    this.manage().timeouts().implicitlyWait(Duration.ofMillis(100));
  }
}
