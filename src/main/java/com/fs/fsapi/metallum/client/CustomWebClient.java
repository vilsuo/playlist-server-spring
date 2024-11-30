package com.fs.fsapi.metallum.client;

import org.springframework.web.reactive.function.client.WebClient;

public class CustomWebClient {
  
  private final WebClient client;

  private final String BASE_URL;

  public CustomWebClient(String baseUrl, WebClient client) {
    this.client = client;
    this.BASE_URL = baseUrl;
  }

  public org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec<?> get() {
    return client.get();
  }

  public String getBaseUrl() {
    return this.BASE_URL;
  }
}
