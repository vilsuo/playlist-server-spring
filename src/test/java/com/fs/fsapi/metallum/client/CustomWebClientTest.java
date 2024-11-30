package com.fs.fsapi.metallum.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fs.fsapi.helpers.MetallumFileHelper;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CustomWebClientTest {

  private MockWebServer mockWebServer;

  private WebClient webClient;

  private CustomWebClient client;

  @BeforeEach
  public void init() throws IOException {
    mockWebServer = new MockWebServer();
    final String baseUrl = mockWebServer.url("/").toString();
		webClient = WebClient.builder()
			.baseUrl(baseUrl)
			.build();

      client = new CustomWebClient(baseUrl, webClient);
  }

  @AfterEach
  public void tearDown() throws IOException {
    mockWebServer.close();
  }

  /*
  @Nested
  @DisplayName("searchByArtistAndTitle")
  public class SearchByArtistAndTitle {

    private final ArtistTitleSearchResponse expectedResponse = MetallumFileHelper.SEARCH_RESPONSE;

    private final List<ArtistTitleSearchResult> expectedResults = MetallumFileHelper.SEARCH_RESULTS;
    private final ArtistTitleSearchResult expectedResult = MetallumFileHelper.SEARCH_RESULT;

    @Test
    public void shouldReturnSearchResultTest() throws IOException, InterruptedException {
      when(parser.parseSearchResults(any(ArtistTitleSearchResponse.class)))
        .thenReturn(expectedResults);

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
      final ArtistTitleSearchResult actual = service.searchByArtistAndTitle(
        artist, title
      );

      verify(parser).parseSearchResults(
        argThat((response) -> response.getError().equals(expectedResponse.getError())
          && response.getTotalRecords() == expectedResponse.getTotalRecords()
          && response.getTotalDisplayRecords() == expectedResponse.getTotalDisplayRecords()
          && response.getAaData().size() == expectedResponse.getAaData().size()
          && IntStream.range(0, response.getAaData().size())
              .filter(i -> expectedResponse.getAaData().get(i)
                .equals(response.getAaData().get(i)))
              .count() == expectedResponse.getAaData().size()
        )
      );

      // Optional: confirm that your app made the HTTP requests you were expecting.
      final RecordedRequest req = mockWebServer.takeRequest();
      assertEquals(HttpMethod.GET.name(), req.getMethod());
      assertTrue(req.getPath().startsWith("/search/ajax-advanced/searching/albums"));
      assertTrue(req.getPath().contains("bandName=" + artist));
      assertTrue(req.getPath().contains("releaseTitle=" + title));
      assertEquals(MediaType.APPLICATION_JSON_VALUE, req.getHeader(HttpHeaders.ACCEPT));

      StepVerifier.create(Mono.just(actual))
        .expectNextMatches(searchResultPredicateFactory(expectedResult))
        .verifyComplete();
    } 
  }

  public Predicate<ArtistTitleSearchResult> searchResultPredicateFactory(ArtistTitleSearchResult expected) {
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
  */
}
