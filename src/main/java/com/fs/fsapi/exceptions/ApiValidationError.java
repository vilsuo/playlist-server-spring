package com.fs.fsapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ApiValidationError {
  
  private String field;

  private String message;

  private Object rejectedValue;
}
