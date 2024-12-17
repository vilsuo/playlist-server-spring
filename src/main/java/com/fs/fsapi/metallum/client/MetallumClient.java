package com.fs.fsapi.metallum.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fs.fsapi.metallum.base.MetallumWebInterface;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;
import com.fs.fsapi.metallum.result.ResultImage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetallumClient implements MetallumWebInterface
  <ArtistTitleSearchResponse, String, String>
{
  
  private final CustomWebClient client;

  private final String IMAGE_EXTENSION = "jpg";

  @Override
  public ArtistTitleSearchResponse getSearchResponse(String artist, String title) {
    return client.get()
      .uri(uriBuilder -> uriBuilder
        .path("/search/ajax-advanced/searching/albums/") // the actual path ends in '/'
        .queryParam("bandName", artist)
        .queryParam("releaseTitle", title)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(ArtistTitleSearchResponse.class)
      .block();
  }

  @Override
  public String getSongs(String titleId) {
    return client.get()
      .uri(uriBuilder -> uriBuilder
        .path("/albums/{artist}/{title}/{titleId}") 
        .build("", "", titleId)) // artist and title can be empty
      .accept(MediaType.TEXT_HTML)
      .retrieve()
      .bodyToMono(String.class)
      .block();
  }

  @Override
  public String getSongLyrics(String titleId, String songId) {
    return client.get()
      .uri(uriBuilder -> uriBuilder
        .path("/release/ajax-view-lyrics/id/{songId}")
        .build(songId))
      .accept(MediaType.TEXT_HTML)
      .retrieve()
      .bodyToMono(String.class)
      .block();
  }

  @Override
  public ResultImage getArtistLogo(String artistId) {
    return loadImage(createArtistLogoPath(artistId));
  }

  @Override
  public String getArtistLogoUrl(String artistId) {
    return client.getBaseUrl() + createArtistLogoPath(artistId);
  }

  private String createArtistLogoPath(String artistId) {
    return createBaseImagePath(artistId) + "_logo." + IMAGE_EXTENSION;
  }

  @Override
  public ResultImage getTitleCover(String titleId) {
    return loadImage(createTitleCoverPath(titleId));
  }

  @Override
  public String getTitleCoverUrl(String titleId) {
    return client.getBaseUrl() + createTitleCoverPath(titleId);
  }

  private String createTitleCoverPath(String titleId) {
    return createBaseImagePath(titleId) + "." + IMAGE_EXTENSION;
  }

  private ResultImage loadImage(String imagePath) {
    final ResponseEntity<byte[]> response = client.get()
      .uri(uriBuilder -> uriBuilder
        .path(imagePath)
        .build())
      .header("Accept", "image/*")
      .retrieve()
      .toEntity(byte[].class)
      .block();

    return new ResultImage(
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
