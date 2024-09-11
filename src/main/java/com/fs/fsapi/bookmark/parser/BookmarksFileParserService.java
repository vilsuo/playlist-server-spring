package com.fs.fsapi.bookmark.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fs.fsapi.exceptions.CustomHtmlParsingException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;

@Service
public class BookmarksFileParserService {

  private final Pattern HEADER_PATTERN = Pattern.compile("h[1-6]");

  /**
   * Find HTML {@code a} elements in a specific block indicated by a header
   * text content. Each resulting object will also contain the preceeding header
   * element's text content. 
   * 
   * The input is expected to have/contain the structure specified by the following 
   * Backus-Naur Form (BNF):
   * 
   * <pre> {@code
   * <root>         ::= <header> <folder> "<p />"
   * <folder>       ::= "<dl>" (<dtcontainer> | <dtsingle>)* "</dl>"
   * <dtcontainer>  ::= "<dt>" <header> <folder> "<p />" "</dt>"
   * <dtsingle>     ::= "<dt>" <link> "</dt>"
   * <header>       ::= "<h" <step> (" " <attribute>)*  ">" <text> "</h" <step> ">"
   * <link>         ::= "<a" (" " <attribute>)* ">" <text> "</a>"
   * <step>         ::= [1-6]
   * } </pre>
   * 
   * where {@code <attribute>} is element attribute key=value pair and 
   * {@code <text>} is element text content. 
   * 
   * @param file  html file input stream
   * @param headerText  the {@code <text>} of the {@code <header>} indicating
   *                    the search for links is limited in the following
   *                    {@code <folder>}
   * @return the list of link elements with their associated header text content
   * @throws IOException
   */
  public List<BookmarksLinkElement> parseFile(InputStream file, String headerText) throws IOException {
    Document doc = Jsoup.parse(file, null, "");

    return parseFolder(findHeader(doc, headerText), new ArrayList<>());
  }

  /**
   * Find a header element with the given text content. Case sensitive.
   * 
   * @param doc  Document to search in
   * @param headerText  text content of the header
   * @return the header element
   * @throws CustomParameterConstraintException unless an unique such header is found
   */
  private Element findHeader(Document doc, String headerText) {
    Elements headers = doc.select(
      ":is(h1, h2, h3, h4, h5, h6):matches(^" + headerText + "$)"
    );

    if (headers.isEmpty()) {
      throw new CustomParameterConstraintException(
        "Could not find a header with the text content '" + headerText + "'"
      );

    } else if (headers.size() > 1) {
      throw new CustomParameterConstraintException(
        "Found multiple headers with the text content '" + headerText + "'"
      );
    }

    return headers.first();
  }

  /**
   * Find link elements recursively inside the block indicated by the header
   * element text content.
   * 
   * @param h  the header element
   * @param folderLinks  the list where to add found link elements with their
   *                     associated header text content
   * @return the passed in list 
   */
  private List<BookmarksLinkElement> parseFolder(Element h, List<BookmarksLinkElement> folderLinks) {
    final String text = h.text();

    // the next element should be a dl element
    Element next = h.nextElementSibling();

    if (next == null) {
      throw new CustomHtmlParsingException(
        "Header with the text content '" + text
        + "' does not have a next sibling"
      );

    } else if (!elementHasTag(next, "dl")) {
      throw new CustomHtmlParsingException(
        "Expected the next sibling element of header with text content '"
        + text + "' to be 'dl' element, instead found '"
        + getElementTagName(next) + "' element"
      );
    }

    next.children().stream()
      .skip(1) // first element is expected to be paragraph, so skip it
      .forEach(element -> {
        if (isDtSingleElement(element)) {
          // the only child is link element
          Element a = element.child(0);
          folderLinks.add(new BookmarksLinkElement(a, text));

        } else if (isDtContainerElement(element)) {
          // the first child is header element
          Element hSub = element.child(0);
          parseFolder(hSub, folderLinks);

        } else {
          throw new CustomHtmlParsingException(
            "A child element of 'dl' has unexpected structure"
          );
        }
      });

    return folderLinks;
  }

  private String getElementTagName(Element e) {
    return e.normalName();
  }

  private boolean elementHasTag(Element e, String tagName) {
    return tagName.equals(getElementTagName(e));
  }

  /**
   * Check if element has the structure
   * {@code
   *  <dt>
   *    <a />
   *  </dt>
   * }.
   * 
   * @param e  the element to check
   * @return true if element has the structure
   */
  private boolean isDtSingleElement(Element e) {
    return elementHasTag(e, "dt")
      && (e.childrenSize() == 1)
      && elementHasTag(e.child(0), "a");
  }

  /**
   * Check if element has the structure
   * {@code
   *  <dt>
   *    <h? />
   *    <dl />
   *    <p />
   *  </dt>
   * }. The tag {@code h?} is a html header element of any level.
   * 
   * @param e  the element to check
   * @return true if element has the structure
   */
  private boolean isDtContainerElement(Element e) {
    return elementHasTag(e, "dt")
      && (e.childrenSize() == 3)
      && HEADER_PATTERN.matcher(getElementTagName(e.child(0))).matches()
      && elementHasTag(e.child(1), "dl")
      && elementHasTag(e.child(2), "p");
  }
}
