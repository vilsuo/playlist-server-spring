package com.fs.fsapi.bookmark.parser;

import org.jsoup.nodes.Element;

import lombok.Getter;

/**
 * Contains details about bookmark file album {@code a} element.
 */
@Getter
public class BookmarksLinkElement extends LinkElement {

  private final String headerText;

  private final String addDate; // link 'add_date' attribute value

  public BookmarksLinkElement(Element element, String headerText) {
    super(element);

    if (!element.hasAttr("add_date")) {
      throw new IllegalArgumentException(
        "Expected element to have 'add_date' attribute"
      );
    }

    this.headerText = headerText;
    this.addDate = element.attr("add_date");
  }

}
