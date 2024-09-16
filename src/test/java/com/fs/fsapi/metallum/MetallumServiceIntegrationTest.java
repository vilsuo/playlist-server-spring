package com.fs.fsapi.metallum;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.fs.fsapi.config.CustomWebClientConfig;
import com.fs.fsapi.helpers.MetallumFileHelper;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.parser.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.parser.MetallumParser;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

// https://jskim1991.medium.com/spring-boot-using-mockwebserver-for-integration-tests-499030f6bfff

@SpringBootTest(classes = {
  CustomWebClientConfig.class,
  MetallumParser.class,
  ArtistTitleSearchCache.class,
  MetallumService.class,
})
public class MetallumServiceIntegrationTest {

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
  private MetallumService service;

  @BeforeEach
  public void init() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start(MOCK_SERVER_PORT);
  }

  @AfterEach
  public void tearDown() throws IOException {
    mockWebServer.close();
  }

  @Nested
  @DisplayName("searchByArtistAndTitle")
  public class SearchByArtistAndTitle {

    @BeforeEach
    public void setUpCache() {
      cache.clear();
    }

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
      final String artist = "Adramelech";
      final String title = "Psychostasia";

      final ArtistTitleSearchResult actual = service.searchByArtistAndTitle(
        artist, 
        title
      );

      StepVerifier.create(Mono.just(actual))
        .expectNextMatches(result -> {
          return result.getArtist().equals(expected.getArtist())
            && result.getArtistHref().equals(expected.getArtistHref())
            && result.getArtistId().equals(expected.getArtistId())
            && result.getTitle().equals(expected.getTitle())
            && result.getTitleHref().equals(expected.getTitleHref())
            && result.getTitleId().equals(expected.getTitleId())
            && result.getReleaseType().equals(expected.getReleaseType());
        })
        .verifyComplete();
    } 
  }
}

