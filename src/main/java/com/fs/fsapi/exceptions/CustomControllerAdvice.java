package com.fs.fsapi.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class CustomControllerAdvice {

  // When a validation error occurs in a Spring application,
  // a MethodArgumentNotValidException is thrown.
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidExceptions(
    MethodArgumentNotValidException e
  ) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String message = e.getMessage();

    log.info(message);

    List<ApiValidationError> validationErrors = e.getBindingResult()  
      .getFieldErrors()  
      .stream()  
      .map(err -> new ApiValidationError(
        err.getField(),
        err.getDefaultMessage(),
        err.getRejectedValue())
      )  
      .collect(Collectors.toList());  
      
    return new ResponseEntity<>(
      new ErrorResponse(status, message, validationErrors),
      status
    );
  }
  
  @ExceptionHandler(CustomDataNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCustomDataNotFoundExceptions(Exception e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    String message = e.getMessage();

    log.info(message);

    return new ResponseEntity<>(
      new ErrorResponse(status, message),
      status
    );
  }

  @ExceptionHandler(CustomParameterConstraintException.class)
  public ResponseEntity<ErrorResponse> handleCustomParameterConstraintExceptions(Exception e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String message = e.getMessage();
    
    log.info(message);

    return new ResponseEntity<>(
      new ErrorResponse(status, message),
      status
    );
  }

  // fallback method
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleExceptions(Exception e) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String message = e.getMessage();
    
    log.error(message, e);

    return new ResponseEntity<>(
      new ErrorResponse(status, message),
      status
    );
  }

  /*
  private String getStackTrace(Exception e) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);

    return stringWriter.toString();
  }
  */
}
