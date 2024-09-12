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
      final Album expected = MOCK_ALBUM_1();

      when(repository.findById(MOCK_ID_1))
        .thenReturn(Optional.of(expected));

      assertEquals(expected, service.findOne(MOCK_ID_1));
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
      final AlbumCreation value = ALBUM_CREATION_VALUE_1();

      final String artist = value.getArtist();
      final String title = value.getTitle();

      when(repository.existsByArtistAndTitle(artist, title))
        .thenReturn(true);

      CustomParameterConstraintException ex = assertThrows(
        CustomParameterConstraintException.class, 
        () -> service.create(value)
      );

      assertEquals(
        "Album with artist '" + artist + "' and title '"
        + title + "' already exists",
        ex.getMessage()
      );
    }

    @Nested
    @DisplayName("when album does not exist by artist and title")
    public class NonExisting {

      private AlbumCreation creationValue;

      private MockedStatic<Instant> mockedStatic;

      @BeforeEach
      public void setUpMocks() {
        creationValue = ALBUM_CREATION_VALUE_1();

        when(repository.existsByArtistAndTitle(
          creationValue.getArtist(), creationValue.getTitle())
        )
          .thenReturn(false);

        when(mapper.albumCreationToAlbum(creationValue))
          .thenReturn(MOCK_MAPPED_ALBUM_CREATION_VALUE_1());

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
        when(repository.save(MOCK_MAPPED_ALBUM_CREATION_VALUE_WITH_ADD_DATE_1()))
          .thenThrow(ConstraintViolationException.class);

        assertThrows(
          ConstraintViolationException.class,
          () -> service.create(creationValue)
        );
      }

      @Nested
      @DisplayName("when saving does not throw")
      public class ValidValues {

        private Album result = MOCK_ALBUM_1();

        @BeforeEach
        public void setUpSavingMock() {
          when(repository.save(MOCK_MAPPED_ALBUM_CREATION_VALUE_WITH_ADD_DATE_1()))
            .thenReturn(result);
        }

        @Test
        public void shouldReturnSavedAlbumTest() {
          assertEquals(result, service.create(creationValue));
        }

        @Test
        public void shouldHaveNullIdWhenSavingTest() {
          service.create(creationValue);
          
          verify(repository).save(argThat(savedAlbum -> savedAlbum.getId() == null));
        }

        @Test
        public void shouldSaveWithTheCreationValuesTest() {
          service.create(creationValue);

          verify(repository).save(argThat(savedAlbum -> 
               savedAlbum.getVideoId().equals(creationValue.getVideoId())
            && savedAlbum.getArtist().equals(creationValue.getArtist())
            && savedAlbum.getTitle().equals(creationValue.getTitle())
            && savedAlbum.getPublished().equals(creationValue.getPublished())
            && savedAlbum.getCategory().equals(creationValue.getCategory())
          ));
        }

        @Test
        public void shouldSaveWithCreatedAddDateTest() {
          service.create(creationValue);

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

    private final List<AlbumParseResult> values = List.of(
      MOCK_PARSE_RESULT_1(),
      MOCK_PARSE_RESULT_2()
    );

    private final AlbumParseResult firstValue = values.get(0);
    private final AlbumParseResult secondValue = values.get(1);

    @Test
    public void shouldCreateAllWhenTheAlbumsDoesNotAlreadyExistByArtistAndTitleTest() {
      // set up existence mocks
      when(repository.existsByArtistAndTitle(any(), any()))
        .thenReturn(false);
      
      // set up mapper mocks
      when(mapper.albumParseResultToAlbum(firstValue))
        .thenReturn(MOCK_MAPPED_PARSE_RESULT_1());

      when(mapper.albumParseResultToAlbum(secondValue))
        .thenReturn(MOCK_MAPPED_PARSE_RESULT_2());

      // set up saving mocks
      when(repository.save(MOCK_MAPPED_PARSE_RESULT_1()))
        .thenReturn(MOCK_ALBUM_1());

      when(repository.save(MOCK_MAPPED_PARSE_RESULT_2()))
        .thenReturn(MOCK_ALBUM_2());
    
      // call method
      List<Album> actuals = service.createMany(values);

      verify(repository, times(2))
        .save(any(Album.class));

      verify(repository).save(MOCK_MAPPED_PARSE_RESULT_1());
      verify(repository).save(MOCK_MAPPED_PARSE_RESULT_2());

      assertEquals(values.size(), actuals.size());
      assertEquals(MOCK_ALBUM_1(), actuals.get(0));
      assertEquals(MOCK_ALBUM_2(), actuals.get(1));
    }

    @Test
    public void shouldSkipAlbumsWhenTheyAlreadyExistWithArtistAndTitleTest() {
      // set up existence mocks
      when(repository.existsByArtistAndTitle(
        firstValue.getArtist(),
        firstValue.getTitle())
      )
        .thenReturn(false);

      when(repository.existsByArtistAndTitle(
        secondValue.getArtist(),
        secondValue.getTitle()
      ))
        .thenReturn(true); // this one already exists
      
      // setup saving and mapping mock only for non-existing
      when(mapper.albumParseResultToAlbum(values.get(0)))
        .thenReturn(MOCK_MAPPED_PARSE_RESULT_1());

      when(repository.save(MOCK_MAPPED_PARSE_RESULT_1()))
        .thenReturn(MOCK_ALBUM_1());

      // call method
      List<Album> actuals = service.createMany(values);

      verify(repository).save(MOCK_MAPPED_PARSE_RESULT_1());
      verify(repository, times(0))
        .save(MOCK_MAPPED_PARSE_RESULT_2());

      assertEquals(1, actuals.size());
      assertEquals(MOCK_ALBUM_1(), actuals.get(0));
    }
  }

  @Nested
  @DisplayName("update")
  public class UpdateTest {

    private final Integer id = MOCK_ID_1;

    private final AlbumCreation newValues = ALBUM_CREATION_VALUE_2();

    @Test
    public void shouldThrowWhenAlbumIsNotFound() {
      when(repository.findById(id))
        .thenReturn(Optional.empty());
  
        CustomDataNotFoundException e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.update(id, newValues)
      );
  
      // nothing was saved
      verify(repository, times(0)).save(any());

      assertEquals(e.getMessage(), "Album was not found");
    }

    @Test
    public void shouldThrowWhenNewValueIsNullTest() {
      IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> service.update(id, null)
      );

      // nothing was saved
      verify(repository, times(0)).save(any());

      assertEquals("Expected update value to be present", e.getMessage());
    }

    @Nested
    @DisplayName("when album to update is found")
    public class Saving {

      private final Album albumToUpdate = MOCK_ALBUM_1();

      @BeforeEach
      public void setUpMockFinding() {
        when(repository.findById(id))
          .thenReturn(Optional.of(albumToUpdate));
      }

      @Test
      public void shouldThrowWhenSaveThrowsTest() {
        // save is called with the found one since mapper is mocked
        when(repository.save(any()))
          .thenThrow(ConstraintViolationException.class);

        assertThrows(
          ConstraintViolationException.class,
          () -> service.update(id, newValues)
        );
      }

      @Nested
      @DisplayName("when new values are valid")
      public class Success {

        @BeforeEach
        public void setUpMockSave() {
          // save is called with the found one since mapper is mocked
          when(repository.save(any()))
            .thenReturn(MOCK_UPDATED_ALBUM());
        }

        @Test
        public void shouldCallMapperUpdateWithNewValuesAndOriginalAlbumBeforeSavingTest() {
          InOrder inOrder = inOrder(repository, mapper);

          service.update(id, newValues);

          // verify that mapper was called before saving
          inOrder.verify(mapper).updateAlbumFromAlbumCreation(
            newValues, albumToUpdate
          );
            
          inOrder.verify(repository).save(albumToUpdate);

          inOrder.verifyNoMoreInteractions();
        }
      }
    }
  }
}
