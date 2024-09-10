package com.fs.fsapi.metallum.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArtistReleaseSearchResponseDeserializer extends StdDeserializer<ArtistTitleSearchResponse> {

  // response keys
  private final String ERROR_KEY = "error";
  private final String TOTAL_RECORDS_KEY = "iTotalRecords";
  private final String TOTAL_DISPLAY_RECORDS_KEY = "iTotalRecords";
  private final String DATA_KEY = "aaData";
  //private final String ECHO_KEY = "sEcho"; // unknown value in response...

  public ArtistReleaseSearchResponseDeserializer() {
    this(null);
  }

  public ArtistReleaseSearchResponseDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public ArtistTitleSearchResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    ObjectNode obj = (ObjectNode) node;

    final String error = obj.get(ERROR_KEY).asText();
    final int totalRecords = obj.get(TOTAL_RECORDS_KEY).asInt();
    final int totalDisplayRecords = obj.get(TOTAL_DISPLAY_RECORDS_KEY).asInt();
    //final int echo = obj.get("ECHO_KEY").asInt();

    List<AaDataValue> parsedData = new ArrayList<>();

    // 2d array
    obj.get(DATA_KEY).elements().forEachRemaining(x -> {
      List<String> values = new ArrayList<>();

      x.elements().forEachRemaining(y -> {
        values.add(y.asText());
      });

      parsedData.add(new AaDataValue(values));
    });
    
    return new ArtistTitleSearchResponse(
      error,
      totalRecords,
      totalDisplayRecords,
      //echo,
      parsedData
    );
  }
}
