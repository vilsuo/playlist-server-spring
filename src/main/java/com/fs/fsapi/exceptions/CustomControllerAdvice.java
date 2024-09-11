package com.fs.fsapi.exceptions;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.fs.fsapi.exceptions.response.ApiValidationError;
import com.fs.fsapi.exceptions.response.ErrorResponse;
import com.fs.fsapi.exceptions.response.ValidationErrorResponse;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path.Node;
import lombok.extern.slf4j.Slf4j;

// TODO
// - change response messages to more user-friendly: "Validation failed" etc.

@Slf4j
@ControllerAdvice
public class CustomControllerAdvice {

  private final String FALL_BACK_MESSAGE = "Something went wrong";

  // Exception to be thrown when validation on (controller method?) an argument 
  // annotated with @Valid fails
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidExceptions(
    MethodArgumentNotValidException e
  ) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String message = e.getMessage();

    log.info("MethodArgumentNotValidException: " + message);

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
      new ValidationErrorResponse(status, message, validationErrors),
      status
    );
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ValidationErrorResponse> handleConstraintViolationExceptions(
    ConstraintViolationException e
  ) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String message = e.getMessage();

    log.info("ConstraintViolationException: " + message);

    List<ApiValidationError> validationErrors = e.getConstraintViolations()
      .stream()
      .map(err -> {
        String fieldName = "";
        Iterator<Node> iter = err.getPropertyPath().iterator();
        while(iter.hasNext()) {
          fieldName = iter.next().getName();
        }

        return new ApiValidationError(
          fieldName,
          err.getMessage(),
          err.getInvalidValue()
        );
      })
      .collect(Collectors.toList());  

    return new ResponseEntity<>(
      new ValidationErrorResponse(status, message, validationErrors),
      status
    );
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ValidationErrorResponse> handleHandlerMethodValidationExceptions(
    HandlerMethodValidationException e
  ) {
    HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
    String message = e.getReason();

    log.info("HandlerMethodValidationException: " + message);
    
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
      new ValidationErrorResponse(status, message, validationErrors),
      status
    );
  }
  
  @ExceptionHandler(CustomDataNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCustomDataNotFoundExceptions(Exception e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    String message = e.getMessage();

    log.info("CustomDataNotFoundException: " + message);

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
    
    log.info("CustomParameterConstraintException: " + message);

    return new ResponseEntity<>(
      new ErrorResponse(status, message),
      status
    );
  }

  @ExceptionHandler(CustomHtmlParsingException.class)
  public ResponseEntity<ErrorResponse> handleCustomHtmlParsingExceptions(
    CustomHtmlParsingException e
  ) {
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    String message = e.getMessage();
    
    log.info("CustomHtmlParsingException: " + message);

    return new ResponseEntity<>(
      new ErrorResponse(status, message),
      status
    );
  }

  @ExceptionHandler(CustomInvalidMediaTypeException.class)
  public ResponseEntity<ErrorResponse> handleInvalidMediaTypeExceptions(
    CustomInvalidMediaTypeException e
  ) {
    HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    String message = e.getMessage();
    
    log.info("CustomInvalidMediaTypeException: " + message);

    return new ResponseEntity<>(
      new ErrorResponse(status, message),
      status
    );
  }

  @ExceptionHandler(CustomMetallumScrapingException.class)
  public ResponseEntity<ErrorResponse> handleCustomMetallumScrapingExceptions(
    CustomMetallumScrapingException e
  ) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    
    log.error("CustomMetallumScrapingException", e);

    return new ResponseEntity<>(
      new ErrorResponse(status, FALL_BACK_MESSAGE),
      status
    );
  }

  // fallback method
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleExceptions(Exception e) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    
    log.error("Fallback error handler", e);

    return new ResponseEntity<>(
      new ErrorResponse(status, FALL_BACK_MESSAGE),
      status
    );
  }
  
}
