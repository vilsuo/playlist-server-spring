package com.fs.fsapi.helpers;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class ElementHelper {
  
  public static Element createBookmarkLinkTypeElement(String text, String href, String addDate) {
    Attributes attrs = new Attributes();
    if (href != null) { attrs.add("href", href); }
    if (addDate != null) { attrs.add("add_date", addDate); }

    return createElement("a", text, attrs);
  }

  public static Element createLinkTypeElement(String text, String href) {
    return createBookmarkLinkTypeElement(text, href, null);
  }

  public static Element createElement(String tagName, String text, Attributes attrs) {
    Element linkElement = new Element(Tag.valueOf(tagName), null, attrs);
    if (text != null) { linkElement.appendText(text); }

    return linkElement;
  }
}
