package com.fs.fsapi.metallum.driver;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.parser.MetallumParser;
import com.fs.fsapi.metallum.result.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.result.InstrumentalLyricsResult;
import com.fs.fsapi.metallum.result.LyricsResult;
import com.fs.fsapi.metallum.result.NotAvailableLyricsResult;

@Service
public class MetallumDriverParser extends MetallumParser {

  /**
   * Extract search results.
   * 
   * @param htmlTbody  search results table body
   * @return parsed list of search results
   */
  public List<ArtistTitleSearchResult> parseSearchResults(String htmlTbody) {
    final List<Elements> rows = readTableBody(htmlTbody, super::isTableRowDataElement);

    // check if any results
    if (rows.size() == 1) {
      final Elements tds = rows.get(0);
      if (tds.size() == 1) {
        throw new CustomDataNotFoundException("No matches found");
      }
    }

    return super.parseSearchTable(
      rows,
      (row) -> new String[] {
        row.get(0).html(),
        row.get(1).html(),
        row.get(2).ownText()
      }
    );
  }

  /**
   * Read HTML table body into a list of table row child elements.
   * 
   * @param htmlTbody  html {@code tbody} element
   * @param rowFilter  filter for {@code tbody} child elements
   * @return list of table row child elements
   */
  private List<Elements> readTableBody(String htmlTbody, Predicate<Element> rowFilter) {
    // parse as is: https://stackoverflow.com/a/63024182
    final Element tbody = Jsoup.parse(htmlTbody, "", Parser.xmlParser())
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

  public LyricsResult parseLyrics(String text) {
    final String value = text.trim();

    if (value.isEmpty()) {
      return new NotAvailableLyricsResult();

    } else if (value.equals("instrumental")) {
      return new InstrumentalLyricsResult();
    }

    final String rowSeparator = "\n";
    return super.parseLyricsAvailableResult(value, rowSeparator);
  }
}
