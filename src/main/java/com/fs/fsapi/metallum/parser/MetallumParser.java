package com.fs.fsapi.metallum.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fs.fsapi.bookmark.parser.LinkElement;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomHtmlParsingException;
import com.fs.fsapi.exceptions.CustomMetallumException;
import com.fs.fsapi.metallum.ArtistReleaseSearchResult;
import com.fs.fsapi.metallum.response.AaDataValue;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MetallumParser {

  public ArtistReleaseSearchResult getSearchResult(
    ArtistTitleSearchResponse response, String artist, String title
  ) {
    if (!response.getError().isBlank()) {
      log.info(
        "Error '" + response.getError() + "' searching for '"
        + title + "' by '" + artist + "'"
      );

      throw new CustomMetallumException(response.getError());
    }

    switch (response.getTotalRecords()) {
      case 0: {
        throw new CustomDataNotFoundException(
          "'" + title + "' by '" + artist + "' was not found"
        );
      }
      case 1: {
        // return the only result
        AaDataValue data = response.getFirstDataValue();
        return createSearchResponse(data);
      }
      default: {
        log.info("Found multiple results for '" + title + "' by '" + artist);

        // return the first result... should do narrowing?
        AaDataValue data = response.getFirstDataValue();
        return createSearchResponse(data);
      }
    }
  }

  private ArtistReleaseSearchResult createSearchResponse(AaDataValue data) {
    return new ArtistReleaseSearchResult(
      parseLinkElement(data.getArtistLinkElementString()),
      parseLinkElement(data.getReleaseLinkElementString()),
      data.getReleaseType()
    );
  }

  private LinkElement parseLinkElement(String html) {
    return new LinkElement(Jsoup.parse(html).selectFirst("a"));
  }

  public List<SongResult> parseSongs(String html) {
    Document doc = Jsoup.parse(html);

    Element tbody = doc.select(".table_lyrics > tbody").first();
    if (tbody == null) {
      throw new CustomHtmlParsingException("Release song table was not found");
    }

    List<SongResult> songs = new ArrayList<>();
    tbody.children().stream()
      .forEach(tr -> {
        if (tr.hasClass("even") || tr.hasClass("odd")) {
          Elements tds = tr.children();

          final String songTitle = tds.get(1).ownText();
          final String songDuration = tds.get(2).ownText();
          final String id = extractSongId(tds);

          songs.add(new SongResult(id, songTitle, songDuration));
        }
      });

    return songs;
  }

  /**
   * Get the song id from release table row elements. Can contain letters
   * 
   * @param tds  release table row elements
   * @return the song id if it is found, null otherwise
   */
  private String extractSongId(Elements tds) {
    // 1st table data element has a link child element. This link element
    // has 'name' attribute where the song id is found
    Element element = tds.get(0);

    if (element.childrenSize() > 0) {
      Element child = element.child(0);
      if (child.tagName().equals("a")) {
        return child.attr("name");
      }
    }

    throw new CustomHtmlParsingException(
      "Expected to find song id"
    );
  }
}
