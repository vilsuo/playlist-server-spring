package com.fs.fsapi;

import java.time.Instant;

public class DateTimeString {

  public static String create() {
    //Instant now = Instant.now();
    //ZonedDateTime zdt = now.atZone(ZoneId.of("Z"));
    //DateTimeFormatter f = DateTimeFormatter
    //  .ofLocalizedDateTime(FormatStyle.FULL)
    //  .withLocale(Locale.CANADA_FRENCH); 
    //
    //String outputFormatted = zdt.format(f) ;

    return Instant.now().toString();
  }

  public static String parse(String epochSeconds) {
    if (epochSeconds == null) {
      throw new IllegalArgumentException(
        "Can not convert null to date string"
      );
    }

    int utcSeconds = Integer.parseInt(epochSeconds);
    return convertDate(utcSeconds);
  }

  private static String convertDate(int epochSeconds) {
    return Instant.ofEpochSecond(epochSeconds).toString();
  }
}
