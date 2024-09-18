package com.fs.fsapi.metallum.driver;

import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fs.fsapi.bookmark.parser.LinkElement;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.parser.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.parser.SongResult;
import com.fs.fsapi.metallum.response.AaDataValue;

@Service
public class MetallumPageParser {

  /**
   * Extract details about search result.
   * 
   * @param htmlPage  web page where the search results can be found
   * @return parsed list of search results
   */
  public List<ArtistTitleSearchResult> parseSearchResults(String htmlPage) {
    Document doc = Jsoup.parse(htmlPage);

    Element tbody = doc.select("#searchResultsAlbum > tbody").first();
    if (tbody == null) {
      throw new CustomMetallumScrapingException(
        "Search results table was not found"
      );
    }

    return tbody.children().stream()
      .filter(e -> isTableRowElement(e))
      .map(tr -> {
        final Elements tds = tr.children();

        if (tds.size() == 1) {
          // no results
          final String message = tds.get(0).ownText();
          throw new CustomDataNotFoundException(message);
        }

        final String artistLinkHtml = tds.get(0).html();
        final String titleLinkHtml = tds.get(1).html();
        final String releaseType = tds.get(2).ownText();

        return parseSearchData(
          new AaDataValue(artistLinkHtml, titleLinkHtml, releaseType)
        );
      })
      .collect(Collectors.toList());
  }

  /**
   * Extract details about search result.
   * 
   * @param data  search result table data row values
   * @return  parsed search result data row
   */
  public ArtistTitleSearchResult parseSearchData(AaDataValue data) {
    return new ArtistTitleSearchResult(
      parseSearchDataElementOuterHtml(data.getArtistLinkElementOuterHtml()),
      parseSearchDataElementOuterHtml(data.getTitleLinkElementOuterHtml()),
      data.getReleaseType()
    );
  }

  /**
   * Extract details about HTML {@code a} element.
   * 
   * @param html  string containing a HTML {@code a} element
   * @return  object with the element href attribute value and text content
   */
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
   * @param htmlPage  web page where the song table can be found
   * @return parsed list of songs
   */
  public List<SongResult> parseSongs(String htmlPage) {
    Document doc = Jsoup.parse(htmlPage);

    Element tbody = doc.select(".table_lyrics > tbody").first();
    if (tbody == null) {
      throw new CustomMetallumScrapingException(
        "Song table was not found"
      );
    }

    return tbody.children().stream()
      .filter(e -> isTableRowElement(e))
      .map(tr -> {
        Elements tds = tr.children();

        final String id = extractSongId(tds.get(0));
        final String songTitle = tds.get(1).ownText();
        final String songDuration = tds.get(2).ownText();

        return new SongResult(id, songTitle, songDuration);
      })
      .collect(Collectors.toList());
  }

  private boolean isTableRowElement(Element element) {
    return element.tagName().equals("tr") && (
      element.hasClass("even") || element.hasClass("odd")
    );
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
