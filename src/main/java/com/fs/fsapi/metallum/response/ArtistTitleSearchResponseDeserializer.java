package com.fs.fsapi.metallum.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fs.fsapi.exceptions.CustomMetallumScrapingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArtistTitleSearchResponseDeserializer extends StdDeserializer<ArtistTitleSearchResponse> {

  // response properties
  private final String ERROR_PROPERTY = "error"; 
  private final String TOTAL_RECORDS_PROPERTY = "iTotalRecords";
  private final String TOTAL_DISPLAY_RECORDS_PROPERTY = "iTotalRecords";
  private final String DATA_PROPERTY = "aaData";
  //private final String ECHO_PROPERTY = "sEcho"; // unknown int value in response...

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

    JsonNode x = getProperty(obj, DATA_PROPERTY);
    if (!x.isArray()) {
      throw new CustomMetallumScrapingException(
        "Expected property '" + DATA_PROPERTY + "' value '"
        + x.toPrettyString() + "' to be an array"
      );
    }

    // 2d array
    Iterator<JsonNode> xItr = x.iterator();
    List<AaDataValue> dataList = new ArrayList<>();
    while (xItr.hasNext()) {
      JsonNode y = xItr.next();
      if (!y.isArray() || y.size() != 3) {
        throw new CustomMetallumScrapingException(
          "Expected the value '" + y.toPrettyString() + "' of property '"
          + DATA_PROPERTY + "' be an array of length of 3"
        );
      }

      Iterator<JsonNode> yItr = y.iterator();
      List<String> dataRow = new ArrayList<>();
      while (yItr.hasNext()) {
        JsonNode dataNode = yItr.next();
        dataRow.add(dataNode.asText());
      }

      dataList.add(new AaDataValue(dataRow));
    }
    
    return new ArtistTitleSearchResponse(
      asText(obj, ERROR_PROPERTY),
      asInt(obj, TOTAL_RECORDS_PROPERTY),
      asInt(obj, TOTAL_DISPLAY_RECORDS_PROPERTY),
      dataList
    );
  }

  private String asText(ObjectNode obj, String propertyName) {
    final JsonNode node = getProperty(obj, propertyName);
    if (!node.isTextual()) {
      throw new CustomMetallumScrapingException(
        "Expected the property '" + propertyName + "' value '"
        + node.toPrettyString() + "' to be a basic string value"
      );
    }

    return node.asText();
  }

  private int asInt(ObjectNode obj, String propertyName) {
    final JsonNode node = getProperty(obj, propertyName);
    if (!node.isInt()) {
      throw new CustomMetallumScrapingException(
        "Expected the property '" + propertyName + "' value '"
        + node.toPrettyString() + "' to be presentable as int"
      );
    }

    return node.asInt();
  }

  private JsonNode getProperty(ObjectNode obj, String propertyName) {
    if (!obj.has(propertyName)) {
      throw new CustomMetallumScrapingException(
        "Expected the object '" + obj.toPrettyString()
        + "' to have property '" + propertyName + "'"
      );
    }
    return obj.get(propertyName);
  }
}
