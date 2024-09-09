package com.fs.fsapi.metallum;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fs.fsapi.bookmark.parser.HtmlParserService;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO create CustomMetallumException.class

@Slf4j
@Service
@RequiredArgsConstructor
public class MetallumService {

  private final WebClient webClient;

  private final HtmlParserService parser;
  
  public SearchLink getSearchResultsWeb(String artist, String title) {
    ArtistTitleSearchResponse results = webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/search/ajax-advanced/searching/albums/")
        .queryParam("bandName", artist)
        .queryParam("releaseTitle", title)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(ArtistTitleSearchResponse.class)
      .block();

    if (!results.getError().isBlank()) {
      log.error(
        "Error " + results.getError(), 
        new RuntimeException("Error finding album link")
      );
    }

    switch (results.getITotalRecords()) {
      case 0:
        throw new CustomDataNotFoundException(
          "Album '" + title + "' by '" + artist + "' was not found"
        );

      case 1:
        // return the only result
        return createSearchLink(results.getAaData().getFirst());

      default:
        // return the first result
        log.error(
          "Error found multiple results", 
          new RuntimeException("Error found multiple results")
        );

        return createSearchLink(results.getAaData().getFirst());
    }
  }

  public SearchLink createSearchLink(AaDataValue data) {
    return new SearchLink(
      parser.createLink(data.getArtistLinkElementString()),
      parser. createLink(data.getTitleLinkElementString())
      //data.getAlbumType()
    );
  }
}
