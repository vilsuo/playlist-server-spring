package com.fs.fsapi.metallum.result;

import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

import com.fs.fsapi.exceptions.CustomMetallumScrapingException;

import lombok.Getter;

@Getter
public class ResultImage {

  private final MediaType mediaType;

  private final byte[] bytes;

  public ResultImage(byte[] bytes, MediaType mediaType) {
    if (!mediaType.getType().equals("image")) {
      throw new CustomMetallumScrapingException(
        "Unexpected media type '" + mediaType.getType() + "/" + mediaType.getSubtype() + "'"
      );
    }

    this.bytes = bytes;
    this.mediaType = mediaType;
  }

  public ResultImage(byte[] bytes, String subtype) {
    try {
      this.mediaType = MediaType.valueOf("image/" + subtype);
      this.bytes = bytes;
      
    } catch(InvalidMediaTypeException e) {
      throw new CustomMetallumScrapingException("Invalid media subtype '" + subtype + "'");
    }
  }
}
