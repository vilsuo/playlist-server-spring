package com.fs.fsapi.metallum.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SongResult {
  
  private String id;
  
  private String title;

  private String duration;
}
