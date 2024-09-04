package com.fs.fsapi.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fs.fsapi.bookmark.parser.FolderLink;
import com.fs.fsapi.bookmark.parser.HtmlParserService;
import com.fs.fsapi.exceptions.CustomHtmlParsingException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;

public class HtmlParserServiceTest {

  private final HtmlParserService service = new HtmlParserService();

  private final String TEST_FILES_LOCATION = "src/test/data";

  private final String VALID_FILE = "bookmarks.html";
  private final String INVALID_FILE = "bookmarks-invalid.html";

  private final String PARENT = "Example";
  private final String CHILD = "Sub";
  private final String EMPTY = "Empty";
  
  private InputStream getFileAsInputStream(String filename) throws FileNotFoundException {
    File initialFile = new File(TEST_FILES_LOCATION + "/" + filename);
    return new FileInputStream(initialFile);
  }

  @Test
  public void shouldThrowWhenHeaderDoesNotExistWithTheGivenTextContentTest() throws Exception {
    final String nonExisting = "Punk";

    CustomParameterConstraintException ex = assertThrows(
      CustomParameterConstraintException.class, 
      () -> service.createFolderLinks(getFileAsInputStream(VALID_FILE), nonExisting)
    );

    assertEquals(
      "Could not find a header with the text content '" + nonExisting + "'",
      ex.getMessage()
    );
  }

  @Test
  public void shouldNotCreateAnyWhenFolderIsEmptyTest() throws Exception {
    assertTrue(service.createFolderLinks(getFileAsInputStream(VALID_FILE), EMPTY).isEmpty());
  }

  @Test
  public void shouldThrowWhenElementStructureIsInvalidTest() {
    CustomHtmlParsingException ex = assertThrows(
      CustomHtmlParsingException.class,
      () -> service.createFolderLinks(getFileAsInputStream(INVALID_FILE), PARENT)
    );

    assertEquals(
      "Expected the next sibling element of header with text content '"
        + PARENT + "' to be 'dl' element, instead found 'p' element",
      ex.getMessage()
    );
  }

  @Nested
  @DisplayName("folder with a single album")
  public class Sub {

    private List<FolderLink> results;
    private FolderLink result;

    @BeforeEach
    public void create() throws Exception {
      results = service.createFolderLinks(getFileAsInputStream(VALID_FILE), CHILD);
      result = results.get(0);
    }

    @Test
    public void shouldCreateSingleTest() {
      assertEquals(1, results.size());
    }

    @Test
    public void shouldGetFolderNameFromHeaderTextContentTest() {
      assertEquals(CHILD, result.getFolderName());
    }

    @Test
    public void shouldGetTextFromLinkTextContentTest() {
      assertEquals(
        "Angel Dust - Into the Dark Past (1986)",
        result.getText()
      );
    }

    @Test
    public void shouldGetHrefFromLinkHrefAttributeTest() {
      assertEquals(
        "https://www.youtube.com/watch?v=DopHEl-BCGQ",
        result.getHref()
      );
    }
    
    @Test
    public void shouldGetAddDateFromLinkAddDateAttributeTest() {
      assertEquals("1711378617", result.getAddDate());
    }
  }

  @Nested
  @DisplayName("folder with a sub folder")
  public class WithSub {

    private List<FolderLink> results;

    @BeforeEach
    public void create() throws Exception {
      results = service.createFolderLinks(getFileAsInputStream(VALID_FILE), PARENT);
    }

    @Test
    public void shouldCreateFromAllLinksInTheFolderRecursivelyTest() {
      assertEquals(5, results.size());
    }

    @Test
    public void shouldSetParentHeaderTextContentAsFolderNameForLinksBeforeSubFolderTest() {
      assertEquals(PARENT, results.get(0).getFolderName());
      assertEquals(PARENT, results.get(1).getFolderName());
      assertEquals(PARENT, results.get(2).getFolderName());
    }

    @Test
    public void shouldSetChildHeaderTextContentAsFolderNameForLinksInSubFolderTest() {
      assertEquals(CHILD, results.get(3).getFolderName());
    }

    @Test
    public void shouldSetParentHeaderTextContentAsFolderNameForLinksAfterSubFolderTest() {
      assertEquals(PARENT, results.get(4).getFolderName());
    }
  }
}
