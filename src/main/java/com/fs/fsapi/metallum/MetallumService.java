package com.fs.fsapi.metallum;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fs.fsapi.metallum.cache.ArtistReleaseSearchCache;
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

  private final ArtistReleaseSearchCache cache;
  
  /**
   * Search basic release information. Caches results.
   * 
   * @param artist  the artist name
   * @param title  the release title
   * @return result containing 
   */
  public ArtistTitleSearchResult searchByArtistAndReleaseTitle(String artist, String title) {
    // check if cached
    var cached = cache.get(artist, title);
    if (cached.isPresent()) {
      log.info("Cache hit!");
      return cached.get();
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
   * Search release cover image by artist name and release title.
   * 
   * @param artist  the artist name
   * @param title  the release title
   * @return the release cover
   */
  public byte[] searchCover(String artist, String title) {
    ArtistTitleSearchResult result = searchByArtistAndReleaseTitle(artist, title);
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

  /**
   * Get the path of the release cover image. Example, if {@code titleHref} is
   * 
   * <pre>"https://www.metal-archives.com/albums/Adramelech/Psychostasia/6516"</pre>,
   * then resulting path will be
   * <pre>"https://www.metal-archives.com/images/6/5/1/6/6516.jpg"</pre>.
   * 
   * @param titleHref  the release page uri
   * @return the path of the release cover image
   */
  private String getCoverPath(String titleHref) {
    final String basePathSegment = "/images";

    // final path segment
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

    final String middlePathSegments = new String(outArr);

    // always jpg?
    final String extension = ".jpg";
    return String.join("/", new String[]{ basePathSegment, middlePathSegments, id }) + extension;
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
  public String searchLyrics(String songId) {
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
