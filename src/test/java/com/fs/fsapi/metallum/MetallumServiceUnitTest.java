package com.fs.fsapi.metallum;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.fs.fsapi.helpers.MetallumFileHelper;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.parser.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.parser.MetallumParser;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class MetallumServiceUnitTest {

  private MockWebServer mockWebServer;

  private WebClient webClient;

  @Mock
  private MetallumParser parser;

  @Mock
  private ArtistTitleSearchCache cache;

  private MetallumService service;

  @BeforeEach
  public void init() throws IOException {
    mockWebServer = new MockWebServer();
		webClient = WebClient.builder()
			.baseUrl(mockWebServer.url("/").toString())
			.build();

    service = new MetallumService(webClient, parser, cache);
  }

  @AfterEach
  public void tearDown() throws IOException {
    mockWebServer.close();
  }

  @Nested
  @DisplayName("searchByArtistAndTitle")
  public class SearchByArtistAndTitle {

    private final ArtistTitleSearchResponse expectedResponse = MetallumFileHelper.SEARCH_RESPONSE;

    private final ArtistTitleSearchResult expectedResult = MetallumFileHelper.SEARCH_RESULT;

    @BeforeEach
    public void setUpCache() {
      // mock cache to always not find
      when(cache.get(anyString(), anyString()))
        .thenReturn(Optional.empty());
    }

    @Test
    public void test() throws IOException, InterruptedException {
      final String mockBody = MetallumFileHelper.readSearchResponseFile();
      
      // Schedule a response
      final MockResponse mockResponse = new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody(mockBody);

      mockWebServer.enqueue(mockResponse);

      // Exercise your application code, which should make those HTTP requests.
      // Responses are returned in the same order that they are enqueued.
      final String artist = "Adramelech";
      final String title = "Psychostasia";

      when(parser.getSearchResult(
          any(ArtistTitleSearchResponse.class),
          anyString(),
          anyString()))
        .thenReturn(expectedResult);

      final ArtistTitleSearchResult actual = service.searchByArtistAndTitle(
        artist, 
        title
      );

      verify(parser).getSearchResult(
        argThat((response) -> response.getError().equals(expectedResponse.getError())
          && response.getTotalRecords() == expectedResponse.getTotalRecords()
          && response.getTotalDisplayRecords() == expectedResponse.getTotalDisplayRecords()
          && response.getAaData().size() == expectedResponse.getAaData().size()
          && IntStream.range(0, response.getAaData().size())
              .filter(i -> expectedResponse.getAaData().get(i)
                .equals(response.getAaData().get(i)))
              .count() == expectedResponse.getAaData().size()
        ),
        eq(artist),
        eq(title)
      );

      StepVerifier.create(Mono.just(actual))
        .expectNextMatches(getPredicate(expectedResult))
        .verifyComplete();
    } 
  }

  public Predicate<ArtistTitleSearchResult> getPredicate(ArtistTitleSearchResult expected) {
    return new Predicate<ArtistTitleSearchResult>() {

      @Override
      public boolean test(ArtistTitleSearchResult actual) {
        return actual.getArtist().equals(expected.getArtist())
            && actual.getArtistHref().equals(expected.getArtistHref())
            && actual.getArtistId().equals(expected.getArtistId())
            && actual.getTitle().equals(expected.getTitle())
            && actual.getTitleHref().equals(expected.getTitleHref())
            && actual.getTitleId().equals(expected.getTitleId())
            && actual.getReleaseType().equals(expected.getReleaseType());
      }
    };
  }


  /*
  @Nested
  @DisplayName("createArtistLogoUrl")
  public class searchArtistLogo {

    @Test
    public void test() throws IOException, InterruptedException {
      final byte[] expected = MetallumFileHelper.readArtistLogoImage();

      var expectedBody = "hello";

      mockMetallumServer.enqueue(new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "image/jpeg")
        .setBody(expectedBody)
      );

      // Asserting response
      StepVerifier.create(service.searchArtistLogo(artistId))
        .assertNext(res -> {
          assertNotNull(res);
          assertEquals("value for y", res.getY());
          assertEquals("789", res.getZ());
        })
        .verifyComplete();

      // Asserting request
      RecordedRequest recordedRequest = mockMetallumServer.takeRequest();
      // use method provided by MockWebServer to assert the request header
      recordedRequest.getHeader("Authorization").equals("customAuth");
      DocumentContext context = JsonPath.parse(recordedRequest.getBody().inputStream());
      // use JsonPath library to assert the request body
      assertThat(context, isJson(allOf(
              withJsonPath("$.a", is("value1")),
              withJsonPath("$.b", is(123))

      final byte[] actual = service.searchArtistLogo(artistId);
    } 
  }

  @Nested
  @DisplayName("image urls")
  public class ImageUrls {

    @Test
    public void shouldCreateArtistLogoUrl() {
      assertEquals(expectedArtistLogoUrl, service.createArtistLogoUrl(artistId));
    } 
  }
  */
}
