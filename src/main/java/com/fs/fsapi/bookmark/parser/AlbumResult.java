package com.fs.fsapi.bookmark.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumResult {
  
  private String videoId;

  private String artist;

  private String title;

  private Integer published;

  private String category;

  private String addDate;
}
