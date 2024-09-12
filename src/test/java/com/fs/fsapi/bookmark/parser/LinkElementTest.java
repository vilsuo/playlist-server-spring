package com.fs.fsapi.bookmark.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jsoup.nodes.Attributes;
import org.junit.jupiter.api.Test;

import com.fs.fsapi.helpers.ElementHelper;

public class LinkElementTest {
  
  private final String text = "Annihilator - Alice In Hell (1989)";

  private final String href = "https://www.youtube.com/watch?v=IdRn9IYWuaQ";

  @Test
  public void shouldThrowWhenCreatedWithNonLinkElementTest() {
    final String nonLinkTag = "p";
    Attributes attrs = new Attributes();
    attrs.add("href", href);

    assertThrows(
      IllegalArgumentException.class,
      () -> new LinkElement(
        ElementHelper.createElement(nonLinkTag, text, attrs)
      )
    );
  }

  @Test
  public void shouldThrowWhenCreatedWithoutHrefAttributeTest() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new LinkElement(
        ElementHelper.createLinkTypeElement(text, null)
      )
    );
  }
}
