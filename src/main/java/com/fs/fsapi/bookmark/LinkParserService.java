package com.fs.fsapi.bookmark;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
public class LinkParserService {

  private final String HREF_PREFIX = "https://www.youtube.com/watch?";

  private final String KEY_NAME = "v";

  // reluctant one or more times
  private final Pattern TEXT_PATTERN = Pattern.compile("(.+?) - (.+?) \\((\\d+)\\)$");

  public List<AlbumBase> createAlbumBases(List<FolderLink> folderLinks) {
    return folderLinks.stream()
      .map(folderLink -> {
        TextDetails details = extractTextContentDetails(folderLink.getText());

        return new AlbumBase(
          extractVideoId(folderLink.getHref()),
          details.getArtist(),
          details.getTitle(),
          details.getPublished(),
          folderLink.getFolder(),
          parseAddDate(folderLink.getAddDate())
        );
      })
      .collect(Collectors.toList());
  }

  /**
   * Extract the query paramenter {@link LinkParserService#KEY_NAME} value
   * from an URL starting with {@link LinkParserService#HREF_PREFIX}.
   * 
   * @param href Link element href attribute value
   * @return the query parameter value
   */
  private String extractVideoId (String href) {
    if (href == null || href.isEmpty()) {
      throw new RuntimeException("Link href attribute is missing");
    }
  
    if (!href.startsWith(HREF_PREFIX)) {
      throw new RuntimeException("Link href attribute is not youtube");
    }

    // take the query parameter key=value pairs
    String[] keyValuePairs = href.substring(HREF_PREFIX.length()).split("&");

    String keyAndValue = Arrays.stream(keyValuePairs)
      .filter(pair -> pair.startsWith(KEY_NAME + "="))
      .findFirst()
      .orElseThrow(() -> new RuntimeException(
        "Link href '" + href + "' does not contain a query parameter '"
        + KEY_NAME + "' value"
      ));

    // return the value of the key and value pair
    return keyAndValue.split("=", 2)[1];
  };

  /**
   * Convert Epoch seconds to date representation in ISO-8601 format
   * @param addDate UTC seconds string
   * @return ISO-8601 date string
   */
  private String parseAddDate(String addDate) {
    if (addDate == null || addDate.isEmpty()) {
      throw new RuntimeException("Link add_date attribute is missing");
    }
  
    try {
      int utcSeconds = Integer.parseInt(addDate);
      return Instant.ofEpochSecond(utcSeconds).toString();

    } catch (NumberFormatException e) {
      throw new RuntimeException("Link add date '" + addDate + "' is not a valid number");
    }
  };

  private TextDetails extractTextContentDetails (String text) {
    Matcher m = TEXT_PATTERN.matcher(text);
    
    if (m.matches()) {
      return new TextDetails(
        m.group(1),
        m.group(2),
        Integer.parseInt(m.group(3))
      );
    }

    throw new RuntimeException(
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
}
