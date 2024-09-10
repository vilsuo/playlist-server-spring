package com.fs.fsapi.bookmark;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import com.fs.fsapi.bookmark.parser.AlbumBase;
import com.fs.fsapi.bookmark.parser.BookmarksFileParserService;
import com.fs.fsapi.bookmark.parser.BookmarksLinkParserService;
import com.fs.fsapi.exceptions.CustomHtmlParsingException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;
import com.fs.fsapi.helpers.AlbumHelper;
import com.fs.fsapi.helpers.BookmarksFileHelper;
import com.fs.fsapi.helpers.BookmarksFileHelper.InvalidHeader;

import static com.fs.fsapi.helpers.BookmarksFileHelper.ValidHeader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {
  BookmarksService.class,
  BookmarksFileParserService.class,
  BookmarksLinkParserService.class
})
public class BookmarksServiceTest {

  private final String name = "file";

  @Autowired
  private BookmarksService service;

  @Test
  public void shouldThrowWhenFileElementStructureIsInvalidTest() throws Exception {
    InputStream contentStream = BookmarksFileHelper.readFileWithInvalidStruture();
    MockMultipartFile file = new MockMultipartFile(name, contentStream);

    final InvalidHeader header = InvalidHeader.STRUCTURE;

    CustomHtmlParsingException ex = assertThrows(
      CustomHtmlParsingException.class,
      () -> service.getAlbumBases(file, header.getTextContent())
    );

    assertEquals(
      "Expected the next sibling element of header with text content '"
        + header.getTextContent() + "' to be 'dl' element, instead found 'p' element",
      ex.getMessage()
    );
  }

  @Nested
  public class InvalidLinks {

    private MockMultipartFile file;

    @BeforeEach
    public void setUpMockFile() throws IOException {
      final InputStream contentStream = BookmarksFileHelper.readFileWithInvalidLink();
      file = new MockMultipartFile(name, contentStream);
    }

    @Test
    public void shouldThrowWhenRequiredAttributeIsMissingTest() {
      final InvalidHeader header = InvalidHeader.LINK_ATTRIBUTE_MISSING;

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> service.getAlbumBases(file, header.getTextContent())
      );

      assertEquals(
        "Link 'add_date' attribute is missing",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenRequiredAttributeHasInvalidValueTest() {
      final InvalidHeader header = InvalidHeader.LINK_ATTRIBUTE_INVALID;

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> service.getAlbumBases(file, header.getTextContent())
      );

      assertEquals(
        "Link href attribute 'https://www.youtub.com/watch?v=DopHEl-BCGQ'"
        + " must start with the prefix 'https://www.youtube.com/watch?'",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenTextContentHasInvalidValueTest() {
      final InvalidHeader header = InvalidHeader.LINK_TEXT_CONTENT_INVALID;

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> service.getAlbumBases(file, header.getTextContent())
      );

      assertEquals(
        "Link text content 'Angel Dust Into the Dark Past (1986)' "
        + "is in incorrect format",
        ex.getMessage()
      );
    }
  }

  @Nested
  public class ValidFile {

    private MockMultipartFile file;

    @BeforeEach
    public void setUpMockFile() throws IOException {
      final InputStream contentStream = BookmarksFileHelper.readValidFile();
      file = new MockMultipartFile(name, contentStream);
    }

    @Test
    public void shouldThrowWhenHeaderDoesNotExistWithTheGivenTextContentTest() throws Exception {
      final ValidHeader header = ValidHeader.NON_EXISTING;

      CustomParameterConstraintException ex = assertThrows(
        CustomParameterConstraintException.class, 
        () -> service.getAlbumBases(file, header.getTextContent())
      );

      assertEquals(
        "Could not find a header with the text content '" + header.getTextContent() + "'",
        ex.getMessage()
      );
    }

    @Test
    public void shouldNotCreateAnyWhenThereAreNoLinksTest() throws IOException {
      final ValidHeader header = ValidHeader.EMPTY;
      List<AlbumBase> results = service.getAlbumBases(file, header.getTextContent());

      assertTrue(results.isEmpty());
    }

    @Nested
    @DisplayName("folder without sub folders")
    public class WithoutSubFolders {

      private final ValidHeader header = ValidHeader.CHILD;

      private List<AlbumBase> results;
      private AlbumBase result;

      private final AlbumBase expected = AlbumHelper.VALID_FILE_CHILD_ALBUMBASES[0];

      @BeforeEach
      public void create() throws Exception {
        results = service.getAlbumBases(file, header.getTextContent());
        result = results.get(0);
      }

      @Test
      public void shouldCreateSingleTest() {
        assertEquals(1, results.size());
      }

      @Test
      public void shouldSetCategoryToHeaderTextContentTest() {
        assertEquals(expected.getCategory(), result.getCategory());
      }

      @Test
      public void shouldGetDetailsFromTextContentTest() {
        assertEquals(expected.getArtist(), result.getArtist());
        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(expected.getPublished(), result.getPublished());
      }

      @Test
      public void shouldGetDetailsFromAttributesTest() {
        assertEquals(expected.getVideoId(), result.getVideoId());
        assertEquals(expected.getAddDate(), result.getAddDate());
      }
    }

    @Nested
    @DisplayName("folder with a sub folder")
    public class WithSubFolders {

      private ValidHeader header = ValidHeader.PARENT;

      private List<AlbumBase> results;
      private AlbumBase[] expectations = AlbumHelper.VALID_FILE_PARENT_ALBUMBASES;

      @BeforeEach
      public void create() throws IOException  {
        results = service.getAlbumBases(file, header.getTextContent());
      }

      @Test
      public void shouldCreateFromAllLinksInTheFolderRecursivelyTest() {
        assertEquals(expectations.length, results.size());
      }

      @Test
      public void shouldCreateFromLinksBeforeSubFolderTest() {
        AlbumBase result = results.get(2);
        AlbumBase expected = expectations[2];

        assertEquals(expected.getVideoId(), result.getVideoId());
        assertEquals(expected.getArtist(), result.getArtist());
        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(expected.getPublished(), result.getPublished());
        assertEquals(expected.getCategory(), result.getCategory());
        assertEquals(expected.getAddDate(), result.getAddDate());
      }

      @Test
      public void shouldCreateFromLinksInSubFolderTest() {
        AlbumBase result = results.get(3);
        AlbumBase expected = expectations[3];

        assertEquals(expected.getVideoId(), result.getVideoId());
        assertEquals(expected.getArtist(), result.getArtist());
        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(expected.getPublished(), result.getPublished());
        assertEquals(expected.getCategory(), result.getCategory());
        assertEquals(expected.getAddDate(), result.getAddDate());
      }

      @Test
      public void shouldCreateFromLinksAfterSubFolderTest() {
        AlbumBase result = results.get(4);
        AlbumBase expected = expectations[4];

        assertEquals(expected.getVideoId(), result.getVideoId());
        assertEquals(expected.getArtist(), result.getArtist());
        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(expected.getPublished(), result.getPublished());
        assertEquals(expected.getCategory(), result.getCategory());
        assertEquals(expected.getAddDate(), result.getAddDate());
      }
    }
  }
}

