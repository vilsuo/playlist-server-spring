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

@SpringBootTest(classes = AlbumMapperImpl.class)
public class AlbumMapperTest {

  @Autowired
  private AlbumMapper mapper;

  private static final AlbumCreation source = new AlbumCreation(
    "JMAbKMSuVfI",
    "Massacra",
    "Signs of the Decline",
    1992,
    "Death"
  );

  @Nested
  @DisplayName("albumCreationToAlbum")
  public class AlbumCreationToAlbumTest {

    @Test
    public void mapsNullToNull() {
      AlbumCreation values = null;

      var target = mapper.albumCreationToAlbum(values);
      assertNull(target);
    }

    @Test
    public void mapsNullPropertiesToNullProperties() {
      AlbumCreation values = new AlbumCreation();

      Album result = mapper.albumCreationToAlbum(values);
      assertNull(result.getId());
      assertNull(result.getVideoId());
      assertNull(result.getArtist());
      assertNull(result.getTitle());
      assertNull(result.getPublished());
      assertNull(result.getCategory());
      assertNull(result.getAddDate());
    }

    @Test
    public void mapsPropertiesToProperties() {
      Album result = mapper.albumCreationToAlbum(source);
      assertEquals(source.getVideoId(), result.getVideoId());
      assertEquals(source.getArtist(), result.getArtist());
      assertEquals(source.getTitle(), result.getTitle());
      assertEquals(source.getPublished(), result.getPublished());
      assertEquals(source.getCategory(), result.getCategory());
    }
  }

  @Nested
  @DisplayName("updateAlbumFromAlbumCreation")
  public class UpdateAlbumFromAlbumCreationTest {

    private final Integer id = 1947;
    private final String addDate = "2024-08-14T10:33:57.604056616Z";

    private Album target;

    @BeforeEach
    public void setUpTarget() {
      this.target = new Album(
        id,
        "qJVktESKhKY",
        "Devastation",
        "Idolatry",
        1991,
        "Thrash",
        addDate
      );
    }

    @Test
    public void doesNotUpdatePropertiesWhenSourceIsNull() {
      AlbumCreation values = null;
      mapper.updateAlbumFromAlbumCreation(values, target);

      assertNotNull(target.getVideoId());
      assertNotNull(target.getArtist());
      assertNotNull(target.getTitle());
      assertNotNull(target.getPublished());
      assertNotNull(target.getCategory());
    }

    @Test
    public void updatesPropertiesWhenNewPropertiesAreNonNull() {
      mapper.updateAlbumFromAlbumCreation(source, target);

      assertEquals(source.getVideoId(), target.getVideoId());
      assertEquals(source.getArtist(), target.getArtist());
      assertEquals(source.getTitle(), target.getTitle());
      assertEquals(source.getPublished(), target.getPublished());
      assertEquals(source.getCategory(), target.getCategory());
    }

    @Test
    public void updatesGivenPropertiesToNullWhenNewPropertiesAreNull() {
      AlbumCreation values = new AlbumCreation();

      mapper.updateAlbumFromAlbumCreation(values, target);
      
      assertNull(target.getVideoId());
      assertNull(target.getArtist());
      assertNull(target.getTitle());
      assertNull(target.getPublished());
      assertNull(target.getCategory());
    }

    @Test
    public void doesNotUpdateId() {
      mapper.updateAlbumFromAlbumCreation(source, target);

      assertEquals(id, target.getId());
    }

    @Test
    public void doesNotUpdateAddDate() {
      mapper.updateAlbumFromAlbumCreation(source, target);

      assertEquals(addDate, target.getAddDate());
    }
  }
}
