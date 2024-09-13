package com.fs.fsapi.album;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

import static com.fs.fsapi.helpers.AlbumHelper.*;

@ExtendWith(MockitoExtension.class)
public class AlbumControllerUnitTest {

  @Mock
  private AlbumService service;

  @Mock
  private MockHttpServletRequest request;

  @InjectMocks
  private AlbumController controller;

  private final Integer id = MOCK_ID_1;

  private final AlbumCreation source = ALBUM_CREATION_VALUE_1();

  private final AlbumCreation newValues = ALBUM_CREATION_VALUE_1();

  private final Album target = MOCK_ALBUM_1();

  private final Album targetWithNewValues = MOCK_UPDATED_ALBUM();

  @Nested
  @DisplayName("getAll")
  public class GetAll {

    @Test
    public void shouldReturnEmptyArrayWhenThereAreNoAlbumsTest() {
      when(service.findAll()).thenReturn(List.of());

      ResponseEntity<List<Album>> ent = controller.getAll();
      final List<Album> albums = ent.getBody();

      assertTrue(albums != null && albums.isEmpty());

      assertEquals(HttpStatus.OK.value(), ent.getStatusCode().value());
    }

    @Test
    public void shouldReturnAlbumsArrayWhenThereAreAlbumsTest()  {
      when(service.findAll()).thenReturn(List.of(target));

      ResponseEntity<List<Album>> ent = controller.getAll();
      List<Album> albums = ent.getBody();

      assertNotNull(albums);
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

        final Album album = ent.getBody();
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

        final URI location = ent.getHeaders().getLocation();
        assertNotNull(location);
        assertEquals(finalUrl, location.toString());
      }
    }

    @Test
    public void shouldThrowWhenServiceThrowsTest() {
      final String message = "Something went wrong";
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
        final Album album = ent.getBody();

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
      final String message = "Not found";

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
        final Album album = ent.getBody();

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
      final String message = "Something went wrong";

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
