package com.fs.fsapi.bookmark.parser;

import java.time.DateTimeException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fs.fsapi.DateTimeString;
import com.fs.fsapi.exceptions.CustomHtmlParsingException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
public class BookmarksLinkParserService {

  private final String HREF_PREFIX = "https://www.youtube.com/watch?";

  private final String VIDEO_ID_KEY_NAME = "v";

  // ".+?" = reluctant one or more times
  private final Pattern TEXT_PATTERN = Pattern.compile("(.+?) - (.+?) \\((\\d+)\\)$");

  /**
   * Create base album objects based on a extracted details about {@code a}
   * elements.
   * 
   * <ul>
   *  <li>{@code videoId} is created from element {@code href} attribute
   *  <li>
   *    {@code artist}, {@code title} and {@code published} are extracted 
   *    from the element text content according to the formula
   *    {@code artist_name - album_title (publish_year)}
   *  <li>
   *    {@code category} is set to the element parent header element text
   *    content
   *  <li>
   *    {@code addDate} is created from element {@code add_date} attribute.
   *    The value is expected to be in Unix Epoch seconds
   * </ul>
   * 
   * @param values  list of {@code a} element details
   * @return the created list of album base objects
   * 
   */
  public List<AlbumParseResult> parse(List<BookmarksLinkElement> values) {
    return values.stream()
      .map(value -> {
        final TextDetails details = extractTextContentDetails(value.getText());

        return new AlbumParseResult(
          extractVideoId(value.getHref()),
          details.getArtist(),
          details.getTitle(),
          details.getPublished(),
          value.getHeaderText(),
          parseAddDate(value.getAddDate())
        );
      })
      .collect(Collectors.toList());
  }

  /**
   * Extract the video id query paramenter value.
   * 
   * @param href  element href attribute value
   * @return the extracted query parameter value
   */
  private String extractVideoId (String href) {
    if (href == null) {
      throw new CustomHtmlParsingException(
        "The 'href' attribute is missing"
      );
    }
  
    if (!href.startsWith(HREF_PREFIX)) {
      throw new CustomHtmlParsingException(
        "Expected the 'href' attribute value '" + href
        + "' to start with '"  + HREF_PREFIX + "'"
      );
    }

    // take the query parameter key=value pairs
    String[] keyValuePairs = href.substring(HREF_PREFIX.length()).split("&");

    String keyAndValue = Arrays.stream(keyValuePairs)
      .filter(pair -> pair.startsWith(VIDEO_ID_KEY_NAME + "="))
      .findFirst()
      .orElseThrow(() -> new CustomHtmlParsingException(
        "Expected 'href' attribute value '" + href
        + "' to have a value for the query parameter '" + VIDEO_ID_KEY_NAME + "'"
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
      throw new CustomHtmlParsingException(
        "The 'add_date' attribute is missing"
      );
    }
  
    try {
      return DateTimeString.parse(addDate);

    } catch (NumberFormatException | DateTimeException e) {
      throw new CustomHtmlParsingException(
        "The 'add_date' attribute value '" + addDate + "' is invalid"
      );
    }
  };

  /**
   * Extract details from the text according to the formula
   * {@code artist_name - release_title (publish_year)}.
   * 
   * @param text  the text to extract details from
   * @return the extracted details
   */
  private TextDetails extractTextContentDetails (String text) {
    if (text == null) {
      throw new CustomHtmlParsingException(
        "The text content is missing"
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

    throw new CustomHtmlParsingException(
      "The text content '" + text + "' is invalid"
    );
  };

  @Getter
  @AllArgsConstructor
  public class TextDetails {

    final String artist;

    final String title;

    final int published;
  }
}
