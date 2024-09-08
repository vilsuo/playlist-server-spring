package com.fs.fsapi.exceptions;

import org.apache.tika.mime.MediaType;

public class CustomInvalidMediaTypeException extends RuntimeException {
  
  public CustomInvalidMediaTypeException(MediaType actual, String expected) {
    super(
      String.format(
        "The file mediatype must be '%s', detected '%s'",
        expected,
        actual.getBaseType()
      )
    );
  }
}
