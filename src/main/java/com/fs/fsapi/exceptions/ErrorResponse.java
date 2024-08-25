package com.fs.fsapi.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErrorResponse {
  
  private Date timestamp;

  private int code;

  private String status;

  private String message;

  public ErrorResponse() {
    this.timestamp = new Date();
  }

  public ErrorResponse(HttpStatus httpStatus, String message) {
    this();
  
    this.code = httpStatus.value();
    this.status = httpStatus.name();
    this.message = message;
  }
}
