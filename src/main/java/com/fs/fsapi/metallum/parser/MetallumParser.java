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
import com.fs.fsapi.exceptions.CustomMetallumException;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.response.AaDataValue;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MetallumParser {

  /**
   * Extract the best result from the response.
   * 
   * @param response  object where to extract details from
   * @param artist  the artist name used in the response, used for logging
   *                and error messages only
   * @param title  the release title used in the response, used for logging
   *               and error messages only
   * @return the extracted result
   */
  public ArtistTitleSearchResult getSearchResult(
    ArtistTitleSearchResponse response, String artist, String title
  ) {
    if (!response.getError().isBlank()) {
      log.info(
        "Error '" + response.getError() + "' while searching for '"
        + title + "' by '" + artist + "'"
      );

      throw new CustomMetallumException(response.getError());
    }

    switch (response.getTotalRecords()) {
      case 0: {
        throw new CustomDataNotFoundException(
          "No results for '" + title + "' by '" + artist + "'"
        );
      }
      case 1: {
        // return the only result
        AaDataValue data = response.getFirstDataValue();
        return parseSearchData(data);
      }
      default: {
        log.info("Found multiple results for '" + title + "' by '" + artist);

        // return the first result...
        // - implement narrowing by release type?
        // - return a list of results?
        AaDataValue data = response.getFirstDataValue();
        return parseSearchData(data);
      }
    }
  }

  private ArtistTitleSearchResult parseSearchData(AaDataValue data) {
    return new ArtistTitleSearchResult(
      parseSearchDataElementOuterHtml(data.getArtistLinkElementOuterHtml()),
      parseSearchDataElementOuterHtml(data.getTitleLinkElementOuterHtml()),
      data.getReleaseType()
    );
  }

  private LinkElement parseSearchDataElementOuterHtml(String html) {
    final Element e = Jsoup.parse(html).selectFirst("a");

    if (e == null) {
      throw new CustomMetallumScrapingException(
        "Expected data '" + html + "' to contain a 'a' element"
      );
    } else if (!e.hasAttr("href")) {
      throw new CustomMetallumScrapingException(
        "Expected data element '" + e.toString() + "' to have 'href' attribute"
      );
    }

    return new LinkElement(e);
  }

  /**
   * Extract songs from the song table.
   * 
   * @param html  string where the song table can be found
   * @return list of songs
   */
  public List<SongResult> parseSongs(String html) {
    Document doc = Jsoup.parse(html);

    Element tbody = doc.select(".table_lyrics > tbody").first();
    if (tbody == null) {
      throw new CustomMetallumScrapingException(
        "Song table was not found"
      );
    }

    List<SongResult> songs = new ArrayList<>();
    tbody.children().stream()
      .forEach(tr -> {
        if (tr.hasClass("even") || tr.hasClass("odd")) {
          Elements tds = tr.children();

          final String id = extractSongId(tds.get(0));
          final String songTitle = tds.get(1).ownText();
          final String songDuration = tds.get(2).ownText();

          songs.add(new SongResult(id, songTitle, songDuration));
        }
      });

    return songs;
  }

  /**
   * Get the song id from release table row data element.
   * 
   * @param tds  release table row data element (single song row)
   * @return the song id
   * @throws CustomMetallumScrapingException if id can not be found
   */
  private String extractSongId(Element element) {
    if (element.childrenSize() > 0) {
      Element child = element.child(0);
      if (child.hasAttr("name")) {
        return child.attr("name");
      }
    }

    throw new CustomMetallumScrapingException(
      "Song id was not found"
    );
  }
}
