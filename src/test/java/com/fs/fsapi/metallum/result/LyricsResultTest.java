package com.fs.fsapi.metallum.result;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class LyricsResultTest {
  
  @Test
  public void shouldHaveEmptySongLyricsWhenCreatedWithErrorTest() {
    final LyricsResult result = new LyricsResult("message");
    assertTrue(result.getLyrics().isEmpty());
  }

  @Test
  public void shouldHaveEmptyErrorWhenCreatedWithLyricsTest() {
    final LyricsResult result = new LyricsResult(List.of("lyrics"));
    assertTrue(result.getError().isEmpty());
  }
}
