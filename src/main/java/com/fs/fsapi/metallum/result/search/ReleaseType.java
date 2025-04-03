package com.fs.fsapi.metallum.result.search;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReleaseType {
  FULL_LENGTH("Full-length"),
  DEMO("Demo"),
  EP("EP"),
  COMPILATION("Compilation"),
  SINGLE("Single"),
  SPLIT("Split"),
  BOXED_SET("Boxed set"),
  COLLABORATION("Collaboration"),
  LIVE_ALBUM("Live album"),
  VIDEO("Video"),
  SPLIT_VIDEO("Split video");

  @JsonValue // serialize only the label
  public final String label;

  private static final Map<String, ReleaseType> BY_LABEL = new HashMap<>();
    
  // cache labels https://www.baeldung.com/java-enum-values#caching
  static {
    for (ReleaseType e: values()) {
      BY_LABEL.put(e.label, e);
    }
  }

  ReleaseType(String label) {
    this.label = label;
  }

  public static Optional<ReleaseType> valueOfLabel(String label) {
    return Optional.of(BY_LABEL.get(label));
  }
}
