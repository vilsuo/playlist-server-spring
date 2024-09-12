package com.fs.fsapi.metallum;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.parser.MetallumParser;
import com.fs.fsapi.metallum.parser.SongResult;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO
// - handle WebClientResponseException
// - handle not found cases, etc...

@Slf4j
@Service
@RequiredArgsConstructor
public class MetallumService {

  private final WebClient webClient;

  private final MetallumParser parser;

  private final ArtistTitleSearchCache cache;

  private final String IMAGE_EXTENSION = ".jpg"; // always?
  
  /**
   * Search basic release information. Contains links for the artist page and
   * the release title page. Caches results to increase performance.
   * 
   * @param artist  the artist name
   * @param title  the release title
   * @return basic search result
   */
  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title) {
    // check if cached
    var cached = cache.get(artist, title);
    if (cached.isPresent()) {
      //log.info("Cache hit!");
      //return cached.get();
    }

    ArtistTitleSearchResponse response = webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/search/ajax-advanced/searching/albums")
        .queryParam("bandName", artist)
        .queryParam("releaseTitle", title)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(ArtistTitleSearchResponse.class)
      .block();

    ArtistTitleSearchResult result = parser.getSearchResult(response, artist, title);

    // update cache
    cache.put(artist, title, result);

    return result;
  }

  /**
   * Search release title cover image.
   * 
   * @param id  the release title id
   * @return the image
   */
  public byte[] searchTitleCover(String id) {
    return searchImage(constructImagePath(id) + IMAGE_EXTENSION);
  }

  /**
   * Search artist logo image.
   * 
   * @param id  the artist id
   * @return the image
   */
  public byte[] searchArtistLogo(String id) {
    return searchImage(constructImagePath(id) + "_logo" + IMAGE_EXTENSION);
  }

  private byte[] searchImage(String imagePath) {
    return webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path(imagePath)
        .build())
      .accept(MediaType.IMAGE_JPEG)
      .retrieve()
      .bodyToMono(byte[].class)
      .block();
  }

  /**
   * Construct base image path. Example for {@code id}
   * <pre>"528471"</pre> the resulting path will be
   * <pre>"https://www.metal-archives.com/images/5/2/8/4/528471"</pre>.
   * 
   * @param id  resource id
   * @return the base path of a image resource
   */
  private String constructImagePath(String id) {
    final String basePathSegment = "/images";
    
    // middle part of the url seems to consist of max first four integers
    // from the last value (id) separated by '/'
    final int PARTS = Math.min(4, id.length());
    final int PIECES = 2 * PARTS - 1;
    final char[] inArr = id.toCharArray();
    final char[] outArr = new char[PIECES];
    for (int i = 0; i < PIECES; i++) {
      outArr[i] = (i % 2 == 0) ? inArr[i / 2] : '/';
    }

    final String middlePathSegments = new String(outArr);

    return String.join("/", new String[]{ basePathSegment, middlePathSegments, id });
  }

  /**
   * Search songs by artist name and release title.
   * 
   * @param titleId  the release title id
   * @return  a list containing the details of each song
   * @implNote {@code path} songs are search from release title page, same as
   * {@link ArtistTitleSearchResult#getTitleHref()}
   */
  public List<SongResult> searchSongs(String titleId) {
    // only title id seems to be required,
    // artist and title can be empty...
    final String html = webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/albums/{artist}/{title}/{titleId}") 
        .build("", "", titleId))
      .accept(MediaType.TEXT_HTML)
      .retrieve()
      .bodyToMono(String.class)
      .block();

    return parser.parseSongs(html);
  }

  /**
   * Search song lyrics by song id.
   * 
   * @param songId  the song id
   * @return html string containing the lyrics, or html string describing
   *         the lyrics were not found
   */
  public String searchSongLyrics(String songId) {
    return webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/release/ajax-view-lyrics/id/{songId}")
        .build(songId))
      .accept(MediaType.TEXT_HTML)
      .retrieve()
      .bodyToMono(String.class)
      .block();
  }
}
