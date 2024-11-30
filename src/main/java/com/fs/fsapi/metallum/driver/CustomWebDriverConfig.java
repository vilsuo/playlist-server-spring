package com.fs.fsapi.metallum.driver;

import java.util.Collections;

import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomWebDriverConfig {
  
  @Bean
  public CustomWebDriver webDriver(@Value("${metallum.url}") String metallumUrl) {
    final ChromeOptions options = new ChromeOptions();

    // Try to bypass Cloudflare checks

    // Removes navigator.webdriver flag
    options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
    options.setExperimentalOption("useAutomationExtension", false);

    // Changing Resolution, User-Agent, and other Details
    options.addArguments("window-size=1920,1080");
    options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36");

    return new CustomWebDriver(metallumUrl, options);
  }
}
