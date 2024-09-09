package com.fs.fsapi.metallum;

import com.fs.fsapi.bookmark.parser.Link;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SearchLink {

  private Link artist;

  private Link title;

  public String getArtistHref() {
    return artist.getHref();
  }

  public String getTitleHref() {
    return title.getHref();
  }
}
