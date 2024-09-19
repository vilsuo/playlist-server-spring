package com.fs.fsapi.metallum.client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.fs.fsapi.bookmark.parser.LinkElement;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomMetallumException;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.parser.MetallumParser;
import com.fs.fsapi.metallum.response.AaDataValue;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;
import com.fs.fsapi.metallum.result.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.result.InstrumentalLyricsResult;
import com.fs.fsapi.metallum.result.LyricsResult;
import com.fs.fsapi.metallum.result.NotAvailableLyricsResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MetallumClientParser extends MetallumParser {

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
        AaDataValue data = response.getAaData().get(0);
        return parseSearchData(data);
      }
      default: {
        log.info("Found multiple results for '" + title + "' by '" + artist);

        // return the first result...
        // - implement narrowing by release type?
        // - return a list of results?
        AaDataValue data = response.getAaData().get(0);
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

  public LyricsResult parseLyrics(String text) {
    final String value = text.trim();

    // no lyrics
    if (value.equals("<em>(lyrics not available)</em>")) {
      return new NotAvailableLyricsResult();
    }

    // instrumental
    if (value.equals("(<em>Instrumental</em>)<br />")) {
      return new InstrumentalLyricsResult();
    }

    final String rowSeparator = "<br />";
    return super.parseLyricsAvailableResult(value, rowSeparator);
  }
}
