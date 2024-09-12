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

  private final Pattern ANY_HEADER_PATTERN = Pattern.compile("h[1-6]");

  /**
   * Find details about {@code a} elements in a specific block indicated by a header
   * text content. Each resulting object will also contain the parent header
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
   * @return the list of {@code a} element details
   * @throws IOException if the file could not be found, or read, or if the
   *                     charsetName is invalid
   */
  public List<BookmarksLinkElement> parse(InputStream file, String headerText) throws IOException {
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
        "Could not find a header element with the text content '" + headerText + "'"
      );

    } else if (headers.size() > 1) {
      throw new CustomParameterConstraintException(
        "Found multiple header elements with the text content '" + headerText + "'"
      );
    }

    return headers.first();
  }

  /**
   * Find details about {@code a} elements recursively inside the block indicated
   * by the header element text content.
   * 
   * @param h  the header element
   * @param values  the list to populate with details about {@code a} elements
   * @return the populated list
   */
  private List<BookmarksLinkElement> parseFolder(Element h, List<BookmarksLinkElement> values) {
    final String text = h.text();

    // the next element should be a dl element
    Element next = h.nextElementSibling();

    if (next == null) {
      throw new CustomHtmlParsingException(
        "Expected the header element with the text content '" + text
        + "' to have a next sibling element"
      );

    } else if (!next.nameIs("dl")) {
      throw new CustomHtmlParsingException(
        "Expected the next sibling element of header with text content '"
        + text + "' to be a 'dl' element, instead found a '"
        + next.normalName() + "' element"
      );
    }

    next.children().stream()
      .skip(1) // first element is expected to be a 'p' element
      .forEach(element -> {
        if (isDtSingleElement(element)) {
          try {
            // the only child is 'a' element
            Element a = element.child(0);
            values.add(new BookmarksLinkElement(a, text));
          } catch (IllegalArgumentException e) {
            // throw creation errors
            throw new CustomHtmlParsingException(e.getMessage());
          }
        } else if (isDtContainerElement(element)) {
          // the first child is header element
          Element hSub = element.child(0);
          parseFolder(hSub, values);

        } else  if (element.nameIs("dt")) {
          throw new CustomHtmlParsingException(
            "A 'dl' element has a child element 'dt' with invalid structure"
          );
        } else {
          throw new CustomHtmlParsingException(
            "A 'dl' element has unexpected child element '" + element.normalName() + "'"
          );
        }
      });

    return values;
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
    return e.nameIs("dt")
      && (e.childrenSize() == 1)
      && e.child(0).nameIs("a");
  }

  /**
   * Check if element has the structure
   * {@code
   *  <dt>
   *    <h? />
   *    <dl />
   *    <p />
   *  </dt>
   * }, where the tag {@code h?} is a html header element of any level.
   * 
   * @param e  the element to check
   * @return true if element has the structure
   */
  private boolean isDtContainerElement(Element e) {
    return e.nameIs("dt")
      && (e.childrenSize() == 3)
      && ANY_HEADER_PATTERN.matcher(e.child(0).normalName()).matches()
      && e.child(1).nameIs("dl")
      && e.child(2).nameIs("p");
  }
}
