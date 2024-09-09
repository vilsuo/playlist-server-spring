package com.fs.fsapi.metallum;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = ArtistTitleSearchResponseDeserializer.class)
public class ArtistTitleSearchResponse {

  private String error;

  private int iTotalRecords;

  private int iTotalDisplayRecords;

  private int sEcho;

  private List<AaDataValue> aaData;

}
