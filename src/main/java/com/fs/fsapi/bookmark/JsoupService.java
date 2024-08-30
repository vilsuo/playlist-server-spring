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

@Service
public class JsoupService {

  /*
  private enum Tag {
    LINK ("a"),
    DT ("dt"),
    DL ("dl");

    private final String name;

    Tag(String name) {
      this.name = name;
    }
  };
  */

  private final String LINK_TAG = "a";
  
  private final String DT_TAG = "dt";

  private final String DL_TAG = "dl";

  private final Pattern HEADER_PATTERN = Pattern.compile("h[1-6]");


  public List<FolderLink> createFolderLinks(MultipartFile file, String headerText) throws IOException {
    Document doc = Jsoup.parse(file.getInputStream(), null, "");

    return parseFolder(doc, findHeader(doc, headerText), new ArrayList<>());
  }

  private Element findHeader(Document doc, String headerText) {
    Elements headers = doc.select(
      ":is(h1, h2, h3, h4, h5, h6):matches(^" + headerText + "$)"
    );

    if (headers.isEmpty()) {
      throw new RuntimeException("header '" + headerText + "' was not found");

    } else if (headers.size() > 1) {
      throw new RuntimeException("Multiple headers '" + headerText + "' were found");
    }

    return headers.first();
  }

  private List<FolderLink> parseFolder(Document doc, Element h, List<FolderLink> folderLinks) {
    String text = h.text();

    // find the <dl> element following the header
    Element dlElement = h.nextElementSibling();

    if (dlElement == null) {
      throw new RuntimeException(
        "Header '" + text + "' does not have sibling elements"
      );

    } else if (!dlElement.tagName().equals(DL_TAG)) {
      throw new RuntimeException(
        "The next element of header '" + text + "' was not a '" + DL_TAG + "' element"
      );
    }

    dlElement.children().stream()
      .filter(child -> child.tagName().equals(DT_TAG))
      .forEach(dt -> {
        if (isSingleDtElement(dt)) {
          Element link = dt.child(0);

          folderLinks.add(
            new FolderLink(
              new Link(
                link.text(),
                link.attribute("href").getValue(),
                link.attribute("add_date").getValue()
              ),
              text
            )
          );

        } else if (isFolderDtElement(dt)) {
          String newText = dt.child(0).text();
          parseFolder(doc, findHeader(doc, newText), folderLinks);

        } else {
          throw new RuntimeException("Found invalid '" + DT_TAG + "' element");
        }
      });

    return folderLinks;
  }

  private boolean isSingleDtElement(Element dt) {
    return (dt.childrenSize() == 1) && dt.child(0).tagName().equals(LINK_TAG);
  }

  private boolean isFolderDtElement(Element dt) {
    return (dt.childrenSize() > 0)
      && HEADER_PATTERN.matcher(dt.child(0).tagName()).matches();
  }
  
  /*
  @Getter
  public abstract class Element<T> {

    private final String tagName;

    private T content;

    public Element(String tagName, T content) {
      this.tagName = tagName;
      this.content = content;
    }
  }

  @Getter
  public class HeaderElement extends Element<String> {

    public HeaderElement(int level, String content) {
      super("h" + level, content);
    }
  }

  @Getter
  public class LinkElement extends Element<String> {

    private final String href;

    private final String addDate;

    public LinkElement(String content, String href, String addDate) {
      super("a", content);

      this.href = href;
      this.addDate = addDate;
    }
  }

  @Getter
  public class DtSingleElement extends Element<LinkElement> {
    
    public DtSingleElement(LinkElement link) {
      super("dt", link);
    }
  };

  @Getter
  public class DtFolderElement extends Element<List<LinkElement>> {

    public DtFolderElement(List<LinkElement> links) {
      super("dt", links);
    }
  };

  @Getter
  public class DlElement extends Element<List<LinkElement>> {

    public DlElement(List<LinkElement> links) {
      super("dl", links);
    }
  };
  */
}
