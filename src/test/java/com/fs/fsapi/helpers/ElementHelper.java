package com.fs.fsapi.helpers;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class ElementHelper {

  public static Element createLinkTypeElement(String text, String href) {
    return createLinkTypeElement(text, href, null);
  }
  
  public static Element createLinkTypeElement(String text, String href, String addDate) {
    Attributes attrs = new Attributes();
    if (href != null) { attrs.add("href", href); }
    if (addDate != null) { attrs.add("add_date", addDate); }

    Element linkElement = new Element(Tag.valueOf("a"), null, attrs);
    if (text != null) { linkElement.appendText(text); }

    return linkElement;
  }
}
