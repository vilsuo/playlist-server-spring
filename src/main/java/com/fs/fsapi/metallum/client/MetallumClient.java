package com.fs.fsapi.metallum.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fs.fsapi.metallum.MetallumImage;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetallumClient {
  
  private final CustomWebClient client;

  private final String IMAGE_EXTENSION = "jpg";

  /**
   * 
   * @param artist
   * @param title
   * @return
   */
  public ArtistTitleSearchResponse loadSearchResponse(String artist, String title) {
    return client.get()
      .uri(uriBuilder -> uriBuilder
        .path("/search/ajax-advanced/searching/albums/") // actual has the '/' in the end
        .queryParam("bandName", artist)
        .queryParam("releaseTitle", title)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(ArtistTitleSearchResponse.class)
      .block();
  }

  /**
   * 
   * @param titleId
   * @return
   */
  public String loadSongs(String titleId) {
    return client.get()
      .uri(uriBuilder -> uriBuilder
        .path("/albums/{artist}/{title}/{titleId}") 
        .build("", "", titleId)) // artist and title can be empty
      .accept(MediaType.TEXT_HTML)
      .retrieve()
      .bodyToMono(String.class)
      .block();
  }

  /**
   * 
   * @param songId
   * @return
   */
  public String loadSongLyrics(String songId) {
    return client.get()
      .uri(uriBuilder -> uriBuilder
        .path("/release/ajax-view-lyrics/id/{songId}")
        .build(songId))
      .accept(MediaType.TEXT_HTML)
      .retrieve()
      .bodyToMono(String.class)
      .block();
  }

  /**
   * Search artist logo image.
   * 
   * @param artistId  the artist id
   * @return the image
   */
  public MetallumImage loadArtistLogo(String artistId) {
    return loadImage(createArtistLogoPath(artistId));
  }

  /**
   * Create the url where the artist logo image can be found.
   * 
   * @param artistId  the artist id
   * @return the image url
   */
  public String createArtistLogoUrl(String artistId) {
    return client.getBaseUrl() + createArtistLogoPath(artistId);
  }

  /**
   * Get the path of the artist logo image. 
   * 
   * @param artistId  the artist id
   * @return the image
   */
  private String createArtistLogoPath(String artistId) {
    return createBaseImagePath(artistId) + "_logo." + IMAGE_EXTENSION;
  }

  /**
   * Search release title cover image.
   * 
   * @param titleId  the release title id
   * @return the image
   */
  public MetallumImage loadTitleCover(String titleId) {
    return loadImage(createTitleCoverPath(titleId));
  }

  /**
   * Create the url where the release title cover image can be found.
   * 
   * @param titleId  the release title id
   * @return the image url
   */
  public String createTitleCoverUrl(String titleId) {
    return client.getBaseUrl() + createTitleCoverPath(titleId);
  }

  /**
   * Get the path of the release title cover image. 
   * 
   * @param titleId  the release title id
   * @return the path image
   */
  private String createTitleCoverPath(String titleId) {
    return createBaseImagePath(titleId) + "." + IMAGE_EXTENSION;
  }

  private MetallumImage loadImage(String imagePath) {
    final ResponseEntity<byte[]> response = client.get()
      .uri(uriBuilder -> uriBuilder
        .path(imagePath)
        .build())
      .header("Accept", "image/*")
      .retrieve()
      .toEntity(byte[].class)
      .block();

    return new MetallumImage(
      response.getBody(),
      response.getHeaders().getContentType()
    );
  }

  /**
   * Construct base image path with missing extension. Example for
   * {@code id} <pre>"528471"</pre> the resulting path will be
   * <pre>"/images/5/2/8/4/528471"</pre>.
   * 
   * @param id  resource id
   * @return the base path of a image resource
   */
  private String createBaseImagePath(String id) {
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
}
