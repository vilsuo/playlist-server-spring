package com.fs.fsapi.exceptions.response;

import java.util.List;

import org.springframework.http.HttpStatus;

public class ValidationErrorResponse extends ErrorDataResponse<List<ApiValidationError>> {
  
  public ValidationErrorResponse(
    HttpStatus httpStatus,
    String message,
    List<ApiValidationError> data
  ) {
    super(httpStatus, message, data);
  }
}
