package com.fs.fsapi.parser;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.jupiter.api.Test;

import com.fs.fsapi.bookmark.parser.AlbumBase;
import com.fs.fsapi.bookmark.parser.FolderLink;
import com.fs.fsapi.bookmark.parser.LinkParserService;

public class LinkParserServiceTest {
  
  private final LinkParserService service = new LinkParserService();

  private Element createLinkElement(String text, String href, String addDate) {
    Attributes attrs = new Attributes();
    if (href != null) { attrs.add("href", href); }
    if (addDate != null) { attrs.add("add_date", addDate); }

    Element linkElement = new Element(Tag.valueOf("a"), null, attrs);
    linkElement.appendText(text);

    return linkElement;
  }

  @Test
  public void shouldReturnEmptyListWhenNoFolderLinksTest() {
    assertTrue(service.createAlbumBases(List.of()).isEmpty());
  }

  @Test
  public void shouldReturnParsedAlbumBaseTest() {
    String folderName = "Thrash";
    String text = "Annihilator - Alice In Hell (1989)";
    String href = "https://www.youtube.com/watch?v=IdRn9IYWuaQ";
    String addDate = "1653126836";
    
    Element e = createLinkElement(text, href, addDate);

    List<AlbumBase> result = service.createAlbumBases(List.of(new FolderLink(e, folderName)));
    assertEquals(1, result.size());

    AlbumBase base = result.get(0);
    assertEquals("IdRn9IYWuaQ", base.getVideoId());
    assertEquals("Annihilator", base.getArtist());
    assertEquals("Alice In Hell", base.getTitle());
    assertEquals(1989, base.getPublished());
    assertEquals(folderName, base.getCategory());
    assertEquals("2022-05-21T09:53:56Z", base.getAddDate());

  }
}
