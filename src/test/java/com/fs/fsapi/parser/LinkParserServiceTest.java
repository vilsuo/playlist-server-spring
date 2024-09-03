package com.fs.fsapi.parser;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.fs.fsapi.bookmark.parser.AlbumBase;
import com.fs.fsapi.bookmark.parser.FolderLink;
import com.fs.fsapi.bookmark.parser.LinkParserService;
import com.fs.fsapi.exceptions.CustomHtmlParsingException;

public class LinkParserServiceTest {
  
  private final LinkParserService service = new LinkParserService();

  private final String folderName = "Thrash";
  private final String text = "Annihilator - Alice In Hell (1989)";
  private final String href = "https://www.youtube.com/watch?v=IdRn9IYWuaQ";
  private final String addDate = "1653126836";

  private AlbumBase parseSingle(String folderName, String text, String href, String addDate) {
    Element e = FolderLinktTest.createLinkElement(text, href, addDate);
    return service.createAlbumBases(List.of(new FolderLink(e, folderName))).get(0);
  }

  @Test
  public void shouldReturnEmptyListWhenNoFolderLinksTest() {
    assertTrue(service.createAlbumBases(List.of()).isEmpty());
  }

  @Test
  public void shouldReturnParsedAlbumBaseTest() {
    Element e = FolderLinktTest.createLinkElement(text, href, addDate);

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

  @Nested
  @DisplayName("extractVideoId")
  public class Href {

    private AlbumBase parseSingleWithHref(String hrefValue) {
      return parseSingle(folderName, text, hrefValue, addDate);
    }

    @Test
    public void shouldReturnEmptyWhenRequiredHrefQueryParameterValueIsEmptyTest() {
      String withEmptyParam = "https://www.youtube.com/watch?v=";

      AlbumBase base = parseSingleWithHref(withEmptyParam);
      assertTrue(base.getVideoId().isEmpty());
    }

    @Test
    public void shouldReturnCorrectQueryParameterValueWhenHrefHasMultipleQueryParametersTest() {
      String withMultipleParams = "https://www.youtube.com/watch?g=aspiasdihd&v=IdRn9IYWuaQ";

      AlbumBase base = parseSingleWithHref(withMultipleParams);
      assertEquals("IdRn9IYWuaQ", base.getVideoId());
    }
    
    @Test
    public void shouldThrowWhenMissingHrefAttributeTest() {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithHref(null)
      );

      assertEquals("Link 'href' attribute is missing", ex.getMessage());
    }

    @Test
    public void shouldThrowWhenHrefAttributeHasInvalidPrefixTest() {
      String invalidHref = "https://www.google.com/watch?v=IdRn9IYWuaQ";

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithHref(invalidHref)
      );

      assertEquals(
        "Link href attribute '" + invalidHref
        + "' must start with the prefix 'https://www.youtube.com/watch?'",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenHrefDoesNotHaveQueryParametersTest() {
      String invalidHref = "https://www.youtube.com/watch?";

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithHref(invalidHref)
      );

      assertEquals(
        "Link href attribute '" + invalidHref
        + "' is missing a required query parameter 'v'",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenHrefHasWrongQueryParameterTest() {
      String invalidHref = "https://www.youtube.com/watch?sv=IdRn9IYWuaQ";

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithHref(invalidHref)
      );

      assertEquals(
        "Link href attribute '" + invalidHref
        + "' is missing a required query parameter 'v'",
        ex.getMessage()
      );
    }
  }

  @Nested
  @DisplayName("extractTextContentDetails")
  public class TextContent {

    private AlbumBase parseSingleWithText(String textValue) {
      return parseSingle(folderName, textValue, href, addDate);
    }

    @Test
    public void shouldExtractArtistFromTextContentTest() {
      assertEquals("Annihilator", parseSingleWithText(text).getArtist());
    }

    @Test
    public void shouldExtractTitleFromTextContentTest() {
      assertEquals("Alice In Hell", parseSingleWithText(text).getTitle());
    }

    @Test
    public void shouldExtractPublishedFromTextContentTest() {
      assertEquals(1989, parseSingleWithText(text).getPublished());
    }

    private static Stream<Arguments> argumentProvider() {
      return Stream.of(
        Arguments.of("Annihilator Alice In Hell (1989)", "missing artist-title separator"),
        Arguments.of("Annihilator - Alice In Hell 1989", "missing braces in published year"),
        Arguments.of("Annihilator - Alice In Hell", "missing published year"),
        Arguments.of("Annihilator", "only artist name is given")
      );
    }

    @ParameterizedTest(name = "{index} should throw when {1}")
    @MethodSource("argumentProvider")
    public void shouldThrowWhenMissingSeparatorInTextContentTest(String value, String description) {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithText(value)
      );

      assertEquals(
        "Link text content '" + value + "' is in incorrect format",
        ex.getMessage()
      );
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldThrowWhenTextContentIsNullOrEmptyTest(String value) {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithText(value)
      );

      assertEquals(
        "Link text content '' is in incorrect format",
        ex.getMessage()
      );
    }

  }

  @Nested
  @DisplayName("parseAddDate")
  public class AddDate {

    private AlbumBase parseSingleWithAddDate(String addDateValue) {
      return parseSingle(folderName, text, href, addDateValue);
    }

    @Test
    public void shouldConvertWhenAddDateAttributeIsPositiveTest() {
      String result = parseSingleWithAddDate("1653126836").getAddDate();

      assertEquals("2022-05-21T09:53:56Z", result);
    }

    @Test
    public void shouldConvertWhenAddDateAttributeIsNegativeTest() {
      String result = parseSingleWithAddDate("-1653126836").getAddDate();
      
      assertEquals("1917-08-13T14:06:04Z", result);
    }

    @Test
    public void shouldReturnUnixExpochZeroWhenAddDateAttributeIsZeroTest() {
      String result = parseSingleWithAddDate("0").getAddDate();

      assertEquals("1970-01-01T00:00:00Z", result);
    }

    @Test
    public void shouldThrowWhenMissingAddDateAttributeTest() {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithAddDate(null)
      );

      assertEquals(
        "Link 'add_date' attribute is missing",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenAddDateAttributeIsEmptyTest() {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithAddDate("")
      );

      assertEquals(
        "Link add date attribute '' is not a valid number",
        ex.getMessage()
      );
    }

    @ParameterizedTest
    @ValueSource(strings = { "2022-05-21T09:53:56Z", "1653126x360" })
    public void shouldThrowWhenAddDateAttributeContainsCharactersTest(String value) {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithAddDate(value)
      );

      assertEquals(
        "Link add date attribute '" + value + "' is not a valid number",
        ex.getMessage()
      );
    }
  }
}
