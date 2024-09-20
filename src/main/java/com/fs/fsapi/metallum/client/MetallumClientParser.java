package com.fs.fsapi.metallum.client;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomMetallumException;
import com.fs.fsapi.metallum.parser.MetallumParser;
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
   * Extract search results.
   * 
   * @param response  the search response
   * @return parsed list of search results
   */
  public List<ArtistTitleSearchResult> parseSearchResults(
    ArtistTitleSearchResponse response
  ) {
    if (!response.getError().isBlank()) {
      log.error("Error while searching '" + response.getError() + "''");

      throw new CustomMetallumException(response.getError());
    }

    // check if any results
    if (response.getTotalRecords() == 0) {
      throw new CustomDataNotFoundException("No matches found");
    }

    return super.parseSearchTable(
      response.getAaData(),
      (aaData) -> new String[] {
        aaData.getArtistLinkElementOuterHtml(),
        aaData.getTitleLinkElementOuterHtml(),
        aaData.getReleaseType()
      }
    );
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
