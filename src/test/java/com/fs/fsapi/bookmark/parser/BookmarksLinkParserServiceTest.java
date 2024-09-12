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
import org.junit.jupiter.params.provider.ValueSource;

import com.fs.fsapi.exceptions.CustomHtmlParsingException;
import com.fs.fsapi.helpers.AlbumHelper;
import com.fs.fsapi.helpers.ElementHelper;
import com.fs.fsapi.helpers.BookmarksFileHelper;

public class BookmarksLinkParserServiceTest {
  
  private final BookmarksLinkParserService service = new BookmarksLinkParserService();

  private final BookmarksLinkElement source = BookmarksFileHelper.VALID_FILE_CONTAINER_LINKS[0];
  private final AlbumParseResult expected = AlbumHelper.VALID_FILE_CONTAINER_RESULTS[0];

  private final String headerText = BookmarksFileHelper.ValidHeader.CONTAINER.getTextContent();
  private final String text = source.getText();
  private final String href = source.getHref();
  private final String addDate = source.getAddDate();

  private AlbumParseResult parseSingle(String headerText, String text, String href, String addDate) {
    Element e = ElementHelper.createBookmarkLinkTypeElement(text, href, addDate);
    return service.parse(List.of(new BookmarksLinkElement(e, headerText))).get(0);
  }

  @Test
  public void shouldReturnEmptyListWhenValuesToParseIsEmptyTest() {
    assertTrue(service.parse(List.of()).isEmpty());
  }

  @Test
  public void shouldReturnParsedValuesTest() {
    List<AlbumParseResult> actual = service.parse(List.of(source));

    assertEquals(1, actual.size());
    AlbumParseResult result = actual.get(0);

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

    private AlbumParseResult parseSingleWithHref(String hrefValue) {
      return parseSingle(headerText, text, hrefValue, addDate);
    }

    @Test
    public void shouldHaveEmptyVideoIdWhenRequiredHrefQueryParameterValueIsEmptyTest() {
      String emptyParamVideoIdParameter = "https://www.youtube.com/watch?v=";

      AlbumParseResult actual = parseSingleWithHref(emptyParamVideoIdParameter);
      assertTrue(actual.getVideoId().isEmpty());
    }

    @Test
    public void shouldCorrectVideoIdWhenHrefHasAlsoOtherQueryParametersTest() {
      String multipleParameters = "https://www.youtube.com/watch?g=aspiasdihd&v=IdRn9IYWuaQ";

      AlbumParseResult actual = parseSingleWithHref(multipleParameters);
      assertEquals("IdRn9IYWuaQ", actual.getVideoId());
    }

    @Test
    public void shouldThrowWhenHrefAttributeHasInvalidPrefixTest() {
      String invalidHrefPrefix = "https://www.google.com/watch?v=IdRn9IYWuaQ";

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithHref(invalidHrefPrefix)
      );

      assertEquals(
        "Expected the 'href' attribute value '" + invalidHrefPrefix
        + "' to start with 'https://www.youtube.com/watch?'",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenHrefDoesNotHaveQueryParametersTest() {
      String noParameters = "https://www.youtube.com/watch?";

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithHref(noParameters)
      );

      assertEquals(
        "Expected 'href' attribute value '" + noParameters
        + "' to have a value for the query parameter 'v'",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenHrefHasWrongQueryParameterTest() {
      String invalidParameter = "https://www.youtube.com/watch?sv=IdRn9IYWuaQ";

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithHref(invalidParameter)
      );

      assertEquals(
        "Expected 'href' attribute value '" + invalidParameter
        + "' to have a value for the query parameter 'v'",
        ex.getMessage()
      );
    }
  }

  @Nested
  @DisplayName("extractTextContentDetails")
  public class TextContent {

    private AlbumParseResult parseSingleWithText(String textValue) {
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
    public void shouldThrowWhenTextContentHasInvalidStructureTest(
      String invalidTextContentStructure, String description
    ) {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithText(invalidTextContentStructure)
      );

      assertEquals(
        "The text content '" + invalidTextContentStructure + "' is invalid",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenTextContentIsEmptyTest() {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithText("")
      );

      assertEquals(
        "The text content '' is invalid",
        ex.getMessage()
      );
    }

  }

  @Nested
  @DisplayName("parseAddDate")
  public class AddDate {

    private AlbumParseResult parseSingleWithAddDate(String addDateValue) {
      return parseSingle(headerText, text, href, addDateValue);
    }

    @Test
    public void shouldConvertWhenAddDateAttributeIsPositiveTest() {
      AlbumParseResult actual = parseSingleWithAddDate(addDate);
      assertEquals(expected.getAddDate(), actual.getAddDate());
    }

    @Test
    public void shouldConvertWhenAddDateAttributeIsNegativeTest() {
      AlbumParseResult actual = parseSingleWithAddDate("-1653126836");
      assertEquals("1917-08-13T14:06:04Z", actual.getAddDate());
    }

    @Test
    public void shouldReturnUnixExpochZeroWhenAddDateAttributeIsZeroTest() {
      AlbumParseResult actual = parseSingleWithAddDate("0");
      assertEquals("1970-01-01T00:00:00Z", actual.getAddDate());
    }

    @Test
    public void shouldThrowWhenAddDateAttributeIsEmptyTest() {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithAddDate("")
      );

      assertEquals(
        "The 'add_date' attribute value '' is invalid",
        ex.getMessage()
      );
    }

    @ParameterizedTest
    @ValueSource(strings = { "2022-05-21T09:53:56Z", "1653126x360" })
    public void shouldThrowWhenAddDateAttributeHasInvalidValue(String invalidAddDateValue) {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithAddDate(invalidAddDateValue)
      );

      assertEquals(
        "The 'add_date' attribute value '" + invalidAddDateValue + "' is invalid",
        ex.getMessage()
      );
    }
  }
}
