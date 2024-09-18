package com.fs.fsapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CustomWebClientConfig {

  @Value("${metallum.url:https://www.metal-archives.com}")
  private String metallumUrl;

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
      .baseUrl(metallumUrl)
      .build();
  }
}
