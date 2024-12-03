package com.fs.fsapi.metallum.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.fs.fsapi.helpers.MetallumFileHelper;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.result.ResultRanker;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

// https://jskim1991.medium.com/spring-boot-using-mockwebserver-for-integration-tests-499030f6bfff

// TODO:
// - search
//    - multiple results
//    - no results
// - other methods...


@SpringBootTest(classes = {
  CustomWebClientConfig.class,
  MetallumClient.class,
  MetallumClientParser.class,
  ArtistTitleSearchCache.class,
  ResultRanker.class,
  MetallumClientService.class,
})
public class MetallumClientServiceIntegrationTest {

  static int MOCK_SERVER_PORT;

  static {
    try (var serverSocket = new ServerSocket(0)) {
      MOCK_SERVER_PORT = serverSocket.getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @DynamicPropertySource
  public static void trackerProperties(DynamicPropertyRegistry registry) {
    registry.add("metallum.url", () -> "http://localhost:" + MOCK_SERVER_PORT);
  }

  private MockWebServer mockWebServer;

  @Autowired
  private ArtistTitleSearchCache cache;

  @Autowired
  private MetallumClientService service;

  @BeforeEach
  public void init() throws IOException {
    // start mock web server
    mockWebServer = new MockWebServer();
    mockWebServer.start(MOCK_SERVER_PORT);

    // clear cache
    cache.clear();
  }

  @AfterEach
  public void tearDown() throws IOException {
    mockWebServer.close();
  }

  @Nested
  @DisplayName("searchByArtistAndTitle")
  public class SearchByArtistAndTitle {

    @Test
    public void test() throws IOException, InterruptedException {
      final ArtistTitleSearchResult expected = MetallumFileHelper.SEARCH_RESULT;
      final String mockBody = MetallumFileHelper.readSearchResponseFile();
      
      // Schedule a response
      final MockResponse mockResponse = new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody(mockBody);

      mockWebServer.enqueue(mockResponse);

      // Exercise your application code, which should make those HTTP requests.
      // Responses are returned in the same order that they are enqueued.
      final String artist = MetallumFileHelper.SEARCH_ARTIST;
      final String title = MetallumFileHelper.SEARCH_TITLE;

      final ArtistTitleSearchResult actual = service.searchByArtistAndTitle(
        artist, 
        title
      );

      // Optional: confirm that your app made the HTTP requests you were expecting.
      final RecordedRequest req = mockWebServer.takeRequest();
      assertEquals(HttpMethod.GET.name(), req.getMethod());
      assertEquals(MetallumFileHelper.SEARCH_PATH, req.getPath());
      assertEquals(MediaType.APPLICATION_JSON_VALUE, req.getHeader(HttpHeaders.ACCEPT));

      StepVerifier.create(Mono.just(actual))
        .expectNextMatches(searchResultPredicateFactory(expected))
        .verifyComplete();
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
  }
}

