package com.fs.fsapi.metallum.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import com.fs.fsapi.helpers.MetallumFileHelper;

@JsonTest
public class ArtistTitleSearchResponseDeserializerTest {
  
  @Autowired
  private JacksonTester<ArtistTitleSearchResponse> jacksonTester;

  private final ArtistTitleSearchResponse expected = MetallumFileHelper.SEARCH_RESPONSE;

  @Test
  public void shouldDeserializeTest() throws IOException {
    final ArtistTitleSearchResponse actual = jacksonTester
      .parseObject(MetallumFileHelper.readSearchResponseFile());

    assertEquals(expected.getError(), actual.getError());
    assertEquals(expected.getTotalRecords(), actual.getTotalRecords());
    assertEquals(expected.getTotalDisplayRecords(), actual.getTotalDisplayRecords());

    assertFalse(expected.getAaData().isEmpty());
    assertIterableEquals(expected.getAaData(), actual.getAaData());
  }
}

