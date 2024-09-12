package com.fs.fsapi.metallum.parser;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class LyricsResult {
  
  private String error;
  private List<String> lyrics;

  public LyricsResult(String error) {
    this.error = error;
    lyrics = new ArrayList<>();
  }

  public LyricsResult(List<String> lyrics) {
    this.error = "";
    this.lyrics = lyrics;
  }
}
