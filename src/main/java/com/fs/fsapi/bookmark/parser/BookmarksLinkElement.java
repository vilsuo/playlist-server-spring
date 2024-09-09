package com.fs.fsapi.bookmark.parser;

import org.jsoup.nodes.Element;

import lombok.Getter;

@Getter
public class BookmarksLinkElement extends LinkElement {

  private final String headerText;

  public BookmarksLinkElement(Element element, String headerText) {
    super(element);
    this.headerText = headerText;
  }

  /**
   * 
   * @return the add_date key value, or null if not present
   */
  public String getAddDate() {
    return super.getAttributeValue("add_date");
  }
}
