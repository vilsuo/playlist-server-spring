package com.fs.fsapi.album;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
public class AlbumControllerUnitTest {

  @Mock
  private AlbumService service;

  @Mock
  private MockHttpServletRequest request;

  @InjectMocks
  private AlbumController controller;

  protected final Integer id = 123;

  protected final String addDate = "2024-08-14T10:33:57.604056616Z";

  protected static final AlbumCreation source = new AlbumCreation(
    "JMAbKMSuVfI",
    "Massacra",
    "Signs of the Decline",
    1992,
    "Death"
  );

  protected final AlbumCreation newValues = new AlbumCreation(
    "qJVktESKhKY",
    "Devastation",
    "Idolatry",
    1991,
    "Thrash"
  );

  protected final Album mappedSource = new Album(
    null,
    source.getVideoId(),
    source.getArtist(),
    source.getTitle(),
    source.getPublished(),
    source.getCategory(),
    null
  );

  protected final Album mappedSourceWithAddDate = new Album(
    mappedSource.getId(),
    mappedSource.getVideoId(),
    mappedSource.getArtist(),
    mappedSource.getTitle(),
    mappedSource.getPublished(),
    mappedSource.getCategory(),
    addDate
  );

  protected final Album target = new Album(
    id,
    mappedSourceWithAddDate.getVideoId(),
    mappedSourceWithAddDate.getArtist(),
    mappedSourceWithAddDate.getTitle(),
    mappedSourceWithAddDate.getPublished(),
    mappedSourceWithAddDate.getCategory(),
    mappedSourceWithAddDate.getAddDate()
  );

  protected final Album targetWithNewValues = new Album(
    target.getId(),
    newValues.getVideoId(),
    newValues.getArtist(),
    newValues.getTitle(),
    newValues.getPublished(),
    newValues.getCategory(),
    target.getAddDate()
  );

  @Nested
  @DisplayName("getAll")
  public class GetAll {

    @Test
    public void shouldReturnEmptyArrayWhenThereAreNoAlbumsTest() {
      when(service.findAll()).thenReturn(List.of());

      ResponseEntity<List<Album>> ent = controller.getAll();
      List<Album> albums = ent.getBody();

      assertTrue(albums.isEmpty());
      assertEquals(HttpStatus.OK.value(), ent.getStatusCode().value());
    }

    @Test
    public void shouldReturnAlbumsArrayWhenThereAreAlbumsTest()  {
      when(service.findAll()).thenReturn(List.of(target));

      ResponseEntity<List<Album>> ent = controller.getAll();
      List<Album> albums = ent.getBody();

      assertEquals(1, albums.size());
      assertEquals(target, albums.get(0));
      assertEquals(HttpStatus.OK.value(), ent.getStatusCode().value());
    }
  }

  @Nested
  @DisplayName("postAlbum")
  public class Post {

    MockHttpServletRequest request;

    @BeforeEach
    public void setUpContextMock() {
      request = new MockHttpServletRequest(HttpMethod.POST.name(), "/albums");
      RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
    
    @Nested
    public class Success {

      @BeforeEach
      public void setUpMock() {
        when(service.create(source)).thenReturn(target);
      }

      @Test
      public void shouldReturnCreatedAlbumTest() {
        ResponseEntity<Album> ent = controller.postAlbum(source);

        Album album = ent.getBody();
        assertEquals(target, album);
      }

      @Test
      public void shouldCreateWithPassedBodyTest() {
        controller.postAlbum(source);

        verify(service).create(source);
      }

      @Test
      public void shouldReturnCreatedStatusTest() {
        ResponseEntity<Album> ent = controller.postAlbum(source);

        assertEquals(HttpStatus.CREATED.value(), ent.getStatusCode().value());
      }

      @Test
      public void shouldAttachLocationHeaderTest() {
        ResponseEntity<Album> ent = controller.postAlbum(source);

        StringBuffer requestUrlBuffer = request.getRequestURL();
        String finalUrl = requestUrlBuffer
          .append("/")
          .append(target.getId())
          .toString();

        URI location = ent.getHeaders().getLocation();

        assertEquals(finalUrl, location.toString());
      }
    }

    @Test
    public void shouldThrowWhenServiceThrowsTest() {
      String message = "Something went wrong";
      when(service.create(source))
        .thenThrow(new RuntimeException(message));

      RuntimeException e = assertThrows(
        RuntimeException.class, 
        () -> controller.postAlbum(source)
      );

      assertEquals(message, e.getMessage());
    }
  }

  @Nested
  @DisplayName("getAlbum")
  public class GetOne {

    @Nested
    public class Found {

      @BeforeEach
      public void setUpMock() {
        when(service.findOne(id)).thenReturn(target);
      }

      @Test
      public void shouldReturnAlbumWhenFoundTest() {
        ResponseEntity<Album> ent = controller.getAlbum(id);
        Album album = ent.getBody();

        assertEquals(target, album);
      }

      @Test
      public void shouldReturnOkWhenFoundTest() {
        ResponseEntity<Album> ent = controller.getAlbum(id);
        assertEquals(HttpStatus.OK.value(), ent.getStatusCode().value());
      }

      @Test
      public void shouldSearchForCorrectAlbumTest() {
        controller.getAlbum(id);

        verify(service).findOne(id);
      }
    }

    @Test
    public void shouldThrowWhenNotFoundTest() {
      String message = "Not found";

      when(service.findOne(id))
        .thenThrow(new RuntimeException(message));

      RuntimeException e = assertThrows(
        RuntimeException.class,
        () -> controller.getAlbum(id)
      );

      assertEquals(message, e.getMessage());
    }
  }

  @Nested
  @DisplayName("putAlbum")
  public class Update {

    @Nested
    public class Success {

      @BeforeEach
      public void setUpMock() {
        when(service.update(id, newValues)).thenReturn(targetWithNewValues);
      }

      @Test
      public void shouldReturnUpdatedAlbumTest() {
        ResponseEntity<Album> ent = controller.putAlbum(id, newValues);
        Album album = ent.getBody();

        assertEquals(targetWithNewValues, album);
      }

      @Test
      public void shouldReturnStatusOkTest() {
        ResponseEntity<Album> ent = controller.putAlbum(id, newValues);

        assertEquals(HttpStatus.OK.value(), ent.getStatusCode().value());
      }

      @Test
      public void shouldUpdateCorrectAlbumTest() {
        controller.putAlbum(id, newValues);

        verify(service).update(eq(id), any());
      }

      @Test
      public void shouldUpdateWithCorrectValuesTest() {
        controller.putAlbum(id, newValues);

        verify(service).update(any(), eq(newValues));
      }
    }

    @Test
    public void shouldThrowWhenServiceThrowsTest() {
      String message = "Something went wrong";

      when(service.update(id, newValues))
        .thenThrow(new RuntimeException(message));

      RuntimeException e = assertThrows(
        RuntimeException.class,
        () -> controller.putAlbum(id, newValues)
      );
  
      assertEquals(message, e.getMessage());
    }
  }

  @Nested
  @DisplayName("deleteAlbum")
  public class Delete {
    
    @Test
    public void shouldReturnNoContentAlbumTest() {
      ResponseEntity<Void> ent = controller.deleteAlbum(id);

      assertEquals(HttpStatus.NO_CONTENT.value(), ent.getStatusCode().value());
    }

    @Test
    public void shouldDeleteCorrectAlbumTest() {
      controller.deleteAlbum(id);

      verify(service).remove(id);
    }
  }

  @Nested
  @DisplayName("downloadAlbums")
  public class Download {

    @Test
    public void shouldSetContentDispositionHeaderTest() {
      when(service.findAll()).thenReturn(List.of());

      ResponseEntity<List<Album>> ent = controller.downloadAlbums();
      ContentDisposition disp = ent.getHeaders().getContentDisposition();

      assertTrue(disp.isAttachment());
    }
  }
}
