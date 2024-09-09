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
public class SearchResultsDeserializer extends StdDeserializer<SearchResults> {

  public SearchResultsDeserializer() {
    this(null);
  }

  public SearchResultsDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public SearchResults deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    ObjectNode obj = (ObjectNode) node;

    final String error = obj.remove("error").asText();
    final int iTotalRecords = obj.remove("iTotalRecords").asInt();
    final int iTotalDisplayRecords = obj.remove("iTotalDisplayRecords").asInt();
    final int sEcho = obj.remove("sEcho").asInt();

    List<AaData> parsedFields = new ArrayList<>();

    obj.fields().forEachRemaining(fieldAndNode -> {
      final String fieldName = fieldAndNode.getKey();

      if (fieldName.equals("aaData")) {
        List<String> values = new ArrayList<>();
        fieldAndNode.getValue().elements().forEachRemaining(x -> {
          x.elements().forEachRemaining(y -> {
            values.add(y.asText());
          });
        });

        parsedFields.add(new AaData(values));
      }
    });

    return new SearchResults(
      error,
      iTotalRecords,
      iTotalDisplayRecords,
      sEcho,
      parsedFields
    );
  }
}
