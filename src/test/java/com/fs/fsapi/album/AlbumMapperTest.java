package com.fs.fsapi.album;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.fs.fsapi.helpers.AlbumHelper.*;

@SpringBootTest(classes = AlbumMapperImpl.class)
public class AlbumMapperTest {

  @Autowired
  private AlbumMapper mapper;

  @Nested
  @DisplayName("albumCreationToAlbum")
  public class AlbumCreationToAlbumTest {

    private final AlbumCreation value = ALBUM_CREATION_VALUE_1();
    private final Album result = mapper.albumCreationToAlbum(value);

    @Test
    public void shouldReturnNullWhenNullTest() {
      assertNull(mapper.albumCreationToAlbum(null));
    }

    @Test
    public void shouldReturnNullPropertiesWhenNewPropertiesAreNullTest() {
      final AlbumCreation value = new AlbumCreation();
      final Album result = mapper.albumCreationToAlbum(value);

      assertNull(result.getVideoId());
      assertNull(result.getArtist());
      assertNull(result.getTitle());
      assertNull(result.getPublished());
      assertNull(result.getCategory());
    }

    @Test
    public void shouldReturnNewPropertiesTest() {
      assertEquals(value.getVideoId(), result.getVideoId());
      assertEquals(value.getArtist(), result.getArtist());
      assertEquals(value.getTitle(), result.getTitle());
      assertEquals(value.getPublished(), result.getPublished());
      assertEquals(value.getCategory(), result.getCategory());
    }

    @Test
    public void shouldReturnNullIdTest() {
      assertNull(result.getId());
    }

    @Test
    public void shouldReturnNullAddDateTest() {
      assertNull(result.getAddDate());
    }
  }

  @Nested
  @DisplayName("updateAlbumFromAlbumCreation")
  public class UpdateAlbumFromAlbumCreationTest {

    private final AlbumCreation source = ALBUM_CREATION_VALUE_2();

    private Album target;

    @BeforeEach
    public void setUp() {
      // need to recreate, gets assigned new property values
      target = MOCK_ALBUM_1();
    }

    @Test
    public void shouldNotUpdatePropertiesWhenSourceIsNullTest() {
      final AlbumCreation source = null;

      mapper.updateAlbumFromAlbumCreation(source, target);

      assertNotNull(target.getVideoId());
      assertNotNull(target.getArtist());
      assertNotNull(target.getTitle());
      assertNotNull(target.getPublished());
      assertNotNull(target.getCategory());
    }

    @Test
    public void shouldUpdatePropertiesWhenNewPropertiesAreNonNullTest() {
      mapper.updateAlbumFromAlbumCreation(source, target);

      assertEquals(source.getVideoId(), target.getVideoId());
      assertEquals(source.getArtist(), target.getArtist());
      assertEquals(source.getTitle(), target.getTitle());
      assertEquals(source.getPublished(), target.getPublished());
      assertEquals(source.getCategory(), target.getCategory());
    }

    @Test
    public void shouldUpdatePropertiesToNullWhenNewPropertiesAreNullTest() {
      final AlbumCreation source = new AlbumCreation();

      mapper.updateAlbumFromAlbumCreation(source, target);
      
      assertNull(target.getVideoId());
      assertNull(target.getArtist());
      assertNull(target.getTitle());
      assertNull(target.getPublished());
      assertNull(target.getCategory());
    }

    @Test
    public void shouldNotUpdateIdTest() {
      mapper.updateAlbumFromAlbumCreation(source, target);

      assertEquals(MOCK_ID_1, target.getId());
    }

    @Test
    public void shouldNotUpdateAddDateTest() {
      mapper.updateAlbumFromAlbumCreation(source, target);

      assertEquals(MOCK_ADD_DATE_1, target.getAddDate());
    }
  }
}
