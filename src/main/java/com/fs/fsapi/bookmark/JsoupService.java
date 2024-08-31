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

// TODO
// - create custom exception class, with more info about exception (folder location etc.)

@Service
public class JsoupService {

  private final Pattern HEADER_PATTERN = Pattern.compile("h[1-6]");

  public List<FolderLink> createFolderLinks(MultipartFile file, String headerText) throws IOException {
    Document doc = Jsoup.parse(file.getInputStream(), null, "");

    return parseFolder(doc, findHeader(doc, headerText), new ArrayList<>());
  }

  /**
   * Find a header element with the given text content. Case sensitive.
   * 
   * @param doc Document to search in
   * @param headerText text content of the header
   * @return the header element
   * @throws RuntimeException unless an unique such header is found
   */
  private Element findHeader(Document doc, String headerText) {
    Elements headers = doc.select(
      ":is(h1, h2, h3, h4, h5, h6):matches(^" + headerText + "$)"
    );

    if (headers.isEmpty()) {
      throw new RuntimeException("Header '" + headerText + "' was not found");

    } else if (headers.size() > 1) {
      throw new RuntimeException("Multiple headers '" + headerText + "' were found");
    }

    return headers.first();
  }

  private List<FolderLink> parseFolder(Document doc, Element h, List<FolderLink> folderLinks) {
    final String text = h.text();

    // find the dl element following the header
    Element dlElement = h.nextElementSibling();

    if (dlElement == null) {
      throw new RuntimeException(
        "Header '" + text + "' does not have sibling elements"
      );

    } else if (!elementHasTag(dlElement, Tag.DL)) {
      throw new RuntimeException(
        "The next element of header '" + text + "' was not a '" + Tag.DL + "' element"
      );
    }

    dlElement.children().stream()
      .skip(1) // first element is paragraph, so skip it
      .forEach(dt -> {
        if (isLinkDtElement(dt)) {
          // the only child is link element
          Element link = dt.child(0);

          folderLinks.add(
            new FolderLink(
              link.text(), // empty default
              link.attribute("href").getValue(), // empty default
              link.attribute("add_date").getValue(), // empty default
              text // empty default
            )
          );

        } else if (isFolderDtElement(dt)) {
          // the first child is header element
          Element subH = dt.child(0);
          parseFolder(doc, subH, folderLinks);

        } else {
          throw new RuntimeException(
            "Found unexpected child element type of '" + Tag.DL + "' element"
          );
        }
      });

    return folderLinks;
  }

  private boolean elementHasTag(Element e, Tag tag) {
    return tag.name.equals(e.normalName());
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
      && HEADER_PATTERN.matcher(e.child(0).normalName()).matches()
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
