package com.fs.fsapi.metallum.driver;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.fs.fsapi.exceptions.CustomMetallumException;
import com.fs.fsapi.metallum.base.MetallumService;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.result.ResultRanker;
import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetallumDriverService implements MetallumService {

  private final MetallumDriver driver;

  private final MetallumDriverParser parser;

  private final ArtistTitleSearchCache cache;

  private final ResultRanker ranker;

  @Override
  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title) {
    return cache.getOrElseSupply(artist, title, () -> {
      final String response = driver.getSearchTableBody(artist, title);
      final List<ArtistTitleSearchResult> results = parser.parseSearchResults(response);
      
      // find the "best" result
      final ArtistTitleSearchResult result = ranker
        .getBestSearchResult(results, artist, title);

      // update cache
      cache.put(artist, title, result);

      return result;
    });
  }

  @Override
  public List<SongResult> searchSongs(String titleId) {
    return parser.parseSongs(driver.getSongsContainer(titleId));
  }

  @Override
  public LyricsResult searchSongLyrics(String titleId, String songId) {
    return parser.parseLyrics(driver.getLyricsContainer(titleId, songId));
  }

  @Override
  public byte[] searchArtistLogo(String artistId) {
    return loadImage(driver.getArtistLogoUrl(artistId));
  }

  @Override
  public byte[] searchTitleCover(String titleId) {
    return loadImage(driver.getTitleCoverUrl(titleId));
	}

  @Override
  public String getArtistLogoUrl(String id) {
    return driver.getArtistLogoUrl(id);
  }

  @Override
  public String getTitleCoverUrl(String id) {
    return driver.getTitleCoverUrl(id);
  }

  private byte[] loadImage(String imagePath) {
    try {
      final URL imageURL = new URI(imagePath).toURL();
      final BufferedImage img = ImageIO.read(imageURL);

      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(img, "jpg", baos);
      return baos.toByteArray();

    } catch (URISyntaxException e) {
      throw new CustomMetallumException("Invalid image path '" + imagePath + "'");

    } catch (IOException e) {
      throw new RuntimeException("This should never happen", e);
    }
  }

  public void setBypassCookie(String value) {
    driver.setBypassCookie(value);
  }
}
