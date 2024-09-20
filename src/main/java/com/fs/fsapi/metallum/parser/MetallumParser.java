package com.fs.fsapi.metallum.parser;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fs.fsapi.bookmark.parser.LinkElement;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.result.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.result.LyricsResult;
import com.fs.fsapi.metallum.result.SongResult;

public abstract class MetallumParser {

  // SEARCH

  protected <T> List<ArtistTitleSearchResult> parseSearchTable(
    List<T> rows, Function<T, String[]> rowContentExtractor
  ) {
    return rows.stream()
      .map(rowContentExtractor::apply)
      .map(this::parseSearchTableRow)
      .collect(Collectors.toList());
  }

  private ArtistTitleSearchResult parseSearchTableRow(String[] rowContent) {
    if (rowContent.length != 3) {
      throw new CustomMetallumScrapingException(
        "Expected row to have 3 values"
      );
    }

    return new ArtistTitleSearchResult(
      parseSearchTableRowLinkElement(rowContent[0]),
      parseSearchTableRowLinkElement(rowContent[1]),
      rowContent[2]
    );
  }

  private LinkElement parseSearchTableRowLinkElement(String outerHtml) {
    try {
      final Element element = Jsoup.parse(outerHtml).selectFirst("a");
      return new LinkElement(element);

    } catch (IllegalArgumentException ex) {
      throw new CustomMetallumScrapingException(ex.getMessage());
    }
  }

  // SONGS

  public List<SongResult> parseSongs(String pageSource) {
    final Document doc = Jsoup.parse(pageSource);
    final Element tbody = doc.select(".table_lyrics > tbody").first();
    if (tbody == null) {
      throw new CustomMetallumScrapingException(
        "Song table was not found"
      );
    }

    return tbody.children().stream()
      .filter(this::isTableRowDataElement)
      .map(Element::children)
      .map(this::parseSongTableRow)
      .collect(Collectors.toList());
  }

  private SongResult parseSongTableRow(Elements tds) {
    if (tds.size() < 3) {
      throw new CustomMetallumScrapingException(
        "Song table row has unexpected number of children"
      );
    }

    final String id = extractSongId(tds.get(0));
    final String songTitle = tds.get(1).ownText();
    final String songDuration = tds.get(2).ownText();

    return new SongResult(id, songTitle, songDuration);
  }

  private String extractSongId(Element element) {
    if (element.childrenSize() > 0) {
      final Element child = element.child(0);
      if (child.hasAttr("name")) {
        return child.attr("name");
      }
    }

    throw new CustomMetallumScrapingException(
      "Song id was not found"
    );
  }

  // LYRICS

  protected LyricsResult parseLyricsAvailableResult(String text, String rowSeparator) {
    return Stream.of(text.split(rowSeparator))
      .map(row -> row.trim())
      .collect(Collectors.collectingAndThen(
        Collectors.toList(),
        LyricsResult::new
      ));
  }

  // HELPERS

  /*
  private Stream<Element> readTableBody(String tbodyOuterHTML) {
    // parse as is: https://stackoverflow.com/a/63024182
    final Element tbody = Jsoup.parse(tbodyOuterHTML, "", Parser.xmlParser())
      .selectFirst("tbody");
    
    if (tbody == null) {
      throw new CustomMetallumScrapingException(
        "Table was not found"
      );
    }

    return tbody.children().stream();
  }
  */

  protected boolean isTableRowDataElement(Element element) {
    return element.tagName().equals("tr") && (
      element.hasClass("even") || element.hasClass("odd")
    );
  }
}
