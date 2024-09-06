package com.fs.fsapi.exceptions.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiValidationError {
  
  private String field;

  private String message;

  private Object rejectedValue;
}
