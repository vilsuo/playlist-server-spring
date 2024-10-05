package com.fs.fsapi.metallum.client;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fs.fsapi.metallum.MetallumService;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;
import com.fs.fsapi.metallum.result.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.result.LyricsResult;
import com.fs.fsapi.metallum.result.SongResult;

import lombok.RequiredArgsConstructor;

// TODO
// - handle WebClientResponseException
// - handle not found cases, etc...

@Service
@RequiredArgsConstructor
public class MetallumClientService implements MetallumService {

  public static final String METALLUM_BASE_URL = "https://www.metal-archives.com";

  private final WebClient webClient;

  private final MetallumClientParser parser;

  private final ArtistTitleSearchCache cache;

  private final String IMAGE_EXTENSION = ".jpg";
  @Override
  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title) {
    // check if cached
    final var cached = cache.get(artist, title);
    if (cached.isPresent()) {
      //return cached.get();
    }

    final ArtistTitleSearchResponse response = webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/search/ajax-advanced/searching/albums")
        .queryParam("bandName", artist)
        .queryParam("releaseTitle", title)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(ArtistTitleSearchResponse.class)
      .block();

    final List<ArtistTitleSearchResult> results = parser.parseSearchResults(response);
    final ArtistTitleSearchResult result = results.get(0);

    // update cache
    cache.put(artist, title, result);

    // return the "best" result...
    return result;
  }

  /**
   * Search artist logo image.
   * 
   * @param id  the artist id
   * @return the image
   */
  public byte[] searchArtistLogo(String id) {
    return searchImage(getArtistLogoPath(id));
  }

  /**
   * Create url where the artist logo image can be found.
   * 
   * @param id  the artist id
   * @return the image url
   */
  public String createArtistLogoUrl(String id) {
    return METALLUM_BASE_URL + getArtistLogoPath(id);
  }

  /**
   * Search release title cover image.
   * 
   * @param id  the release title id
   * @return the image
   */
  public byte[] searchTitleCover(String id) {
    return searchImage(getTitleCoverPath(id));
  }

  /**
   * Create url where the release title cover image can be found.
   * 
   * @param id  the release title id
   * @return the image url
   */
  public String createTitleCoverUrl(String id) {
    return METALLUM_BASE_URL + getTitleCoverPath(id);
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
   * Get the path of the artist logo image. 
   * 
   * @param id  the artist id
   * @return the image
   */
  private String getArtistLogoPath(String id) {
    return constructImagePath(id) + "_logo" + IMAGE_EXTENSION;
  }

  /**
   * Get the path of the release title cover image. 
   * 
   * @param id  the release title id
   * @return the path image
   */
  private String getTitleCoverPath(String id) {
    return constructImagePath(id) + IMAGE_EXTENSION;
  }

  /**
   * Construct base image path with missing extension. Example for
   * {@code id} <pre>"528471"</pre> the resulting path will be
   * <pre>"/images/5/2/8/4/528471"</pre>.
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

  @Override
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

  // title id not needed
  @Override
  public LyricsResult searchSongLyrics(String titleId, String songId) {
    final String html = webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/release/ajax-view-lyrics/id/{songId}")
        .build(songId))
      .accept(MediaType.TEXT_HTML)
      .retrieve()
      .bodyToMono(String.class)
      .block();

    return parser.parseLyrics(html);
  }
}
