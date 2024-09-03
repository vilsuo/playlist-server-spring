package com.fs.fsapi.parser;

import static org.junit.Assert.assertEquals;

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

// TODO test files with
// - invalid format
// - empty folder
public class HtmlParserServiceTest {

  private final HtmlParserService service = new HtmlParserService();

  private final String TEST_FILES_LOCATION = "src/test/data";

  private final String EXAMPLE = "bookmarks-example.html";
  
  private InputStream getFileAsInputStream(String filename) throws FileNotFoundException {
    File initialFile = new File(TEST_FILES_LOCATION + "/" + filename);
    return new FileInputStream(initialFile);
  }

  @Nested
  @DisplayName("folder with single album")
  public class Sub {

    private List<FolderLink> results;
    private FolderLink result;

    @BeforeEach
    public void create() throws Exception {
      results = service.createFolderLinks(getFileAsInputStream(EXAMPLE), "Sub");
      result = results.get(0);
    }

    @Test
    public void shouldCreateSingleTest() {
      assertEquals(1, results.size());
    }

    @Test
    public void shouldGetFolderNameFromHeaderTextContentTest() {
      assertEquals("Sub", result.getFolderName());
    }

    @Test
    public void shouldGetTextFromLinkTextContentTest() {
      assertEquals("Angel Dust - Into the Dark Past (1986)", result.getText());
    }

    @Test
    public void shouldGetHrefFromLinkHrefAttributeTest() {
      assertEquals("https://www.youtube.com/watch?v=DopHEl-BCGQ", result.getHref());
    }
    
    @Test
    public void shouldGetAddDateFromLinkAddDateAttributeTest() {
      assertEquals("1711378617", result.getAddDate());
    }
  }

  @Nested
  @DisplayName("folder with a sub album")
  public class WithSub {

    private List<FolderLink> results;

    @BeforeEach
    public void create() throws Exception {
      results = service.createFolderLinks(getFileAsInputStream(EXAMPLE), "Example");
    }

    @Test
    public void shouldCreateFromAllLinksInTheFolderRecursivelyTest() {
      assertEquals(5, results.size());
    }

    // link folderName before "Sub" are "Example"
    // link folderName after "Sub" are "Example"
    // link folderName in "Sub" are "Sub"
  }
}
