package com.fs.fsapi.metallum.response;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = ArtistTitleSearchResponseDeserializer.class)
public class ArtistTitleSearchResponse {

  private String error;

  private int totalRecords;

  private int totalDisplayRecords;

  //private int sEcho;

  @Getter(AccessLevel.NONE)
  private List<AaDataValue> aaData;

  public AaDataValue getFirstDataValue() {
    return this.aaData.getFirst();
  }
}
