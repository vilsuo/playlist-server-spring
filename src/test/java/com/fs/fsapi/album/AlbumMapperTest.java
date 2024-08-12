package com.fs.fsapi.album;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@RequiredArgsConstructor(onConstructor=@__({@Autowired}))
@SpringBootTest(classes = AlbumMapperImpl.class)
public class AlbumMapperTest {

  @Autowired
  private AlbumMapper mapper;

  private final AlbumCreation source = new AlbumCreation(
    "JMAbKMSuVfI",
    "Massacra",
    "Signs of the Decline",
    1992,
    "Death"
  );

  @Nested
  @DisplayName("albumCreationToAlbum")
  public class AlbumCreationToAlbum {

    @Test
    public void mapsNullToNull() {
      AlbumCreation source = null;

      var target = mapper.albumCreationToAlbum(source);
      assertNull(target);
    }

    @Test
    public void mapsNullPropertiesToNullProperties() {
      AlbumCreation source = new AlbumCreation();

      var target = mapper.albumCreationToAlbum(source);
      assertNull(target.getId());
      assertNull(target.getVideoId());
      assertNull(target.getArtist());
      assertNull(target.getTitle());
      assertNull(target.getPublished());
      assertNull(target.getCategory());
      assertNull(target.getAddDate());
    }

    @Test
    public void mapsPropertiesToProperties() {
      var target = mapper.albumCreationToAlbum(source);
      assertEquals(source.getVideoId(), target.getVideoId());
      assertEquals(source.getArtist(), target.getArtist());
      assertEquals(source.getTitle(), target.getTitle());
      assertEquals(source.getPublished(), target.getPublished());
      assertEquals(source.getCategory(), target.getCategory());
    }
  }

  @Nested
  @DisplayName("updateAlbumFromAlbumCreation")
  public class UpdateAlbumFromAlbumCreation {

    private final Integer id = 1947;
    private final String addDate = "2022-05-14T11:40:01.000Z";

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
    public void setsNonNullValues() {
      mapper.updateAlbumFromAlbumCreation(source, target);

      assertEquals(id, target.getId());

      assertEquals(source.getVideoId(), target.getVideoId());
      assertEquals(source.getArtist(), target.getArtist());
      assertEquals(source.getTitle(), target.getTitle());
      assertEquals(source.getPublished(), target.getPublished());
      assertEquals(source.getCategory(), target.getCategory());

      assertEquals(addDate, target.getAddDate());
    }

    @Test
    public void doesNotSetValuesToNull() {
      AlbumCreation source = new AlbumCreation();

      mapper.updateAlbumFromAlbumCreation(source, target);
      
      assertEquals(id, target.getId());

      assertNull(target.getVideoId());
      assertNull(target.getArtist());
      assertNull(target.getTitle());
      assertNull(target.getPublished());
      assertNull(target.getCategory());

      assertEquals(addDate, target.getAddDate());
    }
  }
}
