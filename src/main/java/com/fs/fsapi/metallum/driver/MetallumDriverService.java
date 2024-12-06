package com.fs.fsapi.metallum.driver;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.fs.fsapi.exceptions.CustomMetallumException;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.MetallumImage;
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

  /**
   * Pattern used for finding the file-extension from url.
   */
  private static final Pattern EXTENSION_PATTERN = Pattern.compile("\\d+[^.]*\\.(\\w+)");

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
  public MetallumImage searchArtistLogo(String artistId) {
    return loadImage(driver.getArtistLogoUrl(artistId));
  }

  @Override
  public MetallumImage searchTitleCover(String titleId) {
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

  private MetallumImage loadImage(String imagePath) {
    try {
      final URL imageURL = new URI(imagePath).toURL();
      final BufferedImage img = ImageIO.read(imageURL);

      final String extension = getExtension(imagePath);
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(img, extension, baos);

      return new MetallumImage(baos.toByteArray(), extension);

    } catch (URISyntaxException e) {
      throw new CustomMetallumException("Invalid image path '" + imagePath + "'");

    } catch (IOException e) {
      throw new RuntimeException("Error reading or writing an image", e);
    }
  }

  private final String getExtension(String imagePath) {
    final Matcher m = EXTENSION_PATTERN.matcher(imagePath);
    if (!m.find()) {
      throw new CustomMetallumScrapingException(
        "Did not find extension from '" + imagePath + "'"
      );
    }
    return m.group(1).toLowerCase();
  }

  public void setBypassCookie(String value) {
    driver.setBypassCookie(value);
  }
}
