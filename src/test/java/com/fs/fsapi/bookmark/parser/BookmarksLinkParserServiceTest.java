package com.fs.fsapi.bookmark.parser;

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

import com.fs.fsapi.exceptions.CustomHtmlParsingException;
import com.fs.fsapi.helpers.AlbumHelper;
import com.fs.fsapi.helpers.ElementHelper;
import com.fs.fsapi.helpers.FileHelper;

public class BookmarksLinkParserServiceTest {
  
  private final BookmarksLinkParserService service = new BookmarksLinkParserService();

  private final BookmarksLinkElement source = FileHelper.VALID_FILE_CONTAINER_LINKS[0];
  private final AlbumBase expected = AlbumHelper.VALID_FILE_CONTAINER_ALBUMBASES[0];

  private final String headerText = FileHelper.ValidHeader.CONTAINER.getTextContent();
  private final String text = source.getText();
  private final String href = source.getHref();
  private final String addDate = source.getAddDate();

  private AlbumBase parseSingle(String folderName, String text, String href, String addDate) {
    Element e = ElementHelper.createLinkTypeElement(text, href, addDate);
    return service.parseElements(List.of(new BookmarksLinkElement(e, folderName))).get(0);
  }

  @Test
  public void shouldReturnEmptyListWhenNoFolderLinksTest() {
    assertTrue(service.parseElements(List.of()).isEmpty());
  }

  @Test
  public void shouldReturnParsedAlbumBaseTest() {
    List<AlbumBase> results = service.parseElements(List.of(source));

    assertEquals(1, results.size());

    AlbumBase result = results.get(0);
    assertEquals(expected.getVideoId(), result.getVideoId());
    assertEquals(expected.getArtist(), result.getArtist());
    assertEquals(expected.getTitle(), result.getTitle());
    assertEquals(expected.getPublished(), result.getPublished());
    assertEquals(expected.getCategory(), result.getCategory());
    assertEquals(expected.getAddDate(), result.getAddDate());
  }

  @Nested
  @DisplayName("extractVideoId")
  public class Href {

    private AlbumBase parseSingleWithHref(String hrefValue) {
      return parseSingle(headerText, text, hrefValue, addDate);
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
      return parseSingle(headerText, textValue, href, addDate);
    }

    @Test
    public void shouldExtractArtistFromTextContentTest() {
      assertEquals(expected.getArtist(), parseSingleWithText(text).getArtist());
    }

    @Test
    public void shouldExtractTitleFromTextContentTest() {
      assertEquals(expected.getTitle(), parseSingleWithText(text).getTitle());
    }

    @Test
    public void shouldExtractPublishedFromTextContentTest() {
      assertEquals(expected.getPublished(), parseSingleWithText(text).getPublished());
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
      return parseSingle(headerText, text, href, addDateValue);
    }

    @Test
    public void shouldConvertWhenAddDateAttributeIsPositiveTest() {
      AlbumBase result = parseSingleWithAddDate(addDate);

      assertEquals(expected.getAddDate(), result.getAddDate());
    }

    @Test
    public void shouldConvertWhenAddDateAttributeIsNegativeTest() {
      AlbumBase result = parseSingleWithAddDate("-1653126836");
      
      assertEquals("1917-08-13T14:06:04Z", result.getAddDate());
    }

    @Test
    public void shouldReturnUnixExpochZeroWhenAddDateAttributeIsZeroTest() {
      AlbumBase result = parseSingleWithAddDate("0");

      assertEquals("1970-01-01T00:00:00Z", result.getAddDate());
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
