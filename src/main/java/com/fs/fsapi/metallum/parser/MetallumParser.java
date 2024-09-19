package com.fs.fsapi.metallum.parser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.result.LyricsResult;
import com.fs.fsapi.metallum.result.SongResult;

public abstract class MetallumParser {


  // SONGS

  public List<SongResult> parseSongs(String tbodyOuterHTML) {
    return readTableBody(tbodyOuterHTML)
      .filter(this::isTableRowDataElement)
      .map(Element::children)
      .map(this::parseSongTableRow)
      .collect(Collectors.toList());
  }

  private SongResult parseSongTableRow(Elements tds) {
    final String id = extractSongId(tds.get(0));
    final String songTitle = tds.get(1).ownText();
    final String songDuration = tds.get(2).ownText();

    return new SongResult(id, songTitle, songDuration);
  }

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

  // LYRICS

  protected LyricsResult parseLyricsAvailableResult(String value, String rowSeparator) {
    final List<String> lyrics = Stream.of(value.split(rowSeparator))
      .map(row -> row.trim())
      .collect(Collectors.toList());

    return new LyricsResult(lyrics);
  }

  // HELPERS

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

  private boolean isTableRowDataElement(Element element) {
    return element.tagName().equals("tr") && (
      element.hasClass("even") || element.hasClass("odd")
    );
  }
}
