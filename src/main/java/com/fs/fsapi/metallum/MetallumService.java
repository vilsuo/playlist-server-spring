package com.fs.fsapi.metallum;

import java.net.URI;
import java.net.URISyntaxException;
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
  public ArtistTitleSearchResult searchByArtistAndReleaseTitle(String artist, String title) {
    // check if cached
    var cached = cache.get(artist, title);
    if (cached.isPresent()) {
      //log.info("Cache hit!");
      //return cached.get();
    }

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

    ArtistTitleSearchResult result = parser.getSearchResult(response, artist, title);

    // update cache
    cache.put(artist, title, result);

    return result;
  }

  /**
   * Search release cover image.
   * 
   * @param id  the release id
   * @return the release cover image
   */
  public byte[] searchReleaseCover(String id) {
    return searchImage(getReleaseCoverPath(id));
  }

  /**
   * Search artist logo image.
   * 
   * @param id  the artist id
   * @return the release cover image
   */
  public byte[] searchArtistLogo(String id) {
    return searchImage(getArtistLogoPath(id));
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
   * Get the path of the release cover image. 
   * 
   * @param id  the release title id
   * @return the path of the release cover image
   */
  private String getReleaseCoverPath(String id) {
    return constructImagePath(id) + IMAGE_EXTENSION;
  }

  /**
   * Get the path of the artist logo image. 
   * 
   * @param id  the artist id
   * @return the path of the artist logo image
   */
  private String getArtistLogoPath(String id) {
    return constructImagePath(id) + "_logo" + IMAGE_EXTENSION;
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
   * @param artist  the artist name
   * @param title  the release title
   * @return  a list containing the details of each song
   * @throws URISyntaxException if the search uri is invalid
   */
  public List<SongResult> searchSongs(String artist, String title) throws URISyntaxException {
    ArtistTitleSearchResult result = searchByArtistAndReleaseTitle(artist, title);
    final String path = result.getTitleHref(); // search from release title page

    String html = webClient.get()
      .uri(new URI(path)) // ignore baseUrl
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
    final String path = "/release/ajax-view-lyrics/id/" + songId;

    String html = webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path(path)
        .build())
      .accept(MediaType.TEXT_HTML)
      .retrieve()
      .bodyToMono(String.class)
      .block();

    return html;
  }
}
