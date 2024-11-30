package com.fs.fsapi.metallum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CustomWebClientConfig {

  @Bean
  public CustomWebClient webClient(@Value("${metallum.url}") String metallumUrl) {
    return new CustomWebClient(metallumUrl, WebClient.builder()
      .baseUrl(metallumUrl)
      .build()
    );
  }
}
