package com.fs.fsapi.helpers;

import com.fs.fsapi.album.Album;
import com.fs.fsapi.album.AlbumCreation;
import com.fs.fsapi.bookmark.parser.AlbumParseResult;
import static com.fs.fsapi.helpers.AlbumHelper.*;

public class ParsedAlbumHelper {

  /**
   * Mock album bookmark parse result with values from 
   * {@link AlbumHelper#ALBUM_CREATION_VALUE_1} and add date
   * {@link AlbumHelper#MOCK_ADD_DATE_1}.
   * 
   * @return 
   */
  public static final AlbumParseResult MOCK_PARSE_RESULT_1() {
    final AlbumCreation value = ALBUM_CREATION_VALUE_1();

    return new AlbumParseResult(
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      MOCK_ADD_DATE_1
    );
  }
  
  /**
   * Mock album bookmark parse result with values from 
   * {@link AlbumHelper#ALBUM_CREATION_VALUE_2} and add date
   * {@link AlbumHelper#MOCK_ADD_DATE_2}.
   * 
   * @return 
   */
  public static final AlbumParseResult MOCK_PARSE_RESULT_2() {
    final AlbumCreation value = ALBUM_CREATION_VALUE_2();

    return new AlbumParseResult(
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      MOCK_ADD_DATE_2
    );
  }

  /**
   * Mock invalid album bookmark parse result with the invalid values
   * from {@link AlbumHelper#INVALID_ALBUM_CREATION_VALUE} with the
   * add date value {@link AlbumHelper#MOCK_ADD_DATE_1}.
   * <code>
   *  <strong>
   *    CHECK THAT AN ALBUM IS NOT ALREADY CREATED WITH THE ARTIST AND TITLE!
   *  </strong>
   * </code>
   * @return 
   */
  public static final AlbumParseResult MOCK_INVALID_PARSE_RESULT() {
    final AlbumCreation value = INVALID_ALBUM_CREATION_VALUE();

    return new AlbumParseResult(
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      MOCK_ADD_DATE_1
    );
  }

  /**
   * Mock mapper result for mapping album creation value
   * {@link ParsedAlbumHelper#MOCK_PARSE_RESULT_1} to album.
   * 
   * @return 
   */
  public static final Album MOCK_MAPPED_PARSE_RESULT_1() {
    final AlbumParseResult value = MOCK_PARSE_RESULT_1();

    return new Album(
      null,
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      value.getAddDate()
    );
  }

  /**
   * Mock mapper result for mapping album creation value
   * {@link ParsedAlbumHelper#MOCK_PARSE_RESULT_2} to album.
   * 
   * @return 
   */
  public static final Album MOCK_MAPPED_PARSE_RESULT_2() {
    final AlbumParseResult value = MOCK_PARSE_RESULT_2();

    return new Album(
      null,
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      value.getAddDate()
    );
  }


  private static final AlbumParseResult a1 = new AlbumParseResult(
    "IdRn9IYWuaQ",
    "Annihilator",
    "Alice In Hell",
    1989,
    BookmarksFileHelper.ValidHeader.CONTAINER.getTextContent(),
    "2022-05-21T09:53:56Z"
  );

  private static final AlbumParseResult a2 = new AlbumParseResult(
    "5av2oGfw34g",
    "A.O.D",
    "Altars of Destruction",
    1988,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:53:53Z"
  );

  private static final AlbumParseResult a3 = new AlbumParseResult(
    "zopfZLQibWw",
    "Nuclear Assault",
    "Survive",
    1988,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:57:16Z"
  );

  private static final AlbumParseResult a4 = new AlbumParseResult(
    "Zof79HxNpMs",
    "Exodus",
    "Fabulous Disaster",
    1989,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:58:02Z"
  );

  private static final AlbumParseResult a5 = new AlbumParseResult(
    "DopHEl-BCGQ",
    "Angel Dust",
    "Into the Dark Past",
    1986,
    BookmarksFileHelper.ValidHeader.CHILD.getTextContent(),
    "2024-03-25T14:56:57Z"
  );

  private static final AlbumParseResult a6 = new AlbumParseResult(
    "MV3yQFU3Z6s",
    "Paradox",
    "Product of Imagination",
    1987,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:57:36Z"
  );

  public static final AlbumParseResult[] VALID_FILE_ROOT_RESULTS = { a1, a2, a3, a4, a5, a6 };
  public static final AlbumParseResult[] VALID_FILE_CONTAINER_RESULTS = { a1, a2, a3, a4, a5, a6 };
  public static final AlbumParseResult[] VALID_FILE_PARENT_RESULTS = { a2, a3, a4, a5, a6 };
  public static final AlbumParseResult[] VALID_FILE_CHILD_RESULTS = { a5 };
  public static final AlbumParseResult[] VALID_FILE_EMPTY_RESULTS = { };
}
