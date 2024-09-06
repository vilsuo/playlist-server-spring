package com.fs.fsapi.exceptions.response;

import org.springframework.http.HttpStatus;

import com.fs.fsapi.DateTimeString;

import lombok.Getter;

@Getter
public class ErrorResponse {
  
  private String timestamp;

  private int code;

  private String status;

  private String message;

  public ErrorResponse() {
    this.timestamp = DateTimeString.create();
  }

  public ErrorResponse(HttpStatus httpStatus, String message) {
    this();
  
    this.code = httpStatus.value();
    this.status = httpStatus.name();
    this.message = message;
  }
}
