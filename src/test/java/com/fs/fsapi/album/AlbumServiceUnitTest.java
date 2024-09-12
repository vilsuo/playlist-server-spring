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
import com.fs.fsapi.helpers.AlbumHelper;
import com.fs.fsapi.helpers.ParsedAlbumHelper;

import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceUnitTest {
  
  @Mock
  private AlbumRepository repository;

  @Mock
  private AlbumMapper mapper;

  @InjectMocks
  private AlbumService service;

  private final Integer id = AlbumHelper.mockId1;
  private final String addDate = AlbumHelper.mockAddDate1;

  private final AlbumCreation creation = AlbumHelper.creation1;
  private final AlbumCreation otherCreation = AlbumHelper.creation2;

  private final Album mappedCreation = AlbumHelper.mockMappedAlbum1;
  private final Album mappedCreationWithAddDate = AlbumHelper.mockMappedAlbumWithAddDate1;

  private final Album target = AlbumHelper.mockAlbum1;
  private final Album updatedTarget = AlbumHelper.mockUpdatedAlbum;

  @Nested
  @DisplayName("findOne")
  public class FindOneTest {

    @Test
    public void shouldReturnAlbumWhenAlbumIsFoundTest() {
      when(repository.findById(id))
        .thenReturn(Optional.of(target));

      final Album actual = service.findOne(id);
      assertEquals(target, actual);

      verify(repository).findById(id);
    }

    @Test
    public void shouldThrowWhenAlbumIsNotFoundTest() {
      when(repository.findById(id))
        .thenReturn(Optional.empty());

      CustomDataNotFoundException ex = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.findOne(id)
      );

      assertEquals("Album was not found", ex.getMessage());
      verify(repository).findById(id);
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
      final String artist = creation.getArtist();
      final String title = creation.getTitle();

      when(repository.existsByArtistAndTitle(artist, title))
        .thenReturn(true);

      CustomParameterConstraintException ex = assertThrows(
        CustomParameterConstraintException.class, 
        () -> service.create(creation)
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
        when(repository.existsByArtistAndTitle(creation.getArtist(), creation.getTitle()))
          .thenReturn(false);

        when(mapper.albumCreationToAlbum(creation))
          .thenReturn(mappedCreation);

        // mock time
        mockedStatic = mockStatic(
          Instant.class,
          Mockito.CALLS_REAL_METHODS
        );

        final Instant time = Instant.parse(addDate);
        mockedStatic.when(() -> Instant.now())
          .thenReturn(time);
      }

      @AfterEach
      public void tearDownInstantMock() {
        mockedStatic.close();
      }

      @Test
      public void shouldThrowWhenSaveThrowsTest() {
        when(repository.save(mappedCreationWithAddDate))
          .thenThrow(ConstraintViolationException.class);

        assertThrows(
          ConstraintViolationException.class,
          () -> service.create(creation)
        );
      }

      @Nested
      @DisplayName("when saving does not throw")
      public class ValidValues {

        @BeforeEach
        public void setUpSavingMock() {
          when(repository.save(mappedCreationWithAddDate))
            .thenReturn(target);
        }

        @Test
        public void shouldReturnSavedAlbumTest() {
          assertEquals(target, service.create(creation));
        }

        @Test
        public void shouldHaveNullIdWhenSavingTest() {
          service.create(creation);

          verify(repository).save(argThat(album -> album.getId() == null));
        }

        @Test
        public void shouldSaveWithTheCreationValuesTest() {
          service.create(creation);

          verify(repository).save(argThat(album -> 
              album.getVideoId().equals(creation.getVideoId())
            && album.getArtist().equals(creation.getArtist())
            && album.getTitle().equals(creation.getTitle())
            && album.getPublished().equals(creation.getPublished())
            && album.getCategory().equals(creation.getCategory())
          ));
        }

        @Test
        public void shouldSaveWithCreatedAddDateTest() {
          service.create(creation);

          verify(repository).save(argThat(album ->
            album.getAddDate().equals(addDate)
          ));
        }
      }
    }
  }

  @Nested
  @DisplayName("createMany")
  public class CreateManyTest {

    private final AlbumParseResult base = ParsedAlbumHelper.mockParseResult1;
    private final Album mappedBase = AlbumHelper.mockMappedAlbumWithAddDate1;
    private final Album albumFromMappedBase = AlbumHelper.mockAlbum1;

    private final AlbumParseResult otherBase = ParsedAlbumHelper.mockParseResult2;
    private final Album mappedOtherBase = AlbumHelper.mockMappedAlbumWithAddDate2;
    private final Album albumFromOtherMappedBase = AlbumHelper.mockAlbum2;

    private final List<AlbumParseResult> bases = List.of(base, otherBase);

    @Test
    public void shouldCreateAllWhenTheAlbumsDoesNotAlreadyExistByArtistAndTitleTest() {
      // set up mocks
      when(repository.existsByArtistAndTitle(any(), any()))
        .thenReturn(false);
      
      when(mapper.albumParseResultToAlbum(base)).thenReturn(mappedBase);
      when(mapper.albumParseResultToAlbum(otherBase)).thenReturn(mappedOtherBase);

      when(repository.save(mappedBase)).thenReturn(albumFromMappedBase);
      when(repository.save(mappedOtherBase)).thenReturn(albumFromOtherMappedBase);
    
      // call method
      List<Album> actuals = service.createMany(bases);

      assertEquals(bases.size(), actuals.size());
      assertEquals(albumFromMappedBase, actuals.get(0));
      assertEquals(albumFromOtherMappedBase, actuals.get(1));

      verify(repository, times(2))
        .save(any(Album.class));

      verify(repository).save(mappedBase);
      verify(repository).save(mappedOtherBase);
    }

    @Test
    public void shouldSkipAlbumsWhenTheyAlreadyExistWithArtistAndTitleTest() {
      // set up mocks...
      when(repository.existsByArtistAndTitle(base.getArtist(), base.getTitle()))
        .thenReturn(false);

      // ...second one already exists
      when(repository.existsByArtistAndTitle(otherBase.getArtist(), otherBase.getTitle()))
        .thenReturn(true);
      
      when(mapper.albumParseResultToAlbum(base)).thenReturn(mappedBase);
      when(repository.save(mappedBase)).thenReturn(albumFromMappedBase);

      // call method
      List<Album> actuals = service.createMany(bases);

      assertEquals(1, actuals.size());
      assertEquals(albumFromMappedBase, actuals.get(0));

      verify(repository).save(mappedBase);
      verify(repository, times(0)).save(mappedOtherBase);
    }
  }

  @Nested
  @DisplayName("update")
  public class UpdateTest {

    @Test
    public void shouldThrowWhenAlbumIsNotFound() {
      when(repository.findById(id))
        .thenReturn(Optional.empty());
  
        CustomDataNotFoundException e = assertThrows(
        CustomDataNotFoundException.class,
        () -> service.update(id, creation)
      );
  
      assertEquals(e.getMessage(), "Album was not found");

      verify(repository, times(0)).save(any());
    }

    @Test
    public void shouldThrowWhenNewValueIsNullTest() {
      IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> service.update(id, null)
      );

      assertEquals("Expected update value to be present", e.getMessage());

      verify(repository, times(0)).save(any());
    }

    @Nested
    @DisplayName("when album to update is found")
    public class Saving {

      @BeforeEach
      public void setUpMockFinding() {
        when(repository.findById(id))
          .thenReturn(Optional.of(target));
      }

      @Test
      public void shouldThrowWhenSaveThrowsTest() {
        when(repository.save(any()))
          .thenThrow(ConstraintViolationException.class);

        assertThrows(
          ConstraintViolationException.class,
          () -> service.update(id, otherCreation)
        );
      }

      @Nested
      @DisplayName("when new values are valid")
      public class Success {

        @BeforeEach
        public void setUpMockSave() {
          when(repository.save(any()))
            .thenReturn(updatedTarget);
        }

        @Test
        public void shouldReturnUpdatedAlbumTest() {
          Album actual = service.update(id, otherCreation);
          assertEquals(updatedTarget, actual);
        }

        @Test
        public void shouldCallMapperUpdateWithNewValuesAndOriginalAlbumBeforeSavingTest() {
          InOrder inOrder = inOrder(repository, mapper);

          service.update(id, otherCreation);

          // verify that mapper was called before saving
          inOrder.verify(mapper).updateAlbumFromAlbumCreation(otherCreation, target);
          inOrder.verify(repository).save(target);

          inOrder.verifyNoMoreInteractions();
        }
      }
    }
  }
}
