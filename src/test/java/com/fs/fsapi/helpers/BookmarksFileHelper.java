package com.fs.fsapi.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.fs.fsapi.bookmark.parser.BookmarksLinkElement;

public class BookmarksFileHelper {

  private static final String TEST_FILES_LOCATION = "src/test/data/bookmarks";

  // files
  private static final String VALID_FILE = "bookmarks.html";
  private static final String INVALID_FILE_STRUCTURE = "bookmarks-invalid-structure.html";
  private static final String INVALID_FILE_LINK = "bookmarks-invalid-links.html";

  /**
   * Read bookmarks html file with expected structure.
   * 
   * @return
   * @throws FileNotFoundException
   */
  public static InputStream readValidFile() throws FileNotFoundException {
    return readFile(VALID_FILE);
  }

  /**
   * Read bookmarks html file with unexpected structure.
   * 
   * @return
   * @throws FileNotFoundException
   */
  public static InputStream readFileWithInvalidStruture() throws FileNotFoundException {
    return readFile(INVALID_FILE_STRUCTURE);
  }

  /**
   * Read bookmarks html file with expected structure, but with unexpected 'a' elements
   * 
   * @return
   * @throws FileNotFoundException
   */
  public static InputStream readFileWithInvalidLinks() throws FileNotFoundException {
    return readFile(INVALID_FILE_LINK);
  }

  private static InputStream readFile(String filename) throws FileNotFoundException {
    File initialFile = new File(TEST_FILES_LOCATION + "/" + filename);
    return new FileInputStream(initialFile);
  }

  /**
   * Header element text contents in file {@link BookmarksFileHelper#VALID_FILE}.
   */
  public enum ValidHeader {
    ROOT ("Bookmarks"),
    CONTAINER ("Bookmarks bar"),
    PARENT ("Example"),
    CHILD ("Sub"),
    EMPTY ("Empty"),
    NON_EXISTING ("Punk");

    private final String textContent;

    public String getTextContent() {
      return textContent;
    }

    ValidHeader(String textContent) {
      this.textContent = textContent;
    }
  }

  /**
   * Header element text contents in file {@link BookmarksFileHelper#INVALID_FILE_STRUCTURE}
   * and {@link BookmarksFileHelper#INVALID_FILE_LINK}.
   */
  public enum InvalidHeader {
    STRUCTURE ("Example"),

    /**
     * 'a' missing "add_date" attribute
     */
    LINK_ATTRIBUTE_MISSING ("Missing"),

    /**
     * 'a' has invalid "href" attribute value prefix
     */
    LINK_ATTRIBUTE_INVALID ("Invalid"),

    /**
     * 'a' text content has invalid structure (missing separator)
     */
    LINK_TEXT_CONTENT_INVALID ("Content");

    private final String textContent;

    public String getTextContent() {
      return textContent;
    }

    InvalidHeader(String textContent) {
      this.textContent = textContent;
    }
  }

  // DETAILS ABOUT LINKS FROM A VALID FILE
  
  private static final BookmarksLinkElement f1 = new BookmarksLinkElement(
    ElementHelper.createBookmarkLinkTypeElement(
      "Annihilator - Alice In Hell (1989)",
      "https://www.youtube.com/watch?v=IdRn9IYWuaQ",
      "1653126836"
    ),
    ValidHeader.CONTAINER.getTextContent()
  );

  private static final BookmarksLinkElement f2 = new BookmarksLinkElement(
    ElementHelper.createBookmarkLinkTypeElement(
      "A.O.D - Altars of Destruction (1988)",
      "https://www.youtube.com/watch?v=5av2oGfw34g&list=PLHz-VIInDvH9Wfr0oLNeOch_SIl6rA1ij&index=8",
      "1711378433"
    ),
    ValidHeader.PARENT.getTextContent()
  );

  private static final BookmarksLinkElement f3 = new BookmarksLinkElement(
    ElementHelper.createBookmarkLinkTypeElement(
      "Nuclear Assault - Survive (1988)",
      "https://www.youtube.com/watch?v=zopfZLQibWw",
      "1711378636"
    ),
    ValidHeader.PARENT.getTextContent()
  );

  private static final BookmarksLinkElement f4 = new BookmarksLinkElement(
    ElementHelper.createBookmarkLinkTypeElement(
      "Exodus - Fabulous Disaster (1989)",
      "https://www.youtube.com/watch?v=Zof79HxNpMs",
      "1711378682"
    ),
    ValidHeader.PARENT.getTextContent()
  );

  private static final BookmarksLinkElement f5 = new BookmarksLinkElement(
    ElementHelper.createBookmarkLinkTypeElement(
      "Angel Dust - Into the Dark Past (1986)",
      "https://www.youtube.com/watch?v=DopHEl-BCGQ",
      "1711378617"
    ),
    ValidHeader.CHILD.getTextContent()
  );

  private static final BookmarksLinkElement f6 = new BookmarksLinkElement(
    ElementHelper.createBookmarkLinkTypeElement(
      "Paradox - Product of Imagination (1987)",
      "https://www.youtube.com/watch?v=MV3yQFU3Z6s",
      "1711378656"
    ),
    ValidHeader.PARENT.getTextContent()
  );

  /**
   * Expected details about link elements in file {@link BookmarksFileHelper#VALID_FILE}
   * at the location specified by {@link BookmarksFileHelper.ValidHeader#ROOT}.
   */
  public static final BookmarksLinkElement[] VALID_FILE_ROOT_LINKS = { f1, f2, f3, f4, f5, f6 };

  /**
   * Expected details about link elements in file {@link BookmarksFileHelper#VALID_FILE}
   * at the location specified by {@link BookmarksFileHelper.ValidHeader#CONTAINER}.
   */
  public static final BookmarksLinkElement[] VALID_FILE_CONTAINER_LINKS = { f1, f2, f3, f4, f5, f6 };

  /**
   * Expected details about link elements in file {@link BookmarksFileHelper#VALID_FILE}
   * at the location specified by {@link BookmarksFileHelper.ValidHeader#PARENT}.
   */
  public static final BookmarksLinkElement[] VALID_FILE_PARENT_LINKS = { f2, f3, f4, f5, f6 };

  /**
   * Expected details about link elements in file {@link BookmarksFileHelper#VALID_FILE}
   * at the location specified by {@link BookmarksFileHelper.ValidHeader#CHILD}.
   */
  public static final BookmarksLinkElement[] VALID_FILE_CHILD_LINKS = { f5 };

  /**
   * Expected details about link elements in file {@link BookmarksFileHelper#VALID_FILE}
   * at the location specified by {@link BookmarksFileHelper.ValidHeader#EMPTY}.
   */
  public static final BookmarksLinkElement[] VALID_FILE_EMPTY_LINKS = { };

}
