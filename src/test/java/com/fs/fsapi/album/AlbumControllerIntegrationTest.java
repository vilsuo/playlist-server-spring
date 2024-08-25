package com.fs.fsapi.album;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import com.fs.fsapi.exceptions.ApiValidationError;
import com.fs.fsapi.exceptions.ErrorDataResponse;
import com.fs.fsapi.exceptions.ErrorResponse;

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

  private static AlbumCreation createNewSource() {
    return new AlbumCreation(
      "JMAbKMSuVfI",
      "Massacra",
      "Signs of the Decline",
      1992,
      "Death"
    );
  }

  private static AlbumCreation createNewValues() {
    return new AlbumCreation(
      "qJVktESKhKY",
      "Devastation",
      "Idolatry",
      1991,
      "Thrash"
    );
  }

  private final String invalidVideoId = "thisisinvalid";

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
      Album initial = service.create(createNewSource());

      MvcResult result = mockMvc
        .perform(get("/albums"))
        .andExpect(status().isOk())
        .andReturn();

      List<Album> albums = getAlbumListFromResponse(result);

      assertEquals(1, albums.size());
      assertEquals(initial, albums.get(0));
    }

    private List<Album> getAlbumListFromResponse(MvcResult result) throws Exception {
      String json = result.getResponse().getContentAsString();
      CollectionType collectionType = objectMapper
        .getTypeFactory()
        .constructCollectionType(List.class, Album.class);

      return objectMapper.readValue(json, collectionType);
    }
  }

  @Nested
  public class Post {

    @Test
    public void shouldReturnCreatedAlbumTest() throws Exception {
      AlbumCreation source = createNewSource();
      String content = objectMapper.writeValueAsString(source);

      MvcResult result = mockMvc
        .perform(post("/albums")
          .contentType(MediaType.APPLICATION_JSON)
          .content(content)
        )
        .andDo(print())
        .andExpect(status().isCreated())
        .andReturn();

      Album album = getAlbumFromResponse(result);

      assertNotNull(album.getId());
      assertEquals(source.getArtist(), album.getArtist());
      assertEquals(source.getTitle(), album.getTitle());
      assertEquals(source.getPublished(), album.getPublished());
      assertEquals(source.getCategory(), album.getCategory());
      assertEquals(source.getVideoId(), album.getVideoId());
      assertNotNull(album.getAddDate());
    }

    @Test
    public void shouldReturnErrorWithMissingValuesTest() throws Exception {
      AlbumCreation source = createNewSource();

      // do not include video id
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      source.setVideoId(null);

      String content = objectMapper.writeValueAsString(source);

      MvcResult result = mockMvc
        .perform(post("/albums")
          .contentType(MediaType.APPLICATION_JSON)
          .content(content)
        )
        .andExpect(status().isBadRequest())
        .andReturn();

      var validationErrors = getValidationErrorDataResponse(result).getData();
      assertEquals(1, validationErrors.size());

      ApiValidationError validationError = validationErrors.get(0);
      assertEquals("videoId", validationError.getField());
      assertEquals("Video id is required", validationError.getMessage());
    }

    @Test
    public void shouldReturnErrorWithInvalidValuesTest() throws Exception {
      AlbumCreation source = createNewSource();

      // include invalid video id
      source.setVideoId(invalidVideoId);
      
      String content = objectMapper.writeValueAsString(source);

      MvcResult result = mockMvc
        .perform(post("/albums")
          .contentType(MediaType.APPLICATION_JSON)
          .content(content)
        )
        .andExpect(status().isBadRequest())
        .andReturn();

      var validationErrors = getValidationErrorDataResponse(result).getData();
      assertEquals(1, validationErrors.size());
  
      ApiValidationError validationError = validationErrors.get(0);
      assertEquals("videoId", validationError.getField());
      assertEquals("The video id must be 11 characters long", validationError.getMessage());
    }
  }

  @Nested
  public class GetOne {

    @Test
    public void shouldReturnAlbumWhenItExistsTest() throws Exception {
      Album initial = service.create(createNewSource());

      MvcResult result = mockMvc
        .perform(get("/albums/{id}", initial.getId()))
        .andExpect(status().isOk())
        .andReturn();

      Album album = getAlbumFromResponse(result);
      assertEquals(initial, album);
    }

    @Test
    public void shouldReturnErrorWhenItDoesNotExistTest() throws Exception {
      Integer id = 123;

      MvcResult result = mockMvc
        .perform(get("/albums/{id}", id))
        .andExpect(status().isNotFound())
        .andReturn();

      ErrorResponse error = getErrorFromResponse(result);
      assertEquals("Album was not found", error.getMessage());
    }
  }

  @Nested
  public class Update {

    Album initial;

    @BeforeEach
    public void createInitial() {
      initial = service.create(createNewSource());
    }
    
    @Test
    public void shouldBeAbleToUpdateExistingAlbumTest() throws Exception {
        AlbumCreation newValues = createNewValues();
        String content = objectMapper.writeValueAsString(newValues);

        MvcResult result = mockMvc
          .perform(put("/albums/{id}", initial.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)
          )
          .andExpect(status().isOk())
          .andReturn();
        
        Album album = getAlbumFromResponse(result);

        assertEquals(initial.getId(), album.getId());
        assertEquals(newValues.getArtist(), album.getArtist());
        assertEquals(newValues.getTitle(), album.getTitle());
        assertEquals(newValues.getPublished(), album.getPublished());
        assertEquals(newValues.getCategory(), album.getCategory());
        assertEquals(newValues.getVideoId(), album.getVideoId());
        assertEquals(initial.getAddDate(), album.getAddDate());
      }

    @Test
    public void shouldReturnErrorWithMissingValuesTest() throws Exception {
      AlbumCreation source = createNewSource();

      // do not include video id
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      source.setVideoId(null);

      String content = objectMapper.writeValueAsString(source);

      MvcResult result = mockMvc
        .perform(put("/albums/{id}", initial.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(content)
        )
        .andExpect(status().isBadRequest())
        .andReturn();

      var validationErrors = getValidationErrorDataResponse(result).getData();
      assertEquals(1, validationErrors.size());

      ApiValidationError validationError = validationErrors.get(0);
      assertEquals("videoId", validationError.getField());
      assertEquals("Video id is required", validationError.getMessage());
    }

    @Test
    public void shouldReturnErrorWithInvalidValuesTest() throws Exception {
      AlbumCreation source = createNewSource();

      // include invalid video id
      source.setVideoId(invalidVideoId);
      
      String content = objectMapper.writeValueAsString(source);

      MvcResult result = mockMvc
        .perform(put("/albums/{id}", initial.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(content)
        )
        .andExpect(status().isBadRequest())
        .andReturn();

      var validationErrors = getValidationErrorDataResponse(result).getData();
      assertEquals(1, validationErrors.size());
  
      ApiValidationError validationError = validationErrors.get(0);
      assertEquals("videoId", validationError.getField());
      assertEquals("The video id must be 11 characters long", validationError.getMessage());
    }
  }

  @Nested
  public class Delete {
    
    @Test
    public void shouldBeAbleToDeleteExistingAlbumTest() throws Exception {
      Album initial = service.create(createNewSource());
      Integer id = initial.getId();

      mockMvc
        .perform(delete("/albums/{id}", id))
        .andExpect(status().isNoContent())
        .andReturn();
    }

    @Test
    public void shouldBeAbleToTryToDeleteNonExistingAlbumTest() throws Exception {
      Integer id = 123;

      mockMvc
        .perform(delete("/albums/{id}", id))
        .andExpect(status().isNoContent())
        .andReturn();
    }
  }

  private Album getAlbumFromResponse(MvcResult result) throws Exception {
    String json = result.getResponse().getContentAsString();
    return objectMapper.readValue(json, Album.class);
  }

  private ErrorResponse getErrorFromResponse(MvcResult result) throws Exception {
    String json = result.getResponse().getContentAsString();
    return objectMapper.readValue(json, ErrorResponse.class);
  }

  private ErrorDataResponse<List<ApiValidationError>> getValidationErrorDataResponse(MvcResult result) throws Exception {
    CollectionType collectionType = objectMapper
      .getTypeFactory()
      .constructCollectionType(List.class, ApiValidationError.class);

    JavaType errorType = objectMapper
      .getTypeFactory()
      .constructParametricType(ErrorDataResponse.class, collectionType);

    String json = result.getResponse().getContentAsString();
    return objectMapper.readValue(json, errorType);
  }
}
