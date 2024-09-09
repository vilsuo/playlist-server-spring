package com.fs.fsapi.metallum;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fs.fsapi.metallum.parser.MetallumParser;
import com.fs.fsapi.metallum.parser.SongResult;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetallumService {

  private final WebClient webClient;

  private final MetallumParser parser;
  
  public ArtistTitleSearchResult searchWithArtistAndTitle(String artist, String title) {
    ArtistTitleSearchResponse response = webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/search/ajax-advanced/searching/albums/")
        .queryParam("bandName", artist)
        .queryParam("releaseTitle", title)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(ArtistTitleSearchResponse.class)
      .block();

    return parser.getSearchResult(response, artist, title);
  }

  public byte[] searchCover(String artist, String title) {
    ArtistTitleSearchResult result = searchWithArtistAndTitle(artist, title);
    final String path = getCoverPath(result.getTitleHref());

    byte[] image = webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path(path)
        .build())
      .accept(MediaType.IMAGE_JPEG)
      .retrieve()
      .bodyToMono(byte[].class)
      .block();

    return image;
  }

  private String getCoverPath(String titleHref) {
    final String baseUrl = "/images";

    // final part of url
    final String id = titleHref.substring(titleHref.lastIndexOf("/") + 1);
    
    // middle part of the url seems to consist of max first four integers
    // from the last value (id) separated by '/'
    final int PARTS = Math.min(4, id.length());
    final int PIECES = 2 * PARTS - 1;
    final char[] inaArr = id.toCharArray();
    final char[] outArr = new char[PIECES];
    for (int i = 0; i < PIECES; i++) {
      outArr[i] = (i % 2 == 0) ? inaArr[i / 2] : '/';
    }

    final String middle = new String(outArr);

    // always jpg?
    final String extension = ".jpg";
    return String.join("/", new String[]{ baseUrl, middle, id }) + extension;
  }

  public List<SongResult> searchSongs(String artist, String title) throws URISyntaxException {
    ArtistTitleSearchResult result = searchWithArtistAndTitle(artist, title);
    final String path = result.getTitleHref();

    String html = webClient.get()
      .uri(new URI(path)) // ignore baseUrl
      .accept(MediaType.TEXT_HTML)
      .retrieve()
      .bodyToMono(String.class)
      .block();

    return parser.getSongs(html);
  }
}
