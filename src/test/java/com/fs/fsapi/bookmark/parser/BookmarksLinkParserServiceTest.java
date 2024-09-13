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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fs.fsapi.exceptions.CustomHtmlParsingException;
import com.fs.fsapi.helpers.ElementHelper;
import com.fs.fsapi.helpers.ParsedAlbumHelper;
import com.fs.fsapi.helpers.BookmarksFileHelper;

@SpringBootTest(classes = { BookmarksLinkParserService.class })
public class BookmarksLinkParserServiceTest {
  
  @Autowired
  private BookmarksLinkParserService service;

  private final BookmarksLinkElement source = BookmarksFileHelper.VALID_FILE_CONTAINER_LINKS[0];
  private final AlbumParseResult expected = ParsedAlbumHelper.VALID_FILE_CONTAINER_RESULTS[0];

  private final String expectedHeaderText = BookmarksFileHelper.ValidHeader.CONTAINER.getTextContent();
  private final String expectedText = source.getText();
  private final String expectedHref = source.getHref();
  private final String expectedAddDate = source.getAddDate();

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
    List<AlbumParseResult> actuals = service.parse(List.of(source));

    assertEquals(1, actuals.size());
    final AlbumParseResult actual = actuals.get(0);

    assertEquals(expected.getVideoId(), actual.getVideoId());
    assertEquals(expected.getArtist(), actual.getArtist());
    assertEquals(expected.getTitle(), actual.getTitle());
    assertEquals(expected.getPublished(), actual.getPublished());
    assertEquals(expected.getCategory(), actual.getCategory());
    assertEquals(expected.getAddDate(), actual.getAddDate());
  }

  @Nested
  @DisplayName("extractVideoId")
  public class Href {

    private AlbumParseResult parseSingleWithHref(String hrefValue) {
      return parseSingle(expectedHeaderText, expectedText, hrefValue, expectedAddDate);
    }

    @Test
    public void shouldHaveEmptyVideoIdWhenRequiredHrefQueryParameterValueIsEmptyTest() {
      final String href = "https://www.youtube.com/watch?v=";

      final AlbumParseResult actual = parseSingleWithHref(href);
      assertTrue(actual.getVideoId().isEmpty());
    }

    @Test
    public void shouldCorrectVideoIdWhenHrefHasAlsoOtherQueryParametersTest() {
      final String href = "https://www.youtube.com/watch?g=aspiasdihd&v=IdRn9IYWuaQ";

      final AlbumParseResult actual = parseSingleWithHref(href);
      assertEquals("IdRn9IYWuaQ", actual.getVideoId());
    }

    @Test
    public void shouldThrowWhenHrefAttributeHasInvalidPrefixTest() {
      final String href = "https://www.google.com/watch?v=IdRn9IYWuaQ";

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithHref(href)
      );

      assertEquals(
        "Expected the 'href' attribute value '" + href
        + "' to start with 'https://www.youtube.com/watch?'",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenHrefDoesNotHaveQueryParametersTest() {
      final String href = "https://www.youtube.com/watch?";

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithHref(href)
      );

      assertEquals(
        "Expected 'href' attribute value '" + href
        + "' to have a value for the query parameter 'v'",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenHrefHasWrongQueryParameterTest() {
      final String href = "https://www.youtube.com/watch?sv=IdRn9IYWuaQ";

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithHref(href)
      );

      assertEquals(
        "Expected 'href' attribute value '" + href
        + "' to have a value for the query parameter 'v'",
        ex.getMessage()
      );
    }
  }

  @Nested
  @DisplayName("extractTextContentDetails")
  public class TextContent {

    private AlbumParseResult parseSingleWithText(String textValue) {
      return parseSingle(expectedHeaderText, textValue, expectedHref, expectedAddDate);
    }

    @Test
    public void shouldExtractArtistFromTextContentTest() {
      final AlbumParseResult actual = parseSingleWithText(expectedText);
      assertEquals(expected.getArtist(), actual.getArtist());
    }

    @Test
    public void shouldExtractTitleFromTextContentTest() {
      final AlbumParseResult actual = parseSingleWithText(expectedText);
      assertEquals(expected.getTitle(), actual.getTitle());
    }

    @Test
    public void shouldExtractPublishedFromTextContentTest() {
      final AlbumParseResult actual = parseSingleWithText(expectedText);
      assertEquals(expected.getPublished(), actual.getPublished());
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
      String text, String description
    ) {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithText(text)
      );

      assertEquals(
        "The text content '" + text + "' is invalid",
        ex.getMessage()
      );
    }

    @Test
    public void shouldThrowWhenTextContentIsEmptyTest() {
      final String text = "";

      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithText(text)
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
      return parseSingle(expectedHeaderText, expectedText, expectedHref, addDateValue);
    }

    @Test
    public void shouldConvertWhenAddDateAttributeIsPositiveTest() {
      final AlbumParseResult actual = parseSingleWithAddDate(expectedAddDate);
      assertEquals(expected.getAddDate(), actual.getAddDate());
    }

    @Test
    public void shouldConvertWhenAddDateAttributeIsNegativeTest() {
      final AlbumParseResult actual = parseSingleWithAddDate("-1653126836");
      assertEquals("1917-08-13T14:06:04Z", actual.getAddDate());
    }

    @Test
    public void shouldReturnUnixExpochZeroWhenAddDateAttributeIsZeroTest() {
      final AlbumParseResult actual = parseSingleWithAddDate("0");
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
    public void shouldThrowWhenAddDateAttributeHasInvalidValue(String addDate) {
      CustomHtmlParsingException ex = assertThrows(
        CustomHtmlParsingException.class,
        () -> parseSingleWithAddDate(addDate)
      );

      assertEquals(
        "The 'add_date' attribute value '" + addDate + "' is invalid",
        ex.getMessage()
      );
    }
  }
}
