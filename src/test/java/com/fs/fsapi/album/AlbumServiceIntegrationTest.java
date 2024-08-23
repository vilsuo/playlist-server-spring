package com.fs.fsapi.album;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fs.fsapi.exceptions.CustomDataNotFoundException;

import jakarta.validation.ConstraintViolationException;

@Testcontainers
@SpringBootTest
public class AlbumServiceIntegrationTest {

  @Autowired
  private AlbumRepository repository;

  @Autowired
  private AlbumService service;

  private final AlbumCreation albumValues = new AlbumCreation(
    "JMAbKMSuVfI",
    "Massacra",
    "Signs of the Decline",
    1992,
    "Death"
  );

  private final AlbumCreation newAlbumValues = new AlbumCreation(
    "qJVktESKhKY",
    "Devastation",
    "Idolatry",
    1991,
    "Thrash"
  );

  @Container
  @ServiceConnection
  public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

  @BeforeAll
  public static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  public static void afterAll() {
    postgres.stop();
  }

  @BeforeEach
  public void setUpTarget() {
    repository.deleteAll();
  }

  @Nested
  @DisplayName("findOne")
  public class FindOneTest {

    @Test
    public void canFindCreatedAlbumTest() {
      Album target = service.create(albumValues);
      Integer id = target.getId();
      
      var result = service.findOne(id);
      assertEquals(target, result);
    }

    @Test
    public void canNotFindRemovedAlbumTest() {
      Album target = service.create(albumValues);
      Integer id = target.getId();
      
      service.remove(id);

      assertThrows(
        CustomDataNotFoundException.class, 
        () -> service.findOne(id)
      );
    }

    @Test
    public void throwsIfNotFoundTest() {
      Integer id = 123;

      Exception e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.findOne(id)
      );

      assertEquals(e.getMessage(), "Album was not found");
    }

    @Test
    public void throwsWithNullIdTest() {
      Integer nullId = null;

      assertThrows(
        ConstraintViolationException.class,
        () -> service.findOne(nullId)
      );
    }
  }

  @Nested
  @DisplayName("create")
  public class CreateTest {
    
    @Test
    public void throwsWithNullParameterTest() {
      Exception e = assertThrows(
        IllegalArgumentException.class,
        () -> service.create(null)
      );

      assertEquals("Expected creation value to be present", e.getMessage());
    }

    @Test
    public void throwsWithNullObjectFieldsTest() {
      ConstraintViolationException e = assertThrows(
        ConstraintViolationException.class,
        () -> service.create(new AlbumCreation())
      );

      assertEquals(5, e.getConstraintViolations().size());

      assertTrue(e.getMessage().contains("Video id is required"));
      assertTrue(e.getMessage().contains("Artist name is required"));
      assertTrue(e.getMessage().contains("Artist name is required"));
      assertTrue(e.getMessage().contains("Title is required"));
      assertTrue(e.getMessage().contains("Publish year is required"));
      assertTrue(e.getMessage().contains("Category is required"));
    }

    @Nested
    public class Success {

      @Test
      public void returnsAlbumWithCreatedValuesTest() {
        Album result = service.create(albumValues);

        assertEquals(albumValues.getVideoId(), result.getVideoId());
        assertEquals(albumValues.getArtist(), result.getArtist());
        assertEquals(albumValues.getTitle(), result.getTitle());
        assertEquals(albumValues.getPublished(), result.getPublished());
        assertEquals(albumValues.getCategory(), result.getCategory());
      }

      @Test
      public void createsIdTest() {
        Album result = service.create(albumValues);

        assertNotNull(result.getId());
      }

      @Test
      public void createsAddDateTest() {
        Album result = service.create(albumValues);

        assertNotNull(result.getAddDate());
      }
    }
  }

  @Nested
  @DisplayName("update")
  public class UpdateTest {

    @Test
    public void throwsWhenUpdatingNonExistingAlbumTest() {
      Integer id = 123;

      Exception e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.update(id, albumValues)
      );

      assertEquals("Album was not found", e.getMessage());
    }

    @Test
    public void throwsWhenUpdatingWithNullIdTest() {
      Integer nullId = null;

      assertThrows(
        ConstraintViolationException.class,
        () -> service.update(nullId, albumValues)
      );
    }

    @Test
    public void throwsWhenUpdatingWithNullObjectTest() {
      Integer id = 123;

      Exception e = assertThrows(
        IllegalArgumentException.class,
        () -> service.update(id, null)
      );

      assertEquals("Expected update value to be present", e.getMessage());
    }

    @Test
    public void throwsWhenUpdatingWithNullObjectValuesTest() {
      Integer id = service.create(albumValues).getId();

      Exception e = assertThrows(
        ConstraintViolationException.class,
        () -> service.update(id, new AlbumCreation())
      );

      assertTrue(e.getMessage().contains("Video id is required"));
      assertTrue(e.getMessage().contains("Artist name is required"));
      assertTrue(e.getMessage().contains("Artist name is required"));
      assertTrue(e.getMessage().contains("Title is required"));
      assertTrue(e.getMessage().contains("Publish year is required"));
      assertTrue(e.getMessage().contains("Category is required"));
    }

    @Nested
    public class Success {

      private Album before;
      private Album after;

      @BeforeEach
      public void createAndUpdateAlbum() {
        before = service.create(albumValues);
        after = service.update(before.getId(), newAlbumValues);
      }

      @Test
      public void updatesWithCreationValues() {
        assertEquals(newAlbumValues.getVideoId(), after.getVideoId());
        assertEquals(newAlbumValues.getArtist(), after.getArtist());
        assertEquals(newAlbumValues.getTitle(), after.getTitle());
        assertEquals(newAlbumValues.getPublished(), after.getPublished());
        assertEquals(newAlbumValues.getCategory(), after.getCategory());
      }

      @Test
      public void doesNotUpdateIdTest() {
        assertEquals(before.getId(), after.getId());
      }

      @Test
      public void doesNotUpdateAddDateTest() {
        assertEquals(before.getAddDate(), after.getAddDate());
      }
    }

    @Test
    public void canNotUpdateRemovedAlbumTest() {
      Integer id = service.create(albumValues).getId();
      service.remove(id);

      assertThrows(
        CustomDataNotFoundException.class,
        () -> service.update(id, newAlbumValues)
      );
    }
  }

  @Nested
  @DisplayName("remove")
  public class RemoveTest {

    @Test
    public void canRemoveNonExistingAlbumTest() {
      Integer nonExistingId = 123;

      service.remove(nonExistingId);
    }

    @Test
    public void canRemoveExistingAlbumTest() {
      Album album = service.create(albumValues);

      service.remove(album.getId());
    }
  }
}
