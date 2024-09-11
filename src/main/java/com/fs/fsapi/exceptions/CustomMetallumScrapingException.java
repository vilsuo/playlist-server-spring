package com.fs.fsapi.exceptions;

import lombok.Getter;

/**
 * Thrown when a Metallum related scraping response has an unexpected structure.
 */
@Getter
public class CustomMetallumScrapingException extends RuntimeException {
  
  public CustomMetallumScrapingException(String message) {
    super(message);
  }
}