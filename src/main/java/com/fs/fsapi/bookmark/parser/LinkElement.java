package com.fs.fsapi.bookmark.parser;

import org.jsoup.nodes.Element;
import org.springframework.lang.Nullable;

import lombok.Getter;

@Getter
public class LinkElement {

  private final Element element;

  public final String getText() {
    return element.wholeText();
  }

  public LinkElement(Element element) {
    // attached element must be a tag 'a'
    final String tag = element.normalName();
    if (!tag.equals("a")) {
      throw new IllegalArgumentException(
        "Expected element '" + tag + "' to be 'a' element"
      );
    }

    this.element = element;
  }

  /**
   * 
   * @return the href key value, or null if not present
   */
  public final String getHref() {
    return getAttributeValue("href");
  }

  @Nullable
  protected final String getAttributeValue(String name) {
    if (element.hasAttr(name)) {
      return element.attr(name);
    }

    return null;
  }
}
