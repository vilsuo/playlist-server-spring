package com.fs.fsapi.bookmark;

import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
public class LinkParserService {

  private final String HREF_PREFIX = "https://www.youtube.com/watch?v=";

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

  private String extractVideoId (String href) {
    if (href == null) {
      throw new RuntimeException("Link href attribute is missing");
    }
  
    if (!href.startsWith(HREF_PREFIX)) {
      throw new RuntimeException("Link href attribute is not youtube");
    }

    // return the first query parameter "v" value
    return href.substring(HREF_PREFIX.length()).split("&")[0];
  };

  /**
   * Convert Epoch seconds to date representation in ISO-8601 format
   * @param addDate UTC seconds string
   * @return ISO-8601 date string
   */
  private String parseAddDate(String addDate) {
    if (addDate.isBlank()) {
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
