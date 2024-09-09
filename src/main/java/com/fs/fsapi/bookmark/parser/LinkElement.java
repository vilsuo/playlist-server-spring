package com.fs.fsapi.bookmark.parser;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LinkElement {

  private final Element element;

  public final String getText() {
    return element.wholeText();
  }

  /**
   * 
   * @return the href key value, or null if not present
   */
  public final String getHref() {
    return getAttributeValue("href");
  }

  protected final String getAttributeValue(String name) {
    Attribute attr = element.attribute(name);
    return (attr != null) ? attr.getValue() : null;
  }
}
