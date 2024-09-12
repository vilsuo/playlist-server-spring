package com.fs.fsapi.album;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.TransactionSystemException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fs.fsapi.bookmark.parser.AlbumParseResult;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;

import jakarta.persistence.RollbackException;
import jakarta.validation.ConstraintViolation;
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
    public void shouldFindCreatedAlbumTest() {
      Album target = service.create(albumValues);
      Integer id = target.getId();
      
      var result = service.findOne(id);
      assertEquals(target, result);
      assertEquals(id, result.getId());
    }

    @Test
    public void shouldFindUpdatedAlbumTest() {
      Album before = service.create(albumValues);
      final Integer id = before.getId();
      
      Album after = service.update(id, newAlbumValues);

      Album found = service.findOne(id);
      assertNotEquals(before, found);
      assertEquals(after, found);
    }

    @Test
    public void shouldNotFindRemovedAlbumTest() {
      Album target = service.create(albumValues);
      Integer id = target.getId();
      
      service.remove(id);

      assertThrows(
        CustomDataNotFoundException.class, 
        () -> service.findOne(id)
      );
    }

    @Test
    public void shouldThrowWhenAlbumIsNotFoundTest() {
      Integer id = 123;

      CustomDataNotFoundException e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.findOne(id)
      );

      assertEquals(e.getMessage(), "Album was not found");
    }

    @Test
    public void shouldThrowWhenParameterIsNullTest() {
      Integer nullId = null;

      ConstraintViolationException ex = assertThrows(
        ConstraintViolationException.class,
        () -> service.findOne(nullId)
      );

      assertEquals(1, ex.getConstraintViolations().size());

      ConstraintViolation<?> violation = new ArrayList<>(ex.getConstraintViolations()).get(0);
      assertEquals("must not be null", violation.getMessage());
      assertEquals(nullId, violation.getInvalidValue());
    }
  }

  @Nested
  @DisplayName("create")
  public class CreateTest {
    
    @Test
    public void shouldThrowWhenParameterIsNullTest() {
      IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> service.create(null)
      );

      assertEquals("Expected creation value to be present", e.getMessage());
    }

    // how to handle validation?
    @Test
    public void shouldThrowWhenCreationValuesAreInvalidTest() {
      ConstraintViolationException ex = assertThrows(
        ConstraintViolationException.class,
        () -> service.create(new AlbumCreation())
      );

      assertFalse(ex.getConstraintViolations().isEmpty());

      //assertEquals(5, ex.getConstraintViolations().size());
      //
      //assertTrue(ex.getMessage().contains("Video id is required"));
      //assertTrue(ex.getMessage().contains("Artist name is required"));
      //assertTrue(ex.getMessage().contains("Artist name is required"));
      //assertTrue(ex.getMessage().contains("Title is required"));
      //assertTrue(ex.getMessage().contains("Publish year is required"));
      //assertTrue(ex.getMessage().contains("Category is required"));
    }

    @Nested
    public class Success {

      @Test
      public void shouldReturnAlbumWithCreatedValuesTest() {
        Album result = service.create(albumValues);

        assertEquals(albumValues.getVideoId(), result.getVideoId());
        assertEquals(albumValues.getArtist(), result.getArtist());
        assertEquals(albumValues.getTitle(), result.getTitle());
        assertEquals(albumValues.getPublished(), result.getPublished());
        assertEquals(albumValues.getCategory(), result.getCategory());
      }

      @Test
      public void shouldReturnAlbumWithIdTest() {
        Album result = service.create(albumValues);

        assertNotNull(result.getId());
      }

      @Test
      public void shouldReturnAlbumWithAddDateTest() {
        Album result = service.create(albumValues);

        assertNotNull(result.getAddDate());
      }
    }
  }

  @Nested
  @DisplayName("createMany")
  public class CreateManyTest {

    private final AlbumParseResult base = new AlbumParseResult(
      albumValues.getVideoId(),
      albumValues.getArtist(),
      albumValues.getTitle(),
      albumValues.getPublished(),
      albumValues.getCategory(),
      Instant.ofEpochSecond(1711378617).toString()
    );

    private final AlbumParseResult otherBase = new AlbumParseResult(
      newAlbumValues.getVideoId(),
      newAlbumValues.getArtist(),
      newAlbumValues.getTitle(),
      newAlbumValues.getPublished(),
      newAlbumValues.getCategory(),
      Instant.ofEpochSecond(1711378656).toString()
    );

    private List<AlbumParseResult> bases = List.of(base, otherBase);

    private final AlbumParseResult invalidBase = new AlbumParseResult(
      "12345678900",
      newAlbumValues.getArtist(),
      newAlbumValues.getTitle(),
      1,
      "null",
      "null"
    );

    @Test
    public void shouldCreateAllAlbumsIfAlbumsDoesNotExistByArtistAndTitleTest() {
      List<Album> albums = service.createMany(bases);

      assertEquals(2, albums.size());

      assertThat(albums)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
        .containsExactlyInAnyOrder(
          new Album(
            null, // ignored
            base.getVideoId(),
            base.getArtist(),
            base.getTitle(),
            base.getPublished(),
            base.getCategory(),
            base.getAddDate()
          ),
          new Album(
            null, // ignored
            otherBase.getVideoId(),
            otherBase.getArtist(),
            otherBase.getTitle(),
            otherBase.getPublished(),
            otherBase.getCategory(),
            otherBase.getAddDate()
          )
        );
    }

    @Test
    public void shouldCreateOnlyAlbumsThatDoesNotExistByArtistAndTitleTest() {
      service.create(new AlbumCreation(
        albumValues.getVideoId(),
        bases.get(1).getArtist(), // so the other base is not created
        bases.get(1).getTitle(),  // so the other base is not created
        albumValues.getPublished(),
        albumValues.getCategory()
      ));

      List<Album> albums = service.createMany(bases);

      assertEquals(1, albums.size());

      assertThat(albums)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
        .contains(
          new Album(
            null, // ignored
            base.getVideoId(),
            base.getArtist(),
            base.getTitle(),
            base.getPublished(),
            base.getCategory(),
            base.getAddDate()
          )
        );
    }

    @Test
    public void shouldNotCreateAnyAlbumsIfSingleAlbumIsInvalidTest() {
      TransactionSystemException ex = assertThrows(
        TransactionSystemException.class,
        () -> service.createMany(List.of(base, invalidBase))
      );

      assertTrue(service.findAll().isEmpty());

      // check that orginal exception was constraint violation resulting from invalid base
      assertTrue(ex.contains(RollbackException.class));
      RollbackException rb = (RollbackException) ex.getOriginalException();
      assertInstanceOf(ConstraintViolationException.class, rb.getCause());
    }
  }

  @Nested
  @DisplayName("update")
  public class UpdateTest {

    @Test
    public void shouldThrowWhenAlbumToBeUpdatedCanNotBeFoundTest() {
      Integer nonExistingId = 123;

      CustomDataNotFoundException e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.update(nonExistingId, albumValues)
      );

      assertEquals("Album was not found", e.getMessage());
    }

    // how to handle validation?
    @Test
    public void shouldThrowWhenIdParameterIsNullTest() {
      Integer nullId = null;

      assertThrows(
        ConstraintViolationException.class,
        () -> service.update(nullId, albumValues)
      );
    }

    @Test
    public void shouldThrowWhenNewValueIsNullTest() {
      Integer id = 123;

      IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> service.update(id, null)
      );

      assertEquals("Expected update value to be present", e.getMessage());
    }

    // how to handle validation?
    @Test
    public void shouldThrowWhenNewAlbumValuesAreInvalidTest() {
      Integer id = service.create(albumValues).getId();

      ConstraintViolationException ex = assertThrows(
        ConstraintViolationException.class,
        () -> service.update(id, new AlbumCreation())
      );

      assertTrue(ex.getConstraintViolations().size() > 0);

      //assertTrue(ex.getMessage().contains("Video id is required"));
      //assertTrue(ex.getMessage().contains("Artist name is required"));
      //assertTrue(ex.getMessage().contains("Artist name is required"));
      //assertTrue(ex.getMessage().contains("Title is required"));
      //assertTrue(ex.getMessage().contains("Publish year is required"));
      //assertTrue(ex.getMessage().contains("Category is required"));
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
      public void shouldReturnAlbumWithTheNewValuesTest() {
        assertEquals(newAlbumValues.getVideoId(), after.getVideoId());
        assertEquals(newAlbumValues.getArtist(), after.getArtist());
        assertEquals(newAlbumValues.getTitle(), after.getTitle());
        assertEquals(newAlbumValues.getPublished(), after.getPublished());
        assertEquals(newAlbumValues.getCategory(), after.getCategory());
      }

      @Test
      public void shouldHaveTheOldIdTest() {
        assertEquals(before.getId(), after.getId());
      }

      @Test
      public void shouldHaveTheOldAddDateTest() {
        assertEquals(before.getAddDate(), after.getAddDate());
      }
    }

    @Test
    public void shouldThrowWhenUpdatingAlbumThatHasBeenRemovedTest() {
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
    public void shouldBeAbleToTryToRemoveNonExistingAlbumTest() {
      Integer nonExistingId = 123;

      service.remove(nonExistingId);
    }

    @Test
    public void shouldBeAbleToRemoveExistingAlbumTest() {
      Album album = service.create(albumValues);

      service.remove(album.getId());
    }
  }
}
