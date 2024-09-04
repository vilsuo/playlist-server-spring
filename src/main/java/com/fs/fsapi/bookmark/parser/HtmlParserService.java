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
public class HtmlParserService {

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
   * @param file html file input stream
   * @param headerText the {@code <text>} of the {@code <header>} indicating
   *                   the search for links is limited in the following {@code <folder>}
   * @return
   * @throws IOException
   */
  public List<FolderLink> createFolderLinks(InputStream file, String headerText) throws IOException {
    Document doc = Jsoup.parse(file, null, "");

    return parseFolder(findHeader(doc, headerText), new ArrayList<>());
  }

  /**
   * Find a header element with the given text content. Case sensitive.
   * 
   * @param doc Document to search in
   * @param headerText text content of the header
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

  private List<FolderLink> parseFolder(Element h, List<FolderLink> folderLinks) {
    final String text = h.text(); // empty default

    // the next element should be a dl element
    Element next = h.nextElementSibling();

    if (next == null) {
      throw new CustomHtmlParsingException(
        "Header with the text content '" + text
        + "' does not have a next sibling", h
      );

    } else if (!elementHasTag(next, Tag.DL)) {
      throw new CustomHtmlParsingException(
        "Expected the next sibling element of header with text content '"
        + text + "' to be '" + Tag.DL.name + "' element, instead found '"
        + getElementName(next) + "' element", next
      );
    }

    next.children().stream()
      .skip(1) // first element is paragraph, so skip it
      .forEach(element -> {
        if (isDtSingleElement(element)) {
          // the only child is link element
          Element a = element.child(0);
          folderLinks.add(new FolderLink(a, text));

        } else if (isDtContainerElement(element)) {
          // the first child is header element
          Element hSub = element.child(0);
          parseFolder(hSub, folderLinks);

        } else {
          throw new CustomHtmlParsingException(
            "A child element of '" + Tag.DL.name
            + "' has unexpected structure", element
          );
        }
      });

    return folderLinks;
  }

  private String getElementName(Element e) {
    return e.normalName();
  }

  private boolean elementHasTag(Element e, Tag tag) {
    return tag.name.equals(getElementName(e));
  }

  /**
   * Check if an element is a {@link Tag#DT} element with the structure
   * {@code
   *  <dt>
   *    <a />
   *  </dt>
   * }.
   * 
   * @param e the element to check
   * @return true if element is a certain {@link Tag#DT} element
   */
  private boolean isDtSingleElement(Element e) {
    return elementHasTag(e, Tag.DT)
      && (e.childrenSize() == 1)
      && elementHasTag(e.child(0), Tag.LINK);
  }

  /**
   * Check if element is a {@link Tag#DT} element with the structure
   * {@code
   *  <dt>
   *    <h? />
   *    <dl />
   *    <p />
   *  </dt>
   * }. 
   * 
   * @implNote Does not check the structure of child elements.
   * 
   * @param e the element to check
   * @return true if element is a certain {@link Tag#DT} element
   */
  private boolean isDtContainerElement(Element e) {
    return elementHasTag(e, Tag.DT)
      && (e.childrenSize() == 3)
      && HEADER_PATTERN.matcher(getElementName(e.child(0))).matches()
      && elementHasTag(e.child(1), Tag.DL)
      && elementHasTag(e.child(2), Tag.P);
  }

  private enum Tag {
    P ("p"),
    LINK ("a"),

    /**
     * HTML-element with the tag {@code dt}. The child structure is
     * one of the following
     * 
     * <ol>
     *  <li>Just a single html link element {@code <a />}.</li>
     *  <li>
     *    Exactly three elements in the order {@code <h? /> <dl /> <p />}.
     *    The tag {@code h?} is a html header element of any level and
     *    {@code dl} has structure specified by {@link Tag#DL}. The header
     *    indicates the beginning of a subfolder structure and the {@code dl}
     *    element the contents of that folder.
     *  </li>
     * </ol>
     * 
     * @see {@link HtmlParserService#isDtSingleElement}
     * @see {@link HtmlParserService#isDtContainerElement}
     */
    DT ("dt"),

    /**
     * HTML-element with the tag {@code dl}. The child structure is
     * {@code <p /> <dt /> ... <dt />}, where each {@code dt} is a 
     * structure specified by {@link Tag#DT}.
     */
    DL ("dl");

    private final String name;

    Tag(String name) {
      this.name = name;
    }
  };
  
}
