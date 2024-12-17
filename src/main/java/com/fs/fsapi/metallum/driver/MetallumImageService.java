package com.fs.fsapi.metallum.driver;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.fs.fsapi.exceptions.CustomMetallumException;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.result.ResultImage;

@Service
public class MetallumImageService {

  /**
   * Pattern used for finding the file-extension from url.
   */
  private static final Pattern EXTENSION_PATTERN = Pattern.compile("\\d+[^.]*\\.(\\w+)");

  public ResultImage loadImage(String imagePath) {
    try {
      final URL imageURL = new URI(imagePath).toURL();
      final BufferedImage img = ImageIO.read(imageURL);

      final String extension = getExtension(imagePath);
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(img, extension, baos);

      return new ResultImage(baos.toByteArray(), extension);

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
}
