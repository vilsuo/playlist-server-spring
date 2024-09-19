package com.fs.fsapi.metallum.result;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class LyricsResult {
  
  private String message;
  
  private List<String> lines;

  public LyricsResult(String message) {
    this.message = message;
    lines = new ArrayList<>();
  }

  public LyricsResult(List<String> lines) {
    this.message = "";
    this.lines = lines;
  }
}
