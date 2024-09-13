package com.fs.fsapi;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fs.fsapi.metallum.parser.SongResult;

@Service
public class SongClient {

  private final WebClient webClient;

  // https://stackoverflow.com/a/76352563
  public SongClient(/*@Autowired*/ WebClient.Builder webClient) {
    this.webClient = webClient.build();
  }
  
  public SongResult getSong(String id) {
    return webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/song/{id}")
        .build(id))
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(SongResult.class)
      .block();

    // only title id seems to be required,
    // artist and title can be empty...
    //return webClient.get()
    //  .uri(uriBuilder -> uriBuilder
    //    .path("/albums/{artist}/{title}/{titleId}") 
    //    .build("", "", id))
    //  .accept(MediaType.TEXT_HTML)
    //  .retrieve()
    //  .bodyToMono(SongResult.class)
    //  .block();
  }
}
