package com.fs.fsapi.exceptions.response;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErrorDataResponse<T> extends ErrorResponse {

  private T data;

  public ErrorDataResponse() {
    super();
  }

  public ErrorDataResponse(
    HttpStatus httpStatus, String message, T data
  ) {
    super(httpStatus, message);

    this.data = data;
  }
}
