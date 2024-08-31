package com.fs.fsapi.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class CustomControllerAdvice {

  // Exception to be thrown when validation on an argument annotated with @Valid fails
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDataResponse<List<ApiValidationError>>> handleMethodArgumentNotValidExceptions(
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
      new ErrorDataResponse<>(status, message, validationErrors),
      status
    );
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorDataResponse<List<ApiValidationError>>> handleValidationException(
    HandlerMethodValidationException e
  ) {
    HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
    String message = e.getReason();
    
    List<ApiValidationError> validationErrors = e.getAllValidationResults()
      .stream()
      .map(err -> {
        String parameter = err.getMethodParameter().getParameterName();

        String validationMessage = err.getResolvableErrors()
          .stream()
          .map(MessageSourceResolvable::getDefaultMessage)
          .collect(Collectors.joining(", "));

        Object obj = err.getArgument();

        return new ApiValidationError(
          parameter,
          validationMessage,
          obj
        );
      })
      .collect(Collectors.toList());  

    return new ResponseEntity<>(
      new ErrorDataResponse<>(status, message, validationErrors),
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
  public ResponseEntity<ErrorResponse> handleCustomParameterConstraintExceptions(
    Exception e
  ) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String message = e.getMessage();
    
    log.info(message);

    return new ResponseEntity<>(
      new ErrorResponse(status, message),
      status
    );
  }

  @ExceptionHandler(CustomHtmlParsingException.class)
  public ResponseEntity<ErrorDataResponse<String>> handleCustomHtmlParsingExceptions(
    CustomHtmlParsingException e
  ) {
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    String message = e.getMessage();
    
    log.info(message);

    Element element = e.getElement();

    return new ResponseEntity<>(
      new ErrorDataResponse<>(status, message, element.toString()),
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
  
}
