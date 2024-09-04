package com.fs.fsapi.album;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;

import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceUnitTest {
  
  @Mock
  private AlbumRepository repository;

  @Mock
  private AlbumMapper mapper;

  @InjectMocks
  private AlbumService service;

  private final Integer id = 123;

  private final String addDate = "2024-08-14T10:33:57.604056616Z";

  private static final AlbumCreation source = new AlbumCreation(
    "JMAbKMSuVfI",
    "Massacra",
    "Signs of the Decline",
    1992,
    "Death"
  );

  private final AlbumCreation newValues = new AlbumCreation(
    "qJVktESKhKY",
    "Devastation",
    "Idolatry",
    1991,
    "Thrash"
  );

  private final Album mappedSource = new Album(
    null,
    source.getVideoId(),
    source.getArtist(),
    source.getTitle(),
    source.getPublished(),
    source.getCategory(),
    null
  );

  private final Album mappedSourceWithAddDate = new Album(
    mappedSource.getId(),
    mappedSource.getVideoId(),
    mappedSource.getArtist(),
    mappedSource.getTitle(),
    mappedSource.getPublished(),
    mappedSource.getCategory(),
    addDate
  );

  private final Album target = new Album(
    id,
    mappedSourceWithAddDate.getVideoId(),
    mappedSourceWithAddDate.getArtist(),
    mappedSourceWithAddDate.getTitle(),
    mappedSourceWithAddDate.getPublished(),
    mappedSourceWithAddDate.getCategory(),
    mappedSourceWithAddDate.getAddDate()
  );

  private final Album targetWithNewValues = new Album(
    target.getId(),
    newValues.getVideoId(),
    newValues.getArtist(),
    newValues.getTitle(),
    newValues.getPublished(),
    newValues.getCategory(),
    target.getAddDate()
  );

  @Nested
  @DisplayName("findOne")
  public class FindOneTest {

    @Test
    public void returnsExistingAlbumTest() {
      when(repository.findById(id))
        .thenReturn(Optional.of(target));

      var result = service.findOne(id);
      assertEquals(target, result);

      verify(repository).findById(id);
    }

    @Test
    public void throwsIfNotFoundTest() {
      when(repository.findById(id))
        .thenReturn(Optional.empty());

      Exception e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.findOne(id)
      );

      assertEquals("Album was not found", e.getMessage());

      verify(repository).findById(id);
    }

    @Test
    public void throwsWithNullIdTest() {
      Integer nullId = null;

      when(repository.findById(nullId))
        .thenThrow(IllegalArgumentException.class);

      assertThrows(
        IllegalArgumentException.class,
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

      assertEquals(
        "Expected creation value to be present",
        e.getMessage()
      );
    }

    @Test
    public void shouldThrowIfAlbumExistsWithArtistAndTitleTest() {
      String artist = source.getArtist();
      String title = source.getTitle();

      when(repository.existsByArtistAndTitle(artist, title))
        .thenReturn(true);

      Exception ex = assertThrows(
        CustomParameterConstraintException.class, 
        () -> service.create(source)
      );

      assertEquals(
        "Album with artist '" + artist + "' and title '" + title + "' already exists",
        ex.getMessage()
      );
    }

    @Nested
    public class SavingNonExisting {

      private MockedStatic<Instant> mockedStatic;

      @BeforeEach
      public void setUpMocks() {
        when(repository.existsByArtistAndTitle(source.getArtist(), source.getTitle()))
          .thenReturn(false);

        when(mapper.albumCreationToAlbum(source))
          .thenReturn(mappedSource);

        // mock time
        mockedStatic = mockStatic(
          Instant.class,
          Mockito.CALLS_REAL_METHODS
        );

        var time = Instant.parse(addDate);
        mockedStatic.when(() -> Instant.now())
          .thenReturn(time);
      }

      @AfterEach
      public void tearDownInstantMock() {
        mockedStatic.close();
      }

      @Test
      public void throwsWhenValidationErrorsTest() {
        when(repository.save(mappedSourceWithAddDate))
          .thenThrow(ConstraintViolationException.class);

        assertThrows(
          ConstraintViolationException.class,
          () -> service.create(source)
        );
      }

      @Nested
      public class ValidValues {

        @BeforeEach
        public void setUpSaving() {
          when(repository.save(mappedSourceWithAddDate))
            .thenReturn(target);
        }

        @Test
        public void returnsSavedAlbumTest() {
          var result = service.create(source);

          assertEquals(target, result);
        }

        @Test
        public void callsSavesWithNullIdTest() {
          service.create(source);

          verify(repository).save(argThat(album -> album.getId() == null));
        }

        @Test
        public void callsSavesWithCreationValuesTest() {
          service.create(source);

          verify(repository).save(argThat(album -> 
              album.getVideoId().equals(source.getVideoId())
            && album.getArtist().equals(source.getArtist())
            && album.getTitle().equals(source.getTitle())
            && album.getPublished().equals(source.getPublished())
            && album.getCategory().equals(source.getCategory())
          ));
        }

        @Test
        public void callsSavesWithCreatedAddDateTest() {
          service.create(source);

          verify(repository).save(argThat(album ->
            album.getAddDate().equals(addDate)
          ));
        }
      }
    }
  }

  @Nested
  @DisplayName("update")
  public class UpdateTest {
    
    @Test
    public void throwsWithNullIdTest() {
      Integer nullId = null;
  
      when(repository.findById(nullId))
        .thenThrow(IllegalArgumentException.class);
  
      assertThrows(
        IllegalArgumentException.class,
        () -> service.update(nullId, source)
      );

      verify(repository, times(0)).save(any());
    }

    @Test
    public void throwsIfNotFoundTest() {
      when(repository.findById(id))
        .thenReturn(Optional.empty());
  
      Exception e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.update(id, source)
      );
  
      assertEquals(e.getMessage(), "Album was not found");

      verify(repository, times(0)).save(any());
    }

    @Test
    public void throwsWithNullNewValuesTest() {
      Exception e = assertThrows(
        IllegalArgumentException.class,
        () -> service.update(id, null)
      );

      assertEquals("Expected update value to be present", e.getMessage());

      verify(repository, times(0)).save(any());
    }

    @Nested
    public class Saving {

      @BeforeEach
      public void setUpMocks() {
        when(repository.findById(id))
          .thenReturn(Optional.of(target));
      }

      @Test
      public void throwsWhenSavingInvalidValuesTest() {
        when(repository.save(any()))
          .thenThrow(ConstraintViolationException.class);

        assertThrows(
          ConstraintViolationException.class,
          () -> service.update(id, newValues)
        );
      }

      @Nested
      public class Success {

        @BeforeEach
        public void setUpSave() {
          when(repository.save(any()))
            .thenReturn(targetWithNewValues);
        }

        @Test
        public void returnsSavedAlbumTest() {
          Album result = service.update(id, newValues);
          assertEquals(targetWithNewValues, result);
        }

        @Test
        public void callsMapperUpdateWithNewValuesAndOriginalAlbumBeforeSavingTest() {
          InOrder inOrder = inOrder(repository, mapper);

          service.update(id, newValues);

          // verify that mapper was called before saving
          inOrder.verify(mapper).updateAlbumFromAlbumCreation(newValues, target);
          inOrder.verify(repository).save(target);

          inOrder.verifyNoMoreInteractions();
        }
      }
    }
  }
}
