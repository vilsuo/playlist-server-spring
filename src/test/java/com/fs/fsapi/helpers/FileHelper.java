package com.fs.fsapi.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.fs.fsapi.bookmark.parser.FolderLink;

public class FileHelper {

  public static InputStream getValidFileAsInputStream() throws FileNotFoundException {
    return getFileAsInputStream(VALID_FILE);
  }

  public static InputStream getInvalidFileAsInputStream() throws FileNotFoundException {
    return getFileAsInputStream(INVALID_FILE);
  }

  private static InputStream getFileAsInputStream(String filename) throws FileNotFoundException {
    File initialFile = new File(TEST_FILES_LOCATION + "/" + filename);
    return new FileInputStream(initialFile);
  }

  private static final String TEST_FILES_LOCATION = "src/test/data";

  // files
  private static final String INVALID_FILE = "bookmarks-invalid.html";
  private static final String VALID_FILE = "bookmarks.html";

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

  public enum InvalidHeader {
    INVALID_STRUCTURE ("Example");

    private final String textContent;

    public String getTextContent() {
      return textContent;
    }

    InvalidHeader(String textContent) {
      this.textContent = textContent;
    }
  }

  // folderlinks created from the valid file
  private static final FolderLink f1 = new FolderLink(
    ElementHelper.createLinkElement(
      "Annihilator - Alice In Hell (1989)",
      "https://www.youtube.com/watch?v=IdRn9IYWuaQ",
      "1653126836"
    ),
    ValidHeader.CONTAINER.getTextContent()
  );

  private static final FolderLink f2 = new FolderLink(
    ElementHelper.createLinkElement(
      "A.O.D - Altars of Destruction (1988)",
      "https://www.youtube.com/watch?v=5av2oGfw34g&list=PLHz-VIInDvH9Wfr0oLNeOch_SIl6rA1ij&index=8",
      "1711378433"
    ),
    ValidHeader.PARENT.getTextContent()
  );

  private static final FolderLink f3 = new FolderLink(
    ElementHelper.createLinkElement(
      "Nuclear Assault - Survive (1988)",
      "https://www.youtube.com/watch?v=zopfZLQibWw",
      "1711378636"
    ),
    ValidHeader.PARENT.getTextContent()
  );

  private static final FolderLink f4 = new FolderLink(
    ElementHelper.createLinkElement(
      "Exodus - Fabulous Disaster (1989)",
      "https://www.youtube.com/watch?v=Zof79HxNpMs",
      "1711378682"
    ),
    ValidHeader.PARENT.getTextContent()
  );

  private static final FolderLink f5 = new FolderLink(
    ElementHelper.createLinkElement(
      "Angel Dust - Into the Dark Past (1986)",
      "https://www.youtube.com/watch?v=DopHEl-BCGQ",
      "1711378617"
    ),
    ValidHeader.CHILD.getTextContent()
  );

  private static final FolderLink f6 = new FolderLink(
    ElementHelper.createLinkElement(
      "Paradox - Product of Imagination (1987)",
      "https://www.youtube.com/watch?v=MV3yQFU3Z6s",
      "1711378656"
    ),
    ValidHeader.PARENT.getTextContent()
  );

  public static final FolderLink[] VALID_FILE_ROOT_LINKS = { f1, f2, f3, f4, f5, f6 };
  public static final FolderLink[] VALID_FILE_CONTAINER_LINKS = { f1, f2, f3, f4, f5, f6 };
  public static final FolderLink[] VALID_FILE_PARENT_LINKS = { f2, f3, f4, f5, f6 };
  public static final FolderLink[] VALID_FILE_CHILD_LINKS = { f5 };
  public static final FolderLink[] VALID_FILE_EMPTY_LINKS = { };
}
