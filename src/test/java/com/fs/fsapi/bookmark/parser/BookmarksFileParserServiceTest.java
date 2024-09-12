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
import com.fs.fsapi.helpers.BookmarksFileHelper;

import static com.fs.fsapi.helpers.BookmarksFileHelper.InvalidHeader;
import static com.fs.fsapi.helpers.BookmarksFileHelper.ValidHeader;;

public class BookmarksFileParserServiceTest {

  private final BookmarksFileParserService service = new BookmarksFileParserService();

  private List<BookmarksLinkElement> createWithValidFile(ValidHeader header) throws Exception {
    return service.parse(
      BookmarksFileHelper.readValidFile(), 
      header.getTextContent()
    );
  }

  private List<BookmarksLinkElement> createWithInValidFile(InvalidHeader header) throws Exception {
    return service.parse(
      BookmarksFileHelper.readFileWithInvalidStruture(), 
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
      "Could not find a header element with the text content '" + header.getTextContent() + "'",
      ex.getMessage()
    );
  }

  @Test
  public void shouldNotCreateAnyWhenFolderIsEmptyTest() throws Exception {
    final ValidHeader header = ValidHeader.EMPTY;
    assertTrue(createWithValidFile(header).isEmpty());
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
      + header.getTextContent() + "' to be a 'dl' element, instead found a 'p' element",
      ex.getMessage()
    );
  }

  @Nested
  @DisplayName("folder without sub folders")
  public class WithoutSubFolders {

    private ValidHeader header = ValidHeader.CHILD;

    private List<BookmarksLinkElement> actuals;
    private BookmarksLinkElement actual;

    private final BookmarksLinkElement expected = BookmarksFileHelper.VALID_FILE_CHILD_LINKS[0];

    @BeforeEach
    public void create() throws Exception {
      actuals = createWithValidFile(header);
      actual = actuals.get(0);
    }

    @Test
    public void shouldCreateSingleTest() {
      assertEquals(1, actuals.size());
    }

    @Test
    public void shouldGetFolderNameFromHeaderTextContentTest() {
      assertEquals(expected.getHeaderText(), actual.getHeaderText());
    }

    @Test
    public void shouldGetTextFromLinkTextContentTest() {
      assertEquals(expected.getText(), actual.getText());
    }

    @Test
    public void shouldGetHrefFromLinkHrefAttributeTest() {
      assertEquals(expected.getHref(), actual.getHref());
    }
    
    @Test
    public void shouldGetAddDateFromLinkAddDateAttributeTest() {
      assertEquals(expected.getAddDate(), actual.getAddDate());
    }
  }

  @Nested
  @DisplayName("folder with a sub folder")
  public class WithSubFolders {

    private ValidHeader header = ValidHeader.PARENT;

    private List<BookmarksLinkElement> actuals;
    private BookmarksLinkElement[] expectations = BookmarksFileHelper.VALID_FILE_PARENT_LINKS;

    @BeforeEach
    public void create() throws Exception {
      actuals = createWithValidFile(header);
    }

    @Test
    public void shouldCreateFromAllLinksInTheFolderRecursivelyTest() {
      assertEquals(expectations.length, actuals.size());
    }

    @Test
    public void shouldCreateFromLinksBeforeSubFolderTest() {
      BookmarksLinkElement actual = actuals.get(2);
      BookmarksLinkElement expected = expectations[2];

      assertEquals(expected.getHeaderText(), actual.getHeaderText());
      assertEquals(expected.getText(), actual.getText());
      assertEquals(expected.getHref(), actual.getHref());
      assertEquals(expected.getAddDate(), actual.getAddDate());
    }

    @Test
    public void shouldCreateFromLinksInSubFolderTest() {
      BookmarksLinkElement actual = actuals.get(3);
      BookmarksLinkElement expected = expectations[3];

      assertEquals(expected.getHeaderText(), actual.getHeaderText());
      assertEquals(expected.getText(), actual.getText());
      assertEquals(expected.getHref(), actual.getHref());
      assertEquals(expected.getAddDate(), actual.getAddDate());
    }

    @Test
    public void shouldCreateFromLinksAfterSubFolderTest() {
      BookmarksLinkElement actual = actuals.get(4);
      BookmarksLinkElement expected = expectations[4];

      assertEquals(expected.getHeaderText(), actual.getHeaderText());
      assertEquals(expected.getText(), actual.getText());
      assertEquals(expected.getHref(), actual.getHref());
      assertEquals(expected.getAddDate(), actual.getAddDate());
    }
  }
}
