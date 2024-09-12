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
import java.util.List;
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

import com.fs.fsapi.bookmark.parser.AlbumParseResult;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;
import static com.fs.fsapi.helpers.AlbumHelper.*;
import static com.fs.fsapi.helpers.ParsedAlbumHelper.*;

import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceUnitTest {
  
  @Mock
  private AlbumRepository repository;

  @Mock
  private AlbumMapper mapper;

  @InjectMocks
  private AlbumService service;

  @Nested
  @DisplayName("findOne")
  public class FindOneTest {

    @Test
    public void shouldReturnAlbumWhenAlbumIsFoundTest() {
      when(repository.findById(MOCK_ID_1))
        .thenReturn(Optional.of(MOCK_ALBUM_1));

      assertEquals(MOCK_ALBUM_1, service.findOne(MOCK_ID_1));
      verify(repository).findById(MOCK_ID_1);
    }

    @Test
    public void shouldThrowWhenAlbumIsNotFoundTest() {
      when(repository.findById(MOCK_ID_1))
        .thenReturn(Optional.empty());

      CustomDataNotFoundException ex = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.findOne(MOCK_ID_1)
      );

      verify(repository).findById(MOCK_ID_1);

      assertEquals("Album was not found", ex.getMessage());
    }
  }

  @Nested
  @DisplayName("create")
  public class CreateTest {
  
    @Test
    public void shouldThrowWhenValueIsNullTest() {
      IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> service.create(null)
      );

      assertEquals(
        "Expected creation value to be present",
        e.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenAlbumExistsWithArtistAndTitleTest() {
      final String artist = ALBUM_CREATION_VALUE_1.getArtist();
      final String title = ALBUM_CREATION_VALUE_1.getTitle();

      when(repository.existsByArtistAndTitle(artist, title))
        .thenReturn(true);

      CustomParameterConstraintException ex = assertThrows(
        CustomParameterConstraintException.class, 
        () -> service.create(ALBUM_CREATION_VALUE_1)
      );

      assertEquals(
        "Album with artist '" + artist + "' and title '" + title + "' already exists",
        ex.getMessage()
      );
    }

    @Nested
    @DisplayName("when album does not exist by artist and title")
    public class NonExisting {

      private MockedStatic<Instant> mockedStatic;

      @BeforeEach
      public void setUpMocks() {
        when(repository.existsByArtistAndTitle(
          ALBUM_CREATION_VALUE_1.getArtist(),
          ALBUM_CREATION_VALUE_1.getTitle())
        )
          .thenReturn(false);

        when(mapper.albumCreationToAlbum(ALBUM_CREATION_VALUE_1))
          .thenReturn(MOCK_MAPPED_ALBUM_CREATION_VALUE_1);

        // mock time
        mockedStatic = mockStatic(
          Instant.class,
          Mockito.CALLS_REAL_METHODS
        );

        final Instant time = Instant.parse(MOCK_ADD_DATE_1);
        mockedStatic.when(() -> Instant.now())
          .thenReturn(time);
      }

      @AfterEach
      public void tearDownInstantMock() {
        mockedStatic.close();
      }

      @Test
      public void shouldThrowWhenSaveThrowsTest() {
        when(repository.save(MOCK_MAPPED_ALBUM_CREATION_VALUE_WITH_ADD_DATE_1))
          .thenThrow(ConstraintViolationException.class);

        assertThrows(
          ConstraintViolationException.class,
          () -> service.create(ALBUM_CREATION_VALUE_1)
        );
      }

      @Nested
      @DisplayName("when saving does not throw")
      public class ValidValues {

        @BeforeEach
        public void setUpSavingMock() {
          when(repository.save(MOCK_MAPPED_ALBUM_CREATION_VALUE_WITH_ADD_DATE_1))
            .thenReturn(MOCK_ALBUM_1);
        }

        @Test
        public void shouldReturnSavedAlbumTest() {
          assertEquals(MOCK_ALBUM_1, service.create(ALBUM_CREATION_VALUE_1));
        }

        @Test
        public void shouldHaveNullIdWhenSavingTest() {
          service.create(ALBUM_CREATION_VALUE_1);
          verify(repository).save(argThat(album -> album.getId() == null));
        }

        @Test
        public void shouldSaveWithTheCreationValuesTest() {
          service.create(ALBUM_CREATION_VALUE_1);

          verify(repository).save(argThat(album -> 
              album.getVideoId().equals(ALBUM_CREATION_VALUE_1.getVideoId())
            && album.getArtist().equals(ALBUM_CREATION_VALUE_1.getArtist())
            && album.getTitle().equals(ALBUM_CREATION_VALUE_1.getTitle())
            && album.getPublished().equals(ALBUM_CREATION_VALUE_1.getPublished())
            && album.getCategory().equals(ALBUM_CREATION_VALUE_1.getCategory())
          ));
        }

        @Test
        public void shouldSaveWithCreatedAddDateTest() {
          service.create(ALBUM_CREATION_VALUE_1);

          verify(repository).save(argThat(album ->
            album.getAddDate().equals(MOCK_ADD_DATE_1)
          ));
        }
      }
    }
  }

  @Nested
  @DisplayName("createMany")
  public class CreateManyTest {

    private final List<AlbumParseResult> bases = List.of(
      MOCK_PARSE_RESULT_1,
      MOCK_PARSE_RESULT_2
    );

    @Test
    public void shouldCreateAllWhenTheAlbumsDoesNotAlreadyExistByArtistAndTitleTest() {
      // set up existence mocks
      when(repository.existsByArtistAndTitle(any(), any()))
        .thenReturn(false);
      
      // set up mapper mocks
      when(mapper.albumParseResultToAlbum(MOCK_PARSE_RESULT_1))
        .thenReturn(MOCK_MAPPED_PARSE_RESULT_1);

      when(mapper.albumParseResultToAlbum(MOCK_PARSE_RESULT_2))
        .thenReturn(MOCK_MAPPED_PARSE_RESULT_2);

      // set up saving mocks
      when(repository.save(MOCK_MAPPED_PARSE_RESULT_1))
        .thenReturn(MOCK_ALBUM_1);

      when(repository.save(MOCK_MAPPED_PARSE_RESULT_2))
        .thenReturn(MOCK_ALBUM_2);
    
      // call method
      List<Album> actuals = service.createMany(bases);

      verify(repository, times(2))
        .save(any(Album.class));

      verify(repository).save(MOCK_MAPPED_PARSE_RESULT_1);
      verify(repository).save(MOCK_MAPPED_PARSE_RESULT_2);

      assertEquals(bases.size(), actuals.size());
      assertEquals(MOCK_ALBUM_1, actuals.get(0));
      assertEquals(MOCK_ALBUM_2, actuals.get(1));
    }

    @Test
    public void shouldSkipAlbumsWhenTheyAlreadyExistWithArtistAndTitleTest() {
      // set up existence mocks
      when(repository.existsByArtistAndTitle(
        MOCK_PARSE_RESULT_1.getArtist(),
        MOCK_PARSE_RESULT_1.getTitle())
      )
        .thenReturn(false);

      when(repository.existsByArtistAndTitle(
        MOCK_PARSE_RESULT_2.getArtist(),
        MOCK_PARSE_RESULT_2.getTitle())
      )
        .thenReturn(true); // this one already exists
      
      // setup saving and mapping mock only for non-existing
      when(mapper.albumParseResultToAlbum(MOCK_PARSE_RESULT_1))
        .thenReturn(MOCK_MAPPED_PARSE_RESULT_1);

      when(repository.save(MOCK_MAPPED_PARSE_RESULT_1))
        .thenReturn(MOCK_ALBUM_1);

      // call method
      List<Album> actuals = service.createMany(bases);

      verify(repository).save(MOCK_MAPPED_PARSE_RESULT_1);
      verify(repository, times(0))
        .save(MOCK_MAPPED_PARSE_RESULT_2);

      assertEquals(1, actuals.size());
      assertEquals(MOCK_ALBUM_1, actuals.get(0));
    }
  }

  @Nested
  @DisplayName("update")
  public class UpdateTest {

    @Test
    public void shouldThrowWhenAlbumIsNotFound() {
      when(repository.findById(MOCK_ID_1))
        .thenReturn(Optional.empty());
  
        CustomDataNotFoundException e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.update(MOCK_ID_1, ALBUM_CREATION_VALUE_1)
      );
  
      // nothing was saved
      verify(repository, times(0)).save(any());

      assertEquals(e.getMessage(), "Album was not found");
    }

    @Test
    public void shouldThrowWhenNewValueIsNullTest() {
      IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> service.update(MOCK_ID_1, null)
      );

      // nothing was saved
      verify(repository, times(0)).save(any());

      assertEquals("Expected update value to be present", e.getMessage());
    }

    @Nested
    @DisplayName("when album to update is found")
    public class Saving {

      @BeforeEach
      public void setUpMockFinding() {
        when(repository.findById(MOCK_ID_1))
          .thenReturn(Optional.of(MOCK_ALBUM_1));
      }

      @Test
      public void shouldThrowWhenSaveThrowsTest() {
        when(repository.save(any()))
          .thenThrow(ConstraintViolationException.class);

        assertThrows(
          ConstraintViolationException.class,
          () -> service.update(MOCK_ID_1, ALBUM_CREATION_VALUE_2)
        );
      }

      @Nested
      @DisplayName("when new values are valid")
      public class Success {

        @BeforeEach
        public void setUpMockSave() {
          when(repository.save(any())) // the value changes
            .thenReturn(MOCK_UPDATED_ALBUM);
        }

        @Test
        public void shouldReturnUpdatedAlbumTest() {
          final Album actual = service.update(MOCK_ID_1, ALBUM_CREATION_VALUE_2);
          assertEquals(MOCK_UPDATED_ALBUM, actual);
        }

        @Test
        public void shouldCallMapperUpdateWithNewValuesAndOriginalAlbumBeforeSavingTest() {
          InOrder inOrder = inOrder(repository, mapper);

          service.update(MOCK_ID_1, ALBUM_CREATION_VALUE_2);

          // verify that mapper was called before saving
          inOrder.verify(mapper)
            .updateAlbumFromAlbumCreation(ALBUM_CREATION_VALUE_2, MOCK_ALBUM_1);
            
          inOrder.verify(repository).save(MOCK_ALBUM_1);

          inOrder.verifyNoMoreInteractions();
        }
      }
    }
  }
}
