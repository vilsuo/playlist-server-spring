package com.fs.fsapi.bookmark.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fs.fsapi.exceptions.CustomHtmlParsingException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;
import com.fs.fsapi.helpers.FileHelper;

import static com.fs.fsapi.helpers.FileHelper.InvalidHeader;
import static com.fs.fsapi.helpers.FileHelper.ValidHeader;;

public class BookmarksFileParserServiceTest {

  private final BookmarksFileParserService service = new BookmarksFileParserService();

  private List<BookmarksLinkElement> createWithValidFile(ValidHeader header) throws Exception {
    return service.parseFile(
      FileHelper.getValidFile(), 
      header.getTextContent()
    );
  }

  private List<BookmarksLinkElement> createWithInValidFile(InvalidHeader header) throws Exception {
    return service.parseFile(
      FileHelper.getInvalidFileStruture(), 
      header.getTextContent()
    );
  }

  @Test
  public void shouldThrowWhenHeaderDoesNotExistWithTheGivenTextContentTest() throws Exception {
    final ValidHeader header = ValidHeader.NON_EXISTING;

    CustomParameterConstraintException ex = assertThrows(
      CustomParameterConstraintException.class, 
      () -> createWithValidFile(header)
    );

    assertEquals(
      "Could not find a header with the text content '" + header.getTextContent() + "'",
      ex.getMessage()
    );
  }

  @Test
  public void shouldNotCreateAnyWhenFolderIsEmptyTest() throws Exception {
    final ValidHeader header = ValidHeader.EMPTY;

    List<BookmarksLinkElement> folderLinks = createWithValidFile(header);
    assertTrue(folderLinks.isEmpty());
  }

  @Test
  public void shouldThrowWhenElementStructureIsInvalidTest() {
    final InvalidHeader header = InvalidHeader.STRUCTURE;

    CustomHtmlParsingException ex = assertThrows(
      CustomHtmlParsingException.class,
      () -> createWithInValidFile(header)
    );

    assertEquals(
      "Expected the next sibling element of header with text content '"
        + header.getTextContent() + "' to be 'dl' element, instead found 'p' element",
      ex.getMessage()
    );
  }

  @Nested
  @DisplayName("folder without sub folders")
  public class WithoutSubFolders {

    private ValidHeader header = ValidHeader.CHILD;

    private List<BookmarksLinkElement> results;
    private BookmarksLinkElement result;

    private final BookmarksLinkElement expected = FileHelper.VALID_FILE_CHILD_LINKS[0];

    @BeforeEach
    public void create() throws Exception {
      results = createWithValidFile(header);
      result = results.get(0);
    }

    @Test
    public void shouldCreateSingleTest() {
      assertEquals(1, results.size());
    }

    @Test
    public void shouldGetFolderNameFromHeaderTextContentTest() {
      assertEquals(expected.getHeaderText(), result.getHeaderText());
    }

    @Test
    public void shouldGetTextFromLinkTextContentTest() {
      assertEquals(expected.getText(), result.getText());
    }

    @Test
    public void shouldGetHrefFromLinkHrefAttributeTest() {
      assertEquals(expected.getHref(), result.getHref());
    }
    
    @Test
    public void shouldGetAddDateFromLinkAddDateAttributeTest() {
      assertEquals(expected.getAddDate(), result.getAddDate());
    }
  }

  @Nested
  @DisplayName("folder with a sub folder")
  public class WithSubFolders {

    private ValidHeader header = ValidHeader.PARENT;

    private List<BookmarksLinkElement> results;
    private BookmarksLinkElement[] expectations = FileHelper.VALID_FILE_PARENT_LINKS;

    @BeforeEach
    public void create() throws Exception {
      results = createWithValidFile(header);
    }

    @Test
    public void shouldCreateFromAllLinksInTheFolderRecursivelyTest() {
      assertEquals(expectations.length, results.size());
    }

    @Test
    public void shouldCreateFromLinksBeforeSubFolderTest() {
      BookmarksLinkElement result = results.get(2);
      BookmarksLinkElement expected = expectations[2];

      assertEquals(expected.getHeaderText(), result.getHeaderText());
      assertEquals(expected.getText(), result.getText());
      assertEquals(expected.getHref(), result.getHref());
      assertEquals(expected.getAddDate(), result.getAddDate());
    }

    @Test
    public void shouldCreateFromLinksInSubFolderTest() {
      BookmarksLinkElement result = results.get(3);
      BookmarksLinkElement expected = expectations[3];

      assertEquals(expected.getHeaderText(), result.getHeaderText());
      assertEquals(expected.getText(), result.getText());
      assertEquals(expected.getHref(), result.getHref());
      assertEquals(expected.getAddDate(), result.getAddDate());
    }

    @Test
    public void shouldCreateFromLinksAfterSubFolderTest() {
      BookmarksLinkElement result = results.get(4);
      BookmarksLinkElement expected = expectations[4];

      assertEquals(expected.getHeaderText(), result.getHeaderText());
      assertEquals(expected.getText(), result.getText());
      assertEquals(expected.getHref(), result.getHref());
      assertEquals(expected.getAddDate(), result.getAddDate());
    }
  }
}
