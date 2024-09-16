package com.fs.fsapi.metallum.response;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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

  private List<AaDataValue> aaData;
}
