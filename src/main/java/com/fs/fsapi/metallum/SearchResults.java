package com.fs.fsapi.metallum;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@AllArgsConstructor @NoArgsConstructor
@JsonDeserialize(using = SearchResultsDeserializer.class)
public class SearchResults {

  private String error;
  
  private int iTotalRecords;
  
  private int iTotalDisplayRecords;
  
  private int sEcho;

  private List<AaData> aaData;

}
