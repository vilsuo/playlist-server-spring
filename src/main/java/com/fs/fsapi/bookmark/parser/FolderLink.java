package com.fs.fsapi.bookmark.parser;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FolderLink {

  private Element element;

  private String folderName;

  public String getText() {
    return element.wholeText();
  }

  /**
   * 
   * @return the href key value, or null if not present
   */
  public String getHref() {
    return getAttributeValue("href");
  }

  /**
   * 
   * @return the add_date key value, or null if not present
   */
  public String getAddDate() {
    return getAttributeValue("add_date");
  }

  private String getAttributeValue(String name) {
    Attribute attr = element.attribute(name);
    return (attr != null) ? attr.getValue() : null;
  }
}
