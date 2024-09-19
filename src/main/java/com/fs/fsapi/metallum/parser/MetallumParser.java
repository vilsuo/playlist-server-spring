package com.fs.fsapi.metallum.parser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fs.fsapi.metallum.result.LyricsResult;

public abstract class MetallumParser {

  protected LyricsResult parseLyricsAvailableResult(String value, String rowSeparator) {
    final List<String> lyrics = Stream.of(value.split(rowSeparator))
      .map(row -> row.trim())
      .collect(Collectors.toList());

    return new LyricsResult(lyrics);
  }
}
