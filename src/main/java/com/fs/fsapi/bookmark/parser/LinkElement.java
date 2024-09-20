package com.fs.fsapi.bookmark.parser;

import org.jsoup.nodes.Element;

import lombok.Getter;

@Getter
public class LinkElement {

  private String text; // link text content

  private String href; // link 'href' attribute value

  /**
   * 
   * @param element  {@code a} element with {@code href} attribute
   */
  public LinkElement(Element element) {
    if (element == null) {
      throw new IllegalArgumentException(
        "Expected element to be non null"
      );
    }

    // attached element must be a 'a' element with 'href' attribute
    final String tag = element.normalName();
    if (!tag.equals("a")) {
      throw new IllegalArgumentException(
        "Expected element '" + tag + "' to be 'a' element"
      );
    } else if (!element.hasAttr("href")) {
      throw new IllegalArgumentException(
        "Expected element to have 'href' attribute"
      );
    }

    this.text = element.wholeText();
    this.href = element.attr("href");
  }
}
