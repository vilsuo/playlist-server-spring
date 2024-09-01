package com.fs.fsapi.bookmark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fs.fsapi.exceptions.CustomHtmlParsingException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;

@Service
public class HtmlParserService {

  private final Pattern HEADER_PATTERN = Pattern.compile("h[1-6]");

  public List<FolderLink> createFolderLinks(MultipartFile file, String headerText) throws IOException {
    Document doc = Jsoup.parse(file.getInputStream(), null, "");

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
    final String text = h.text();

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
        if (isLinkDtElement(element)) {
          // the only child is link element
          Element a = element.child(0);

          folderLinks.add(
            new FolderLink(
              a.text(), // empty default
              a.attribute("href").getValue(), // empty default
              a.attribute("add_date").getValue(), // empty default
              text // empty default
            )
          );

        } else if (isFolderDtElement(element)) {
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
  private boolean isLinkDtElement(Element e) {
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
  private boolean isFolderDtElement(Element e) {
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
     * @see {@link JsoupService#isLinkDtElement}
     * @see {@link JsoupService#isFolderDtElement}
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
