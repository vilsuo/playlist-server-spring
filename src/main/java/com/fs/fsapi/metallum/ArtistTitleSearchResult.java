package com.fs.fsapi.metallum;

import com.fs.fsapi.bookmark.parser.LinkElement;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ArtistTitleSearchResult {

  private LinkElement artistFolderLink;

  private LinkElement titleFolderLink;

  private String albumType;

  public String getArtistHref() {
    return artistFolderLink.getHref();
  }

  public String getArtist() {
    return artistFolderLink.getText();
  }

  public String getTitleHref() {
    return titleFolderLink.getHref();
  }

  public String getTitle() {
    return titleFolderLink.getText();
  }

  public String getAlbumType() {
    return albumType;
  }
}
