package com.fs.fsapi.metallum;

import com.fs.fsapi.bookmark.parser.LinkElement;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ArtistReleaseSearchResult {

  private LinkElement artistFolderLink;

  private LinkElement releaseFolderLink;

  private String releaseType;

  public String getArtistHref() {
    return artistFolderLink.getHref();
  }

  public String getArtist() {
    return artistFolderLink.getText();
  }

  public String getReleaseHref() {
    return releaseFolderLink.getHref();
  }

  public String getRelease() {
    return releaseFolderLink.getText();
  }

  public String getReleaseType() {
    return releaseType;
  }
}
