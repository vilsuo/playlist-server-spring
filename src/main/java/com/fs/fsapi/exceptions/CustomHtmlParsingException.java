package com.fs.fsapi.exceptions;

import org.jsoup.nodes.Element;

public class CustomHtmlParsingException extends RuntimeException {
  
  private Element e;

  public CustomHtmlParsingException(String message, Element e) {
    super(message);
    this.e = e;
  }

  /**
   * Returns the element attached to the exception.
   */
  public Element getElement() {
    return this.e;
  }
}
