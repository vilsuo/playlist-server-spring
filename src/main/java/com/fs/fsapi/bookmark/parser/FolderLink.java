package com.fs.fsapi.bookmark.parser;

import org.jsoup.nodes.Element;

import lombok.Getter;

@Getter
public class FolderLink extends Link {

  private final String folderName;

  public FolderLink(Element e, String folderName) {
    super(e);
    this.folderName = folderName;
  }

  /**
   * 
   * @return the add_date key value, or null if not present
   */
  public String getAddDate() {
    return super.getAttributeValue("add_date");
  }
}
