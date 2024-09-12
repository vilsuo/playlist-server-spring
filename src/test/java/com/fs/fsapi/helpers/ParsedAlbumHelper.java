package com.fs.fsapi.helpers;

import com.fs.fsapi.album.Album;
import com.fs.fsapi.bookmark.parser.AlbumParseResult;

public class ParsedAlbumHelper {

  /**
   * Mock album bookmark parse result with values from 
   * {@link AlbumHelper#ALBUM_CREATION_VALUE_1} and add date
   * {@link AlbumHelper#MOCK_ADD_DATE_1}.
   */
  public static final AlbumParseResult MOCK_PARSE_RESULT_1 = new AlbumParseResult(
    AlbumHelper.ALBUM_CREATION_VALUE_1.getVideoId(),
    AlbumHelper.ALBUM_CREATION_VALUE_1.getArtist(),
    AlbumHelper.ALBUM_CREATION_VALUE_1.getTitle(),
    AlbumHelper.ALBUM_CREATION_VALUE_1.getPublished(),
    AlbumHelper.ALBUM_CREATION_VALUE_1.getCategory(),
    AlbumHelper.MOCK_ADD_DATE_1
  );
  
  /**
   * Mock album bookmark parse result with values from 
   * {@link AlbumHelper#ALBUM_CREATION_VALUE_2} and add date
   * {@link AlbumHelper#MOCK_ADD_DATE_2}.
   */
  public static final AlbumParseResult MOCK_PARSE_RESULT_2 = new AlbumParseResult(
    AlbumHelper.ALBUM_CREATION_VALUE_2.getVideoId(),
    AlbumHelper.ALBUM_CREATION_VALUE_2.getArtist(),
    AlbumHelper.ALBUM_CREATION_VALUE_2.getTitle(),
    AlbumHelper.ALBUM_CREATION_VALUE_2.getPublished(),
    AlbumHelper.ALBUM_CREATION_VALUE_2.getCategory(),
    AlbumHelper.MOCK_ADD_DATE_2
  );

  /**
   * Mock mapper result for mapping album creation value
   * {@link ParsedAlbumHelper#MOCK_PARSE_RESULT_1} to album.
   */
  public static final Album MOCK_MAPPED_PARSE_RESULT_1 = new Album(
    null,
    MOCK_PARSE_RESULT_1.getVideoId(),
    MOCK_PARSE_RESULT_1.getArtist(),
    MOCK_PARSE_RESULT_1.getTitle(),
    MOCK_PARSE_RESULT_1.getPublished(),
    MOCK_PARSE_RESULT_1.getCategory(),
    MOCK_PARSE_RESULT_1.getAddDate()
  );

  /**
   * Mock mapper result for mapping album creation value
   * {@link ParsedAlbumHelper#MOCK_PARSE_RESULT_2} to album.
   */
  public static final Album MOCK_MAPPED_PARSE_RESULT_2 = new Album(
    null,
    MOCK_PARSE_RESULT_2.getVideoId(),
    MOCK_PARSE_RESULT_2.getArtist(),
    MOCK_PARSE_RESULT_2.getTitle(),
    MOCK_PARSE_RESULT_2.getPublished(),
    MOCK_PARSE_RESULT_2.getCategory(),
    MOCK_PARSE_RESULT_2.getAddDate()
  );


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
