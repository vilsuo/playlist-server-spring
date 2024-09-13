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

import com.fs.fsapi.bookmark.parser.AlbumParseResult;
import com.fs.fsapi.bookmark.parser.BookmarksFileParserService;
import com.fs.fsapi.bookmark.parser.BookmarksLinkParserService;
import com.fs.fsapi.exceptions.CustomHtmlParsingException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;

import static com.fs.fsapi.helpers.BookmarksFileHelper.*;
import static com.fs.fsapi.helpers.ParsedAlbumHelper.*;
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
    InputStream contentStream = readFileWithInvalidStruture();
    MockMultipartFile file = new MockMultipartFile(name, contentStream);

    final InvalidHeader header = InvalidHeader.STRUCTURE;

    CustomHtmlParsingException ex = assertThrows(
      CustomHtmlParsingException.class,
      () -> service.getAlbumBases(file, header.getTextContent())
    );

    assertEquals(
      "Expected the next sibling element of header with text content '" 
      + header.getTextContent() + "' to be a 'dl' element, instead found a 'p' element",
      ex.getMessage()
    );
  }

  @Nested
  public class InvalidLinks {

    private MockMultipartFile file;

    @BeforeEach
    public void setUpMockFile() throws IOException {
      final InputStream contentStream = readFileWithInvalidLinks();
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
        "Expected element to have 'add_date' attribute",
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
        "Expected the 'href' attribute value 'https://www.youtub.com/watch?v=DopHEl-BCGQ'"
        + " to start with 'https://www.youtube.com/watch?'",
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
        "The text content 'Angel Dust Into the Dark Past (1986)' is invalid",
        ex.getMessage()
      );
    }
  }

  @Nested
  public class ValidFile {

    private MockMultipartFile file;

    @BeforeEach
    public void setUpMockFile() throws IOException {
      final InputStream contentStream = readValidFile();
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
        "Could not find a header element with the text content '"
        + header.getTextContent() + "'",
        ex.getMessage()
      );
    }

    @Test
    public void shouldNotCreateAnyWhenThereAreNoLinksTest() throws IOException {
      final ValidHeader header = ValidHeader.EMPTY;
      List<AlbumParseResult> actual = service.getAlbumBases(file, header.getTextContent());

      assertTrue(actual.isEmpty());
    }

    @Nested
    @DisplayName("folder without sub folders")
    public class WithoutSubFolders {

      private final ValidHeader header = ValidHeader.CHILD;
      private final AlbumParseResult expected = VALID_FILE_CHILD_RESULTS[0];

      private List<AlbumParseResult> actuals;
      private AlbumParseResult actual;

      @BeforeEach
      public void create() throws Exception {
        actuals = service.getAlbumBases(file, header.getTextContent());
        actual = actuals.get(0);
      }

      @Test
      public void shouldCreateSingleTest() {
        assertEquals(1, actuals.size());
      }

      @Test
      public void shouldSetCategoryToHeaderTextContentTest() {
        assertEquals(expected.getCategory(), actual.getCategory());
      }

      @Test
      public void shouldGetDetailsFromTextContentTest() {
        assertEquals(expected.getArtist(), actual.getArtist());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getPublished(), actual.getPublished());
      }

      @Test
      public void shouldGetDetailsFromAttributesTest() {
        assertEquals(expected.getVideoId(), actual.getVideoId());
        assertEquals(expected.getAddDate(), actual.getAddDate());
      }
    }

    @Nested
    @DisplayName("folder with a sub folder")
    public class WithSubFolders {

      private ValidHeader header = ValidHeader.PARENT;
      private AlbumParseResult[] expectations = VALID_FILE_PARENT_RESULTS;
      
      private List<AlbumParseResult> actuals;

      @BeforeEach
      public void create() throws IOException  {
        actuals = service.getAlbumBases(file, header.getTextContent());
      }

      @Test
      public void shouldCreateFromAllLinksInTheFolderRecursivelyTest() {
        assertEquals(expectations.length, actuals.size());
      }

      @Test
      public void shouldCreateFromLinksBeforeSubFolderTest() {
        final AlbumParseResult actual = actuals.get(2);
        final AlbumParseResult expected = expectations[2];

        assertEquals(expected.getVideoId(), actual.getVideoId());
        assertEquals(expected.getArtist(), actual.getArtist());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getPublished(), actual.getPublished());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.getAddDate(), actual.getAddDate());
      }

      @Test
      public void shouldCreateFromLinksInSubFolderTest() {
        final AlbumParseResult actual = actuals.get(3);
        final AlbumParseResult expected = expectations[3];

        assertEquals(expected.getVideoId(), actual.getVideoId());
        assertEquals(expected.getArtist(), actual.getArtist());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getPublished(), actual.getPublished());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.getAddDate(), actual.getAddDate());
      }

      @Test
      public void shouldCreateFromLinksAfterSubFolderTest() {
        final AlbumParseResult actual = actuals.get(4);
        final AlbumParseResult expected = expectations[4];

        assertEquals(expected.getVideoId(), actual.getVideoId());
        assertEquals(expected.getArtist(), actual.getArtist());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getPublished(), actual.getPublished());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.getAddDate(), actual.getAddDate());
      }
    }
  }
}

