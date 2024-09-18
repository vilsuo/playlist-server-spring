package com.fs.fsapi.metallum.driver;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fs.fsapi.bookmark.parser.LinkElement;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.parser.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.parser.LyricsResult;
import com.fs.fsapi.metallum.parser.SongResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MetallumPageParser {

  /**
   * Extract search results.
   * 
   * @param htmlTBody  search results table body
   * @return parsed list of search results
   */
  public List<ArtistTitleSearchResult> parseSearchResults(String htmlTBody) {
    final List<Elements> trs = readTableBody(htmlTBody, this::isTableRowElement);

    if (trs.size() == 1) {
      final Elements tds = trs.get(0);
      if (tds.size() == 1) {
        // no results
        final String message = tds.get(0).ownText();
        throw new CustomDataNotFoundException(message);
      }
    }

    return trs.stream()
      .map(this::parseSearchTableRow)
      .collect(Collectors.toList());

    /*
    final Element tbody = Jsoup.parse(htmlTBody, "", Parser.xmlParser())
      .selectFirst("tbody");
    
    if (tbody == null) {
      throw new CustomMetallumScrapingException(
        "Search results table was not found"
      );
    }

    final Elements trs = tbody.children();
    if (trs.size() == 1) {
      final Elements tds = trs.get(0).children();
      if (tds.size() == 1) {
        // no results
        final String message = tds.get(0).ownText();
        throw new CustomDataNotFoundException(message);
      }
    }

    return tbody.children().stream()
      .filter(e -> isTableRowElement(e))
      .map(tr -> {
        final Elements tds = tr.children();

        final String artistLinkHtml = tds.get(0).html();
        final String titleLinkHtml = tds.get(1).html();
        final String releaseType = tds.get(2).ownText();

        return parseSearchData(
          new AaDataValue(artistLinkHtml, titleLinkHtml, releaseType)
        );
      })
      .collect(Collectors.toList());
    */
  }

  /**
   * Parse a single search result table row.
   * 
   * @param tds  child elements of a table row
   * @return a search result
   */
  private ArtistTitleSearchResult parseSearchTableRow(Elements tds) {
    final String artistLinkOuterHtml = tds.get(0).html();
    final String titleLinkOuterHtml = tds.get(1).html();
    final String releaseType = tds.get(2).ownText();

    return new ArtistTitleSearchResult(
      parseSearchDataLinkOuterHtml(artistLinkOuterHtml),
      parseSearchDataLinkOuterHtml(titleLinkOuterHtml),
      releaseType
    );
  }

  /**
   * Extract details about a HTML link element.
   * 
   * @param outerHtml  element {@code a} outer html
   * @return element href attribute value and text content
   */
  private LinkElement parseSearchDataLinkOuterHtml(String outerHtml) {
    final Element e = Jsoup.parse(outerHtml).selectFirst("a");

    if (e == null) {
      throw new CustomMetallumScrapingException(
        "Expected data '" + outerHtml + "' to contain a 'a' element"
      );
    } else if (!e.hasAttr("href")) {
      throw new CustomMetallumScrapingException(
        "Expected data element '" + e.toString() + "' to have 'href' attribute"
      );
    }

    return new LinkElement(e);
  }

  /**
   * Extract release title songs.
   * 
   * @param htmlTBody  song table body
   * @return parsed list of songs
   */
  public List<SongResult> parseSongs(String htmlTBody) {
    return readTableBody(htmlTBody, this::isTableRowElement).stream()
      .map(this::parseSongTableRow)
      .collect(Collectors.toList());

    /*
    final Element tbody = Jsoup.parse(htmlTBody, "", Parser.xmlParser())
      .selectFirst("tbody");
    
    if (tbody == null) {
      throw new CustomMetallumScrapingException(
        "Song table was not found"
      );
    }

    return tbody.children().stream()
      .filter(e -> isTableRowElement(e)) // last row should contain the total duration 
      .map(tr -> {
        Elements tds = tr.children();

        final String id = extractSongId(tds.get(0));
        final String songTitle = tds.get(1).ownText();
        final String songDuration = tds.get(2).ownText();

        return new SongResult(id, songTitle, songDuration);
      })
      .collect(Collectors.toList());
      */
  }

  /**
   * Parse a single song table row.
   * 
   * @param tds  child elements of a table row
   * @return a song
   */
  private SongResult parseSongTableRow(Elements tds) {
    final String id = extractSongId(tds.get(0));
    final String songTitle = tds.get(1).ownText();
    final String songDuration = tds.get(2).ownText();

    return new SongResult(id, songTitle, songDuration);
  }

  /**
   * Extract the song id from a release table row data element.
   * 
   * @param tds  release table row data element
   * @return the song id
   */
  private String extractSongId(Element td) {
    if (td.childrenSize() > 0) {
      Element child = td.child(0);
      if (child.hasAttr("name")) {
        return child.attr("name");
      }
    }

    throw new CustomMetallumScrapingException(
      "Song id was not found"
    );
  }

  /**
   * Read HTML table body into a list of table row child elements.
   * 
   * @param htmlTBody  html {@code tbody} element
   * @param rowFilter  filter for {@code tbody} child elements
   * @return list of table row child elements
   */
  private List<Elements> readTableBody(String htmlTBody, Predicate<Element> rowFilter) {
    // parse as is: https://stackoverflow.com/a/63024182
    final Element tbody = Jsoup.parse(htmlTBody, "", Parser.xmlParser())
      .selectFirst("tbody");
    
    if (tbody == null) {
      throw new CustomMetallumScrapingException(
        "Table was not found"
      );
    }

    return tbody.children().stream()
      .filter(e -> rowFilter.test(e))
      .map(Element::children)
      .collect(Collectors.toList());
  }

  private boolean isTableRowElement(Element element) {
    return element.tagName().equals("tr") && (
      element.hasClass("even") || element.hasClass("odd")
    );
  }

  public LyricsResult parseLyrics(String html) {
    final String value = html.trim();

    // no lyrics
    if (value.equals("<em>(lyrics not available)</em>")) {
      return new LyricsResult("Lyrics not available");
    }

    // instrumental
    if (value.equals("(<em>Instrumental</em>)<br />")) {
      return new LyricsResult("Instrumental");
    }

    final String ROW_SEPARATOR = "<br />";
    final List<String> lyrics = Stream.of(value.split(ROW_SEPARATOR))
      .map(row -> row.trim())
      .collect(Collectors.toList());

    return new LyricsResult(lyrics);
  }
}
