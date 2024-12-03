package com.fs.fsapi.metallum.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fs.fsapi.helpers.MetallumFileHelper;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;
import com.fs.fsapi.metallum.result.ResultRanker;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

// TODO:
// remove mockWebServer

// is StepVerifier necessary? learn to use better

// TODO test
// - lyrics available, not available, instrumental
// - web client errors...

@ExtendWith(MockitoExtension.class)
public class MetallumClientServiceUnitTest {

  @Mock
  private MetallumClient client;

  @Mock
  private MetallumClientParser parser;

  @Mock
  private ArtistTitleSearchCache cache;

  @Mock
  private ResultRanker ranker;

  @InjectMocks
  private MetallumClientService service;

  @Nested
  @DisplayName("searchByArtistAndTitle")
  public class SearchByArtistAndTitle {

    @BeforeEach
    public void setUpCache() {
      // https://stackoverflow.com/a/16819818
      doAnswer(new Answer<>() {
        public Object answer(InvocationOnMock invocation) {
          Object[] args = invocation.getArguments();
          Supplier<?> supplier = (Supplier<?>) args[2];
          return supplier.get();
        }
      }).when(cache).getOrElseSupply(
        anyString(),
        anyString(),
        // https://stackoverflow.com/a/13932751
        ArgumentMatchers.<Supplier<ArtistTitleSearchResult>>any()
      ); 
    }

    private final ArtistTitleSearchResponse expectedResponse = MetallumFileHelper.SEARCH_RESPONSE;

    private final List<ArtistTitleSearchResult> expectedResults = MetallumFileHelper.SEARCH_RESULTS;
    private final ArtistTitleSearchResult expectedResult = MetallumFileHelper.SEARCH_RESULT;

    @Test
    public void shouldReturnSearchResultTest() {
      when(client.loadSearchResponse(anyString(), anyString()))
        .thenReturn(expectedResponse);

      when(parser.parseSearchResults(any(ArtistTitleSearchResponse.class)))
        .thenReturn(expectedResults);
      
      when(ranker.getBestSearchResult(anyList(), anyString(), anyString()))
        .thenReturn(expectedResult);

      // Exercise your application code, which should make those HTTP requests.
      // Responses are returned in the same order that they are enqueued.
      final String artist = MetallumFileHelper.SEARCH_ARTIST;
      final String title = MetallumFileHelper.SEARCH_TITLE;
      final ArtistTitleSearchResult actual = service.searchByArtistAndTitle(
        artist, title
      );

      // verify that mocks have been called correctly
      verify(client).loadSearchResponse(artist, title);

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

      verify(ranker).getBestSearchResult(expectedResults, artist, title);

      verify(cache).put(artist, title, expectedResult);

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

  /*
  @BeforeEach
  public void init() throws IOException {
    mockWebServer = new MockWebServer();
    final String mockBaseUrl = mockWebServer.url("/").toString();

		webClient = WebClient.builder()
			.baseUrl(mockBaseUrl)
			.build();

    service = new MetallumClientService(webClient, parser, cache);
  }

  @AfterEach
  public void tearDown() throws IOException {
    mockWebServer.close();
  }

  @Nested
  @DisplayName("searchByArtistAndTitle")
  public class SearchByArtistAndTitle {

    private final ArtistTitleSearchResponse expectedResponse = MetallumFileHelper.SEARCH_RESPONSE;

    private final List<ArtistTitleSearchResult> expectedResults = MetallumFileHelper.SEARCH_RESULTS;
    private final ArtistTitleSearchResult expectedResult = MetallumFileHelper.SEARCH_RESULT;

    @BeforeEach
    public void setUpCache() {
      // mock cache to always not find
      when(cache.get(anyString(), anyString()))
        .thenReturn(Optional.empty());
    }

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

  @Nested
  @DisplayName("getArtistLogo")
  public class LogoImage {

    @Test
    public void shouldReturnLogoImageTest() throws IOException, InterruptedException {
      final byte[] mockBody = MetallumFileHelper.readArtistLogoImage();

      final Buffer buffer = new Buffer();
      buffer.write(mockBody);

      // Schedule a response
      final MockResponse mockResponse = new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "image/jpeg")
        .setBody(buffer);

      mockWebServer.enqueue(mockResponse);

      // Exercise your application code, which should make those HTTP requests.
      // Responses are returned in the same order that they are enqueued.
      final String artistId = MetallumFileHelper.LOGO_ARTIST_ID;
      final byte[] actual = service.getArtistLogo(artistId);

      // Optional: confirm that your app made the HTTP requests you were expecting.
      final RecordedRequest req = mockWebServer.takeRequest();
      assertEquals(HttpMethod.GET.name(), req.getMethod());
      assertEquals(MetallumFileHelper.ARTIST_LOGO_PATH, req.getPath());
      assertEquals(MediaType.IMAGE_JPEG_VALUE, req.getHeader(HttpHeaders.ACCEPT));

      // Asserting response
      StepVerifier.create(Mono.just(actual))
        .expectNextMatches(image -> (image.length > 0) && (image.length == mockBody.length))
        .verifyComplete();
    } 
  }

  @Nested
  @DisplayName("getTitleCover")
  public class CoverImage {

    @Test
    public void shouldReturnCoverImageTest() throws IOException, InterruptedException {
      final byte[] mockBody = MetallumFileHelper.readTitleCoverImage();

      final Buffer buffer = new Buffer();
      buffer.write(mockBody);

      // Schedule a response
      final MockResponse mockResponse = new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "image/jpeg")
        .setBody(buffer);

      mockWebServer.enqueue(mockResponse);

      // Exercise your application code, which should make those HTTP requests.
      // Responses are returned in the same order that they are enqueued.
      final String titleId = MetallumFileHelper.TITLE_COVER_ID;
      final byte[] actual = service.getTitleCover(titleId);

      // Optional: confirm that your app made the HTTP requests you were expecting.
      final RecordedRequest req = mockWebServer.takeRequest();
      assertEquals(HttpMethod.GET.name(), req.getMethod());
      assertEquals(MetallumFileHelper.TITLE_COVER_PATH, req.getPath());
      assertEquals(MediaType.IMAGE_JPEG_VALUE, req.getHeader(HttpHeaders.ACCEPT));

      // Asserting response
      StepVerifier.create(Mono.just(actual))
        .expectNextMatches(image -> (image.length > 0) && (image.length == mockBody.length))
        .verifyComplete();
    } 
  }

  @Nested
  @DisplayName("getArtistLogoUrl")
  public class LogoUrl {

    @Test
    public void shouldCreateArtistLogoUrlFrom4DigitIdTest() {
      final String artistId = MetallumFileHelper.LOGO_ARTIST_ID;
      assertTrue(artistId.length() == 4);

      assertEquals(
        MetallumFileHelper.ARTIST_LOGO_URL,
        service.getArtistLogoUrl(artistId)
      );
    }

    @Test
    public void shouldCreateArtistLogoUrlFrom5DigitIdTest() {
      final String artistId = "24261";

      final String expected = "https://www.metal-archives.com/images/2/4/2/6/24261_logo.jpg";
      assertEquals(expected, service.getArtistLogoUrl(artistId));
    }
  }

  @Nested
  @DisplayName("getTitleCoverUrl")
  public class CoverUrl {

    @Test
    public void shouldCreateTitleCoverImageUrlFrom4DigitIdTest() {
      final String titleId = MetallumFileHelper.TITLE_COVER_ID;
      assertTrue(titleId.length() == 4);

      assertEquals(
        MetallumFileHelper.TITLE_COVER_URL,
        service.getTitleCoverUrl(titleId)
      );
    }

    @Test
    public void shouldCreateTitleCoverImageUrlFrom5DigitIdTest() {
      final String titleId = "24261";

      final String expected = "https://www.metal-archives.com/images/2/4/2/6/24261.jpg";
      assertEquals(expected, service.getTitleCoverUrl(titleId));
    }
  }
  */
}
