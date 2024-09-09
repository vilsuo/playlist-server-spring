package com.fs.fsapi.exceptions;

import lombok.Getter;

@Getter
public class CustomMetallumException extends RuntimeException {
  
  public CustomMetallumException(String message) {
    super(message);
  }
}
