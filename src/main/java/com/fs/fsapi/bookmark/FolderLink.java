package com.fs.fsapi.bookmark;

import lombok.Getter;

@Getter
public class FolderLink extends Link {
  
  private String folder;

  public FolderLink(Link link, String folder) {
    super(link.getText(), link.getHref(), link.getAddDate());

    this.folder = folder;
  }
  
}
