package com.fs.fsapi.album;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import static com.fs.fsapi.helpers.AlbumHelper.*;
import static com.fs.fsapi.helpers.ParsedAlbumHelper.*;

// TODO test error message better?

@Testcontainers
@SpringBootTest
public class AlbumServiceIntegrationTest {

  @Autowired
  private AlbumRepository repository;

  @Autowired
  private AlbumService service;

  private final AlbumCreation creationValues = ALBUM_CREATION_VALUE_1();

  private final AlbumCreation newAlbumValues = ALBUM_CREATION_VALUE_2();

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
      final Album expected = service.create(creationValues);
      final Integer id = expected.getId();
      
      final Album actual = service.findOne(id);
      assertEquals(expected, actual);
      assertEquals(id, actual.getId());
    }

    @Test
    public void shouldFindUpdatedAlbumTest() {
      final Album before = service.create(creationValues);
      final Integer id = before.getId();
      
      final Album after = service.update(id, newAlbumValues);
      final Album found = service.findOne(id);

      assertNotEquals(before, found);
      assertEquals(after, found);
    }

    @Test
    public void shouldNotFindRemovedAlbumTest() {
      final Album target = service.create(creationValues);
      final Integer id = target.getId();
      
      service.remove(id);

      assertThrows(
        CustomDataNotFoundException.class, 
        () -> service.findOne(id)
      );
    }

    @Test
    public void shouldThrowWhenAlbumIsNotFoundTest() {
      final Integer id = 1239;

      CustomDataNotFoundException e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.findOne(id)
      );

      assertEquals(e.getMessage(), "Album was not found");
    }

    @Test
    public void shouldThrowWhenParameterIsNullTest() {
      final Integer nullId = null;

      ConstraintViolationException ex = assertThrows(
        ConstraintViolationException.class,
        () -> service.findOne(nullId)
      );

      Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
      assertEquals(1, violations.size());

      ConstraintViolation<?> violation = new ArrayList<>(violations).get(0);
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
        () -> service.create(INVALID_ALBUM_CREATION_VALUE())
      );

      assertFalse(ex.getConstraintViolations().isEmpty());
    }

    @Nested
    public class Success {

      @Test
      public void shouldReturnAlbumWithCreatedValuesTest() {
        final Album actual = service.create(creationValues);

        assertEquals(creationValues.getVideoId(), actual.getVideoId());
        assertEquals(creationValues.getArtist(), actual.getArtist());
        assertEquals(creationValues.getTitle(), actual.getTitle());
        assertEquals(creationValues.getPublished(), actual.getPublished());
        assertEquals(creationValues.getCategory(), actual.getCategory());
      }

      @Test
      public void shouldReturnAlbumWithIdTest() {
        final Album actual = service.create(creationValues);

        assertNotNull(actual.getId());
      }

      @Test
      public void shouldReturnAlbumWithAddDateTest() {
        final Album actual = service.create(creationValues);

        assertNotNull(actual.getAddDate());
      }
    }
  }

  @Nested
  @DisplayName("createMany")
  public class CreateManyTest {

    private final AlbumParseResult manyCreation = MOCK_PARSE_RESULT_1();

    private final AlbumParseResult otherManyCreation = MOCK_PARSE_RESULT_2();

    private final List<AlbumParseResult> manyValues = List.of(manyCreation, otherManyCreation);

    @Test
    public void shouldCreateAllAlbumsIfAlbumsDoesNotExistByArtistAndTitleTest() {
      final List<Album> actuals = service.createMany(manyValues);

      assertEquals(2, actuals.size());
    }

    @Test
    public void shouldCreateOnlyAlbumsThatDoesNotExistByArtistAndTitleTest() {
      service.create(new AlbumCreation(
        manyCreation.getVideoId(),
        otherManyCreation.getArtist(), // so the other base is not created
        otherManyCreation.getTitle(),  // so the other base is not created
        manyCreation.getPublished(),
        manyCreation.getCategory()
      ));

      final List<Album> actuals = service.createMany(manyValues);

      assertEquals(1, actuals.size());
      final Album actual = actuals.get(0);

      assertEquals(actual.getVideoId(), manyCreation.getVideoId());
      assertEquals(actual.getArtist(), manyCreation.getArtist());
      assertEquals(actual.getTitle(), manyCreation.getTitle());
      assertEquals(actual.getPublished(), manyCreation.getPublished());
      assertEquals(actual.getCategory(), manyCreation.getCategory());
      assertEquals(actual.getAddDate(), manyCreation.getAddDate());
    }

    @Test
    public void shouldNotCreateAnyAlbumsIfSingleAlbumIsInvalidTest() {
      TransactionSystemException ex = assertThrows(
        TransactionSystemException.class,
        () -> service.createMany(List.of(
          manyCreation,
          MOCK_INVALID_PARSE_RESULT() // has to have different artist/title name(s)
        ))
      );

      assertTrue(service.findAll().isEmpty());

      // check that orginal exception was constraint violation resulting
      // from invalid base
      assertTrue(ex.contains(RollbackException.class));
      RollbackException rb = (RollbackException) ex.getOriginalException();
      assertNotNull(rb);
      assertInstanceOf(ConstraintViolationException.class, rb.getCause());
    }
  }

  @Nested
  @DisplayName("update")
  public class UpdateTest {

    @Test
    public void shouldThrowWhenAlbumToBeUpdatedCanNotBeFoundTest() {
      final Integer nonExistingId = 123;

      CustomDataNotFoundException e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.update(nonExistingId, creationValues)
      );

      assertEquals("Album was not found", e.getMessage());
    }

    // how to handle validation?
    @Test
    public void shouldThrowWhenIdParameterIsNullTest() {
      final Integer nullId = null;

      ConstraintViolationException ex = assertThrows(
        ConstraintViolationException.class,
        () -> service.update(nullId, creationValues)
      );

      assertFalse(ex.getConstraintViolations().isEmpty());
    }

    @Test
    public void shouldThrowWhenNewValueIsNullTest() {
      final Integer id = 123;

      IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> service.update(id, null)
      );

      assertEquals(
        "Expected update value to be present",
        e.getMessage()
      );
    }

    // how to handle validation?
    @Test
    public void shouldThrowWhenNewAlbumValuesAreInvalidTest() {
      final Integer id = service.create(creationValues).getId();

      ConstraintViolationException ex = assertThrows(
        ConstraintViolationException.class,
        () -> service.update(id, INVALID_ALBUM_CREATION_VALUE())
      );

      assertFalse(ex.getConstraintViolations().isEmpty());
    }

    @Nested
    public class Success {

      private Album before;
      private Album after;

      @BeforeEach
      public void createAndUpdateAlbum() {
        before = service.create(creationValues);
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
      final Integer id = service.create(creationValues).getId();
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
      final Integer nonExistingId = 123;

      service.remove(nonExistingId);
    }

    @Test
    public void shouldBeAbleToRemoveExistingAlbumTest() {
      final Album album = service.create(creationValues);

      service.remove(album.getId());
    }
  }
}
