package com.fs.fsapi.metallum.response;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import com.fs.fsapi.helpers.MetallumFileHelper;

@JsonTest
public class ArtistReleaseSearchResponseDeserializerTest {
  
  @Autowired
  private JacksonTester<ArtistTitleSearchResponse> jacksonTester;

  @Test
  public void shouldDeserializeTest() throws IOException {
    ArtistTitleSearchResponse expected = MetallumFileHelper.r1;

    ArtistTitleSearchResponse actual = jacksonTester.parseObject(
      MetallumFileHelper.readSearchResponseFile()
    );

    assertEquals(expected.getError(), actual.getError());
    assertEquals(expected.getTotalRecords(), actual.getTotalRecords());
    assertEquals(expected.getTotalDisplayRecords(), actual.getTotalDisplayRecords());

    // compare first results
    AaDataValue expectedFirst = expected.getFirstDataValue();
    AaDataValue actualFirst = actual.getFirstDataValue();

    assertEquals(
      expectedFirst.getArtistLinkElementString(),
      actualFirst.getArtistLinkElementString()
    );
    assertEquals(
      expectedFirst.getReleaseLinkElementString(),
      actualFirst.getReleaseLinkElementString()
    );
    assertEquals(
      expectedFirst.getReleaseType(),
      actualFirst.getReleaseType()
    );
  }
}

