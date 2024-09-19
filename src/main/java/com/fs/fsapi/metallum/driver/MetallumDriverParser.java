package com.fs.fsapi.metallum.driver;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fs.fsapi.bookmark.parser.LinkElement;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;
import com.fs.fsapi.metallum.parser.MetallumParser;
import com.fs.fsapi.metallum.result.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.result.InstrumentalLyricsResult;
import com.fs.fsapi.metallum.result.LyricsResult;
import com.fs.fsapi.metallum.result.NotAvailableLyricsResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MetallumDriverParser extends MetallumParser {

  /**
   * Extract search results.
   * 
   * @param htmlTbody  search results table body
   * @return parsed list of search results
   */
  public List<ArtistTitleSearchResult> parseSearchResults(String htmlTbody) {
    final List<Elements> trs = readTableBody(htmlTbody, this::isTableRowDataElement);

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

  private boolean isTableRowDataElement(Element element) {
    return element.tagName().equals("tr") && (
      element.hasClass("even") || element.hasClass("odd")
    );
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
