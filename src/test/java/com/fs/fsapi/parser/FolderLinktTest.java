package com.fs.fsapi.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fs.fsapi.bookmark.parser.FolderLink;

public class FolderLinktTest {

  private final String text = "Annihilator - Alice In Hell (1989)";

  private final String href = "https://www.youtube.com/watch?v=IdRn9IYWuaQ";

  private final String addDate = "1653126836";

  private final String folderName = "Thrash";

  private final FolderLink folderLink = new FolderLink(
    FolderLinktTest.createLinkElement(text, href, addDate),
    folderName
  );

  public static Element createLinkElement(String text, String href, String addDate) {
    Attributes attrs = new Attributes();
    if (href != null) { attrs.add("href", href); }
    if (addDate != null) { attrs.add("add_date", addDate); }

    Element linkElement = new Element(Tag.valueOf("a"), null, attrs);
    if (text != null) { linkElement.appendText(text); }

    return linkElement;
  }

  @Nested
  @DisplayName("getText")
  public class Text {

    @Test
    public void shouldReturnElementTextContentTest() {
      assertEquals(text, folderLink.getText());
    }

    @Test
    public void shouldReturnEmptyTextWhenElementTextContentIsEmptyTest() {
      final FolderLink f = new FolderLink(
        FolderLinktTest.createLinkElement("", href, addDate),
        folderName
      );

      assertTrue(f.getText().isEmpty());
    }

    @Test
    public void shouldReturnEmptyTextWhenElementDoesNotHaveTextContentTest() {
      final FolderLink f = new FolderLink(
        FolderLinktTest.createLinkElement(null, href, addDate),
        folderName
      );

      assertTrue(f.getText().isEmpty());
    }
  }

  @Nested
  @DisplayName("getHref")
  public class Href {

    @Test
    public void shouldReturnElementHrefAttributeTest() {
      assertEquals(href, folderLink.getHref());
    }

    @Test
    public void shouldReturnEmptyWhenElementHrefAttributeIsEmptyTest() {
      final FolderLink f = new FolderLink(
        FolderLinktTest.createLinkElement(text, "", addDate),
        folderName
      );

      assertTrue(f.getHref().isEmpty());
    }

    @Test
    public void shouldReturnNullWhenElementDoesNotHaveHrefAttributeTest() {
      final FolderLink f = new FolderLink(
        FolderLinktTest.createLinkElement(text, null, addDate),
        folderName
      );

      assertNull(f.getHref());
    }
  }

  @Nested
  @DisplayName("getAddDate")
  public class AddDate {

    @Test
    public void getAddDate_shouldReturnElementAddDateAttributeTest() {
      assertEquals(addDate, folderLink.getAddDate());
    }

    @Test
    public void shouldReturnEmptyWhenElementAddDateAttributeIsEmptyTest() {
      final FolderLink f = new FolderLink(
        FolderLinktTest.createLinkElement(text, href, ""),
        folderName
      );

      assertTrue(f.getAddDate().isEmpty());
    }

    @Test
    public void shouldReturnNullWhenElementDoesNotHaveAddDateAttributeTest() {
      final FolderLink f = new FolderLink(
        FolderLinktTest.createLinkElement(text, href, null),
        folderName
      );

      assertNull(f.getAddDate());
    }
  }
}
