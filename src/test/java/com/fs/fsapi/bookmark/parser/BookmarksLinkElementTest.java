package com.fs.fsapi.bookmark.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fs.fsapi.helpers.ElementHelper;

public class BookmarksLinkElementTest {

  private final String text = "Annihilator - Alice In Hell (1989)";

  private final String href = "https://www.youtube.com/watch?v=IdRn9IYWuaQ";

  private final String addDate = "1653126836";

  private final String folderName = "Thrash";

  private final BookmarksLinkElement folderLink = new BookmarksLinkElement(
    ElementHelper.createBookmarkLinkTypeElement(text, href, addDate),
    folderName
  );

  @Test
  public void shouldThrowWhenCreatedWithoutAddDateAttributeTest() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new BookmarksLinkElement(
        ElementHelper.createBookmarkLinkTypeElement(text, href, null),
        folderName
      )
    );
  }

  @Nested
  @DisplayName("getText")
  public class Text {

    @Test
    public void shouldReturnElementTextContentWhenItExistsTest() {
      assertEquals(text, folderLink.getText());
    }

    @Test
    public void shouldReturnEmptyTextWhenElementTextContentIsEmptyTest() {
      final BookmarksLinkElement f = new BookmarksLinkElement(
        ElementHelper.createBookmarkLinkTypeElement("", href, addDate),
        folderName
      );

      assertTrue(f.getText().isEmpty());
    }

    @Test
    public void shouldReturnEmptyTextWhenElementDoesNotHaveTextContentTest() {
      final BookmarksLinkElement f = new BookmarksLinkElement(
        ElementHelper.createBookmarkLinkTypeElement(null, href, addDate),
        folderName
      );

      assertTrue(f.getText().isEmpty());
    }
  }

  @Nested
  @DisplayName("getHref")
  public class Href {

    @Test
    public void shouldReturnElementHrefAttributeWhenItExistsTest() {
      assertEquals(href, folderLink.getHref());
    }

    @Test
    public void shouldReturnEmptyWhenElementHrefAttributeIsEmptyTest() {
      final BookmarksLinkElement f = new BookmarksLinkElement(
        ElementHelper.createBookmarkLinkTypeElement(text, "", addDate),
        folderName
      );

      assertTrue(f.getHref().isEmpty());
    }
  }

  @Nested
  @DisplayName("getAddDate")
  public class AddDate {

    @Test
    public void shouldReturnElementAddDateAttributeWhenItExistsTest() {
      assertEquals(addDate, folderLink.getAddDate());
    }

    @Test
    public void shouldReturnEmptyWhenElementAddDateAttributeIsEmptyTest() {
      final BookmarksLinkElement f = new BookmarksLinkElement(
        ElementHelper.createBookmarkLinkTypeElement(text, href, ""),
        folderName
      );

      assertTrue(f.getAddDate().isEmpty());
    }
  }
}
