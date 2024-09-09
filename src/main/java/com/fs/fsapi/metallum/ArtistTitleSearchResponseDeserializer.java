package com.fs.fsapi.metallum;

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
public class ArtistTitleSearchResponseDeserializer extends StdDeserializer<ArtistTitleSearchResponse> {

  public ArtistTitleSearchResponseDeserializer() {
    this(null);
  }

  public ArtistTitleSearchResponseDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public ArtistTitleSearchResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    ObjectNode obj = (ObjectNode) node;

    final String error = obj.get("error").asText();
    final int iTotalRecords = obj.get("iTotalRecords").asInt();
    final int iTotalDisplayRecords = obj.get("iTotalDisplayRecords").asInt();
    final int sEcho = obj.get("sEcho").asInt();

    List<AaDataValue> parsedData = new ArrayList<>();
    obj.get("aaData").elements().forEachRemaining(x -> {
      List<String> values = new ArrayList<>();

      x.elements().forEachRemaining(y -> {
        values.add(y.asText());
      });

      parsedData.add(new AaDataValue(values));
    });
    
    return new ArtistTitleSearchResponse(
      error,
      iTotalRecords,
      iTotalDisplayRecords,
      sEcho,
      parsedData
    );
  }
}
