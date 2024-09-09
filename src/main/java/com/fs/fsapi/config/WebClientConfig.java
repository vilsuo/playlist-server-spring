package com.fs.fsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  private final String METALLUM_BASE_URL = "https://www.metal-archives.com";

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
      .baseUrl(METALLUM_BASE_URL)
      .build();
  }
}
