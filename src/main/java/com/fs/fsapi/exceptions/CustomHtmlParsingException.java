package com.fs.fsapi.exceptions;

import org.jsoup.nodes.Element;

public class CustomHtmlParsingException extends RuntimeException {
  
  private Element e;

  public CustomHtmlParsingException() {
    super();
  }

  public CustomHtmlParsingException(String message, Element e) {
    super(message);
    this.e = e;
  }

  public Element getElement() {
    return this.e;
  }
}
