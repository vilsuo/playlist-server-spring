package com.fs.fsapi.metallum.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SongResult {
  
  private String id;
  
  private String title;

  private String duration;
}
