package com.fs.fsapi.album;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fs.fsapi.exceptions.response.ApiValidationError;
import com.fs.fsapi.exceptions.response.ErrorDataResponse;
import com.fs.fsapi.exceptions.response.ErrorResponse;

import static com.fs.fsapi.helpers.AlbumHelper.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class AlbumControllerIntegrationTest {

  @Autowired
  private ObjectMapper objectMapper;
  
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AlbumRepository repository;

  @Autowired
  private AlbumService service;

  @Container
  @ServiceConnection
  public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

  @BeforeAll
  public static void startContainer() {
    postgres.start();
  }

  @AfterAll
  public static void stopContainer() {
    postgres.stop();
  }

  @BeforeEach
  public void resetDb() {
    repository.deleteAll();
  }

  @Nested
  public class GetAll {

    @Test
    public void shouldReturnEmptyArrayWhenThereAreNoAlbumsTest() throws Exception {
      MvcResult result = mockMvc
        .perform(get("/albums"))
        .andExpect(status().isOk())
        .andReturn();

      List<Album> albums = getAlbumListFromResponse(result);

      assertTrue(albums.isEmpty());
    }

    @Test
    public void shouldReturnAlbumsArrayWhenThereAreAlbumsTest() throws Exception {
      final Album expected = service.create(ALBUM_CREATION_VALUE_1());

      MvcResult result = mockMvc
        .perform(get("/albums"))
        .andExpect(status().isOk())
        .andReturn();

      List<Album> albums = getAlbumListFromResponse(result);

      assertEquals(1, albums.size());
      assertEquals(expected, albums.get(0));
    }

    private List<Album> getAlbumListFromResponse(MvcResult result) throws Exception {
      final String json = result.getResponse().getContentAsString();
      CollectionType collectionType = objectMapper
        .getTypeFactory()
        .constructCollectionType(List.class, Album.class);

      return objectMapper.readValue(json, collectionType);
    }
  }

  @Nested
  public class Post {

    private final AlbumCreation source = ALBUM_CREATION_VALUE_1();

    @Test
    public void shouldReturnCreatedAlbumTest() throws Exception {
      final String content = objectMapper.writeValueAsString(source);

      MvcResult result = mockMvc
        .perform(post("/albums")
          .contentType(MediaType.APPLICATION_JSON)
          .content(content))
        .andExpect(status().isCreated())
        .andReturn();

      final Album actual = getAlbumFromResponse(result);

      assertNotNull(actual.getId());
      assertEquals(source.getArtist(), actual.getArtist());
      assertEquals(source.getTitle(), actual.getTitle());
      assertEquals(source.getPublished(), actual.getPublished());
      assertEquals(source.getCategory(), actual.getCategory());
      assertEquals(source.getVideoId(), actual.getVideoId());
      assertNotNull(actual.getAddDate());
    }

    @Test
    public void shouldReturnErrorWithMissingValuesTest() throws Exception {
      final AlbumCreation source = ALBUM_CREATION_VALUE_1();

      // do not include video id
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      source.setVideoId(null);

      final String content = objectMapper.writeValueAsString(source);

      MvcResult result = mockMvc
        .perform(post("/albums")
          .contentType(MediaType.APPLICATION_JSON)
          .content(content))
        .andExpect(status().isBadRequest())
        .andReturn();

      List<ApiValidationError> validationErrors = getValidationErrorDataResponse(result).getData();
      assertEquals(1, validationErrors.size());

      final ApiValidationError validationError = validationErrors.get(0);
      assertEquals("videoId", validationError.getField());
      assertEquals("Video id is required", validationError.getMessage());
      assertEquals(null, validationError.getRejectedValue());
    }

    @Test
    public void shouldReturnErrorWithInvalidValuesTest() throws Exception {
      final AlbumCreation source = INVALID_ALBUM_CREATION_VALUE();
      final String content = objectMapper.writeValueAsString(source);

      MvcResult result = mockMvc
        .perform(post("/albums")
          .contentType(MediaType.APPLICATION_JSON)
          .content(content))
        .andExpect(status().isBadRequest())
        .andReturn();

      List<ApiValidationError> validationErrors = getValidationErrorDataResponse(result).getData();
      assertEquals(1, validationErrors.size());
  
      final ApiValidationError validationError = validationErrors.get(0);
      final ApiValidationError expectedValidationError = INVALID_VIDEO_ID_VALIDATION_ERROR;

      assertEquals(expectedValidationError.getField(), validationError.getField());
      assertEquals(expectedValidationError.getMessage(), validationError.getMessage());
      assertEquals(expectedValidationError.getRejectedValue(), validationError.getRejectedValue());
    }
  }

  @Nested
  public class GetOne {

    @Test
    public void shouldReturnAlbumWhenItExistsTest() throws Exception {
      final Album initial = service.create(ALBUM_CREATION_VALUE_1());

      MvcResult result = mockMvc
        .perform(get("/albums/{id}", initial.getId()))
        .andExpect(status().isOk())
        .andReturn();

      final Album actual = getAlbumFromResponse(result);
      assertEquals(initial, actual);
    }

    @Test
    public void shouldReturnErrorWhenItDoesNotExistTest() throws Exception {
      final Integer id = MOCK_ID_1;

      MvcResult result = mockMvc
        .perform(get("/albums/{id}", id))
        .andExpect(status().isNotFound())
        .andReturn();

      final ErrorResponse error = getErrorFromResponse(result);
      assertEquals("Album was not found", error.getMessage());
    }
  }

  @Nested
  public class Update {

    private Album initial;

    @BeforeEach
    public void createInitial() {
      initial = service.create(ALBUM_CREATION_VALUE_1());
    }
    
    @Test
    public void shouldBeAbleToUpdateExistingAlbumTest() throws Exception {
      final AlbumCreation newValues = ALBUM_CREATION_VALUE_2();
      final String content = objectMapper.writeValueAsString(newValues);

      MvcResult result = mockMvc
        .perform(put("/albums/{id}", initial.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(content))
        .andExpect(status().isOk())
        .andReturn();
      
      final Album actual = getAlbumFromResponse(result);

      assertEquals(initial.getId(), actual.getId());
      assertEquals(newValues.getArtist(), actual.getArtist());
      assertEquals(newValues.getTitle(), actual.getTitle());
      assertEquals(newValues.getPublished(), actual.getPublished());
      assertEquals(newValues.getCategory(), actual.getCategory());
      assertEquals(newValues.getVideoId(), actual.getVideoId());
      assertEquals(initial.getAddDate(), actual.getAddDate());
    }

    @Test
    public void shouldReturnErrorWhenRequiredPropertyIsMissingTest() throws Exception {
      final AlbumCreation newValues = ALBUM_CREATION_VALUE_1();

      // do not include video id
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      newValues.setVideoId(null);

      final String content = objectMapper.writeValueAsString(newValues);

      MvcResult result = mockMvc
        .perform(put("/albums/{id}", initial.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(content))
        .andExpect(status().isBadRequest())
        .andReturn();

      List<ApiValidationError> validationErrors = getValidationErrorDataResponse(result).getData();
      assertEquals(1, validationErrors.size());

      ApiValidationError validationError = validationErrors.get(0);
      assertEquals("videoId", validationError.getField());
      assertEquals("Video id is required", validationError.getMessage());
      assertEquals(null, validationError.getRejectedValue());
    }

    @Test
    public void shouldReturnErrorWithInvalidValuesTest() throws Exception {
      final AlbumCreation newValues = INVALID_ALBUM_CREATION_VALUE();
      final String content = objectMapper.writeValueAsString(newValues);

      MvcResult result = mockMvc
        .perform(put("/albums/{id}", initial.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(content))
        .andExpect(status().isBadRequest())
        .andReturn();

      List<ApiValidationError> validationErrors = getValidationErrorDataResponse(result).getData();
      assertEquals(1, validationErrors.size());
  
      final ApiValidationError validationError = validationErrors.get(0);
      final ApiValidationError expectedValidationError = INVALID_VIDEO_ID_VALIDATION_ERROR;

      assertEquals(expectedValidationError.getField(), validationError.getField());
      assertEquals(expectedValidationError.getMessage(), validationError.getMessage());
      assertEquals(expectedValidationError.getRejectedValue(), validationError.getRejectedValue());
    }

    @Test
    public void shouldReturnErrorWhenAlbumDoesNotExistTest() throws Exception {
      final AlbumCreation newValues = ALBUM_CREATION_VALUE_2();
      final String content = objectMapper.writeValueAsString(newValues);

      final Integer nonExistingId = initial.getId() + 1;
      MvcResult result = mockMvc
        .perform(put("/albums/{id}", nonExistingId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(content))
        .andExpect(status().is4xxClientError())
        .andReturn();

      ErrorResponse error = getErrorFromResponse(result);
      assertEquals("Album was not found", error.getMessage());
    }
  }

  @Nested
  public class Delete {
    
    @Test
    public void shouldBeAbleToDeleteExistingAlbumTest() throws Exception {
      final Album initial = service.create(ALBUM_CREATION_VALUE_1());
      final Integer id = initial.getId();

      mockMvc
        .perform(delete("/albums/{id}", id))
        .andExpect(status().isNoContent())
        .andReturn();
    }

    @Test
    public void shouldBeAbleToTryToDeleteNonExistingAlbumTest() throws Exception {
      final Integer id = MOCK_ID_1;

      mockMvc
        .perform(delete("/albums/{id}", id))
        .andExpect(status().isNoContent())
        .andReturn();
    }
  }

  @Nested
  public class Download {

    @Test
    public void shouldAttachContentDispositionHeaderTest() throws Exception {
      MvcResult result = mockMvc
        .perform(get("/albums/download"))
        .andExpect(status().isOk())
        .andReturn();

      assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));
    }
  }

  private Album getAlbumFromResponse(MvcResult result) throws Exception {
    final String json = result.getResponse().getContentAsString();
    return objectMapper.readValue(json, Album.class);
  }

  private ErrorResponse getErrorFromResponse(MvcResult result) throws Exception {
    final String json = result.getResponse().getContentAsString();
    return objectMapper.readValue(json, ErrorResponse.class);
  }

  private ErrorDataResponse<List<ApiValidationError>> getValidationErrorDataResponse(MvcResult result) throws Exception {
    CollectionType collectionType = objectMapper
      .getTypeFactory()
      .constructCollectionType(List.class, ApiValidationError.class);

    JavaType errorType = objectMapper
      .getTypeFactory()
      .constructParametricType(ErrorDataResponse.class, collectionType);

    final String json = result.getResponse().getContentAsString();
    return objectMapper.readValue(json, errorType);
  }
}
