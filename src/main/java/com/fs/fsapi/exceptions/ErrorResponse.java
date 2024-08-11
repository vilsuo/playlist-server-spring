package com.fs.fsapi.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorResponse {
  
  private Date timestamp;

  private int code;

  private String status;

  private String message;

  private Object data;

  public ErrorResponse() {
    this.timestamp = new Date();
  }

  public ErrorResponse(HttpStatus httpStatus, String message) {
    this();
  
    this.code = httpStatus.value();
    this.status = httpStatus.name();
    this.message = message;
  }

  public ErrorResponse(
    HttpStatus httpStatus, String message, Object data
  ) {
    this(httpStatus, message);

    this.data = data;
  }
}
