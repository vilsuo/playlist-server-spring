package com.fs.fsapi.metallum.result;

import com.fs.fsapi.bookmark.parser.LinkElement;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ArtistTitleSearchResult {

  private LinkElement artistFolderLink;

  private LinkElement titleFolderLink;

  private String releaseType;

  public String getArtist() {
    return artistFolderLink.getText();
  }

  public String getArtistHref() {
    return artistFolderLink.getHref();
  }

  public String getArtistId() {
    String artistHref = getArtistHref();
    return artistHref.substring(artistHref.lastIndexOf("/") + 1);
  }

  public String getTitle() {
    return titleFolderLink.getText();
  }
  
  public String getTitleHref() {
    return titleFolderLink.getHref();
  }

  public String getTitleId() {
    String titleHref = getTitleHref();
    return titleHref.substring(titleHref.lastIndexOf("/") + 1);
  }

  public String getReleaseType() {
    return releaseType;
  }
}
