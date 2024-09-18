package com.fs.fsapi.config;

import java.time.Duration;
import java.util.Collections;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomWebDriverConfig {
  
  @Bean
  public WebDriver webDriver() {
    ChromeOptions options = new ChromeOptions();

    // Try to bypass Cloudflare checks

    // 1. Removes navigator.webdriver flag
    // - for ChromeDriver under version 79.0.3945.16
    options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
    options.setExperimentalOption("useAutomationExtension", false);
    // - for ChromeDriver version 79.0.3945.16 or over?
    //options.addArguments("--disable-blink-features=AutomationControlled");

    // 3. Changing Resolution, User-Agent, and other Details
    options.addArguments("window-size=1920,1080");
    options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36");

    WebDriver driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

    return driver;
  }
}
