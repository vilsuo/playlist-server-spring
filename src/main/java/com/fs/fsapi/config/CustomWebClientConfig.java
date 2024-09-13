package com.fs.fsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.fs.fsapi.metallum.MetallumService;

@Configuration
public class CustomWebClientConfig {

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder().baseUrl(MetallumService.METALLUM_BASE_URL);
  }

}
