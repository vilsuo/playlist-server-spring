package com.fs.fsapi.bookmark.parser;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fs.fsapi.exceptions.CustomHtmlParsingException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
public class LinkParserService {

  private final String HREF_PREFIX = "https://www.youtube.com/watch?";

  private final String VIDEO_ID_KEY_NAME = "v";

  // ".+?" = reluctant one or more times
  private final Pattern TEXT_PATTERN = Pattern.compile("(.+?) - (.+?) \\((\\d+)\\)$");

  /**
   * Create base album objects from a list of html elements with their
   * associated folder name.
   * 
   * <ul>
   *  <li>{@code videoId} is created from element {@code href} attribute
   *  <li>
   *    {@code artist}, {@code title} and {@code published} are extracted 
   *    from the element text content according to the formula
   *    {@code artist_name - album_title (publish_year)}
   *  <li>{@code category} is set to the {@code folderName}
   *  <li>
   *    {@code addDate} is created from element {@code add_date} attribute.
   *    The value is expected to be in Unix Epoch seconds
   * </ul>
   * 
   * @param folderLinks  list of html elements with their associated folder name
   * @return the created list of album base objects
   * 
   */
  public List<AlbumBase> createAlbumBases(List<FolderLink> folderLinks) {
    return folderLinks.stream()
      .map(folderLink -> {
        try {
          final TextDetails details = extractTextContentDetails(folderLink.getText());

          return new AlbumBase(
            extractVideoId(folderLink.getHref()),
            details.getArtist(),
            details.getTitle(),
            details.getPublished(),
            folderLink.getFolderName(),
            parseAddDate(folderLink.getAddDate())
          );
          
        } catch (CustomLinkParsingException ex) {
          throw new CustomHtmlParsingException(
            ex.getMessage(),
            folderLink.getElement()
          );
        }
      })
      .collect(Collectors.toList());
  }

  /**
   * Extract the query paramenter {@link LinkParserService#VIDEO_ID_KEY_NAME} value
   * from an URL starting with {@link LinkParserService#HREF_PREFIX}.
   * 
   * @param href  Link element href attribute value
   * @return the extracted query parameter value
   */
  private String extractVideoId (String href) {
    if (href == null) {
      throw new CustomLinkParsingException(
        "Link 'href' attribute is missing"
      );
    }
  
    if (!href.startsWith(HREF_PREFIX)) {
      throw new CustomLinkParsingException(
        "Link href attribute '" + href
        + "' must start with the prefix '" + HREF_PREFIX + "'"
      );
    }

    // take the query parameter key=value pairs
    String[] keyValuePairs = href.substring(HREF_PREFIX.length()).split("&");

    String keyAndValue = Arrays.stream(keyValuePairs)
      .filter(pair -> pair.startsWith(VIDEO_ID_KEY_NAME + "="))
      .findFirst()
      .orElseThrow(() -> new CustomLinkParsingException(
        "Link href attribute '" + href
        + "' is missing a required query parameter '" + VIDEO_ID_KEY_NAME + "'"
      ));

    // return the value of the key and value pair
    return keyAndValue.split("=", 2)[1];
  };

  /**
   * Convert Epoch seconds to date representation in ISO-8601 format
   * 
   * @param addDate  UTC seconds string
   * @return ISO-8601 date string
   */
  private String parseAddDate(String addDate) {
    if (addDate == null) {
      throw new CustomLinkParsingException(
        "Link 'add_date' attribute is missing"
      );
    }
  
    try {
      int utcSeconds = Integer.parseInt(addDate);
      return Instant.ofEpochSecond(utcSeconds).toString();

    } catch (NumberFormatException e) {
      throw new CustomLinkParsingException(
        "Link add date attribute '" + addDate + "' is not a valid number"
      );
    }
  };

  /**
   * Extract details from the text according to the formula
   * {@code artist_name - album_title (publish_year)}.
   * 
   * @param text  the text to extract details from
   * @return the extracted details
   */
  private TextDetails extractTextContentDetails (String text) {
    if (text == null) {
      throw new CustomLinkParsingException(
        "Link text content is missing"
      );
    }

    Matcher m = TEXT_PATTERN.matcher(text);
    
    if (m.matches()) {
      return new TextDetails(
        m.group(1),
        m.group(2),
        Integer.parseInt(m.group(3))
      );
    }

    throw new CustomLinkParsingException(
      "Link text content '" + text + "' is in incorrect format"
    );
  };

  @Getter
  @AllArgsConstructor
  public class TextDetails {

    final String artist;

    final String title;

    final int published;
  }

  @Getter
  public class CustomLinkParsingException extends RuntimeException {

    public CustomLinkParsingException(String message) {
      super(message);
    }
  }

}
