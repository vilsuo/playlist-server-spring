package com.fs.fsapi.helpers;

import com.fs.fsapi.album.Album;
import com.fs.fsapi.album.AlbumCreation;
import com.fs.fsapi.bookmark.parser.AlbumParseResult;

public class AlbumHelper {

  /**
   * Mocked id to be assigned to an album upon saving.
   */
  public static final Integer MOCK_ID_1 = 123;

   /**
   * Mocked id to be assigned to an album upon saving.
   */
  public static final Integer MOCK_ID_2 = 124;

  /**
   * Mocked add date to be assigned to an album before saving.
   */
  public static final String MOCK_ADD_DATE_1 = "2024-08-14T10:33:57.604056616Z";

  /**
   * Mocked add date to be assigned to an album before saving.
   */
  public static final String MOCK_ADD_DATE_2 = "2024-09-05T22:26:40.701123555Z";

  /**
   * Mocked invalid add video id value
   */
  public static final String MOCK_INVALID_VIDEO_ID = "InVaLiD";

  public static AlbumCreation ALBUM_CREATION_VALUE_1() {
    return new AlbumCreation(
      "JMAbKMSuVfI",
      "Massacra",
      "Signs of the Decline",
      1992,
      "Death"
    );
  }

  public static final AlbumCreation ALBUM_CREATION_VALUE_2() {
    return new AlbumCreation(
      "qJVktESKhKY",
      "Devastation",
      "Idolatry",
      1991,
      "Thrash"
    );
  }

  /**
   * Has invalid video id {@link AlbumHelper#MOCK_INVALID_VIDEO_ID}.
   * @return
   */
  public static final AlbumCreation INVALID_ALBUM_CREATION_VALUE() {
    return new AlbumCreation(
      MOCK_INVALID_VIDEO_ID,
      "Black Crucifixion",
      "The Fallen One of Flames",
      1992,
      "Black"
    );
  }

  /**
   * Mock mapper result for mapping album creation value
   * {@link AlbumHelper#ALBUM_CREATION_VALUE_1} to album.
   * 
   * @return 
   */
  public static Album MOCK_MAPPED_ALBUM_CREATION_VALUE_1() {
    final AlbumCreation value = ALBUM_CREATION_VALUE_1();

    return new Album(
      null,
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      null
    );
  }

  /**
   * Mock mapper result for mapping album creation value
   * {@link AlbumHelper#ALBUM_CREATION_VALUE_2} to album.
   * 
   * @return 
   */
  public static Album MOCK_MAPPED_ALBUM_CREATION_VALUE_2() {
    final AlbumCreation value = ALBUM_CREATION_VALUE_2();
    
    return new Album(
      null,
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      null
    );
  }

  /**
   * Mock mapper result {@link AlbumHelper#MOCK_MAPPED_ALBUM_CREATION_VALUE_1}
   * with added add date {@link AlbumHelper#MOCK_ADD_DATE_1} value to album.
   * 
   * @return 
   */
  public static Album MOCK_MAPPED_ALBUM_CREATION_VALUE_WITH_ADD_DATE_1() {
    final Album value = MOCK_MAPPED_ALBUM_CREATION_VALUE_1();

    return new Album(
      value.getId(),
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      MOCK_ADD_DATE_1
    );
  }

  /**
   * Mock mapper result {@link AlbumHelper#MOCK_MAPPED_ALBUM_CREATION_VALUE_2}
   * with added add date {@link AlbumHelper#MOCK_ADD_DATE_2} value to album.
   * 
   * @return 
   */
  public static Album MOCK_MAPPED_ALBUM_CREATION_VALUE_WITH_ADD_DATE_2() {
    final Album value = MOCK_MAPPED_ALBUM_CREATION_VALUE_2();

    return new Album(
      value.getId(),
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      MOCK_ADD_DATE_2
    );
  }

  /**
   * Mock created album from
   * {@link AlbumHelper#MOCK_MAPPED_ALBUM_CREATION_VALUE_WITH_ADD_DATE_1}
   * with id {@link AlbumHelper#MOCK_ID_1}.
   * 
   * @return 
   */
  public static Album MOCK_ALBUM_1() {
    final Album value = MOCK_MAPPED_ALBUM_CREATION_VALUE_WITH_ADD_DATE_1();

    return new Album(
      MOCK_ID_1,
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      value.getAddDate()
    );
  }

  /**
   * Mock created album from
   * {@link AlbumHelper#MOCK_MAPPED_ALBUM_CREATION_VALUE_WITH_ADD_DATE_2}
   * with id {@link AlbumHelper#MOCK_ID_2}.
   * 
   * @return 
   */
  public static Album MOCK_ALBUM_2() {
    final Album value = MOCK_MAPPED_ALBUM_CREATION_VALUE_WITH_ADD_DATE_2();

    return new Album(
      MOCK_ID_2,
      value.getVideoId(),
      value.getArtist(),
      value.getTitle(),
      value.getPublished(),
      value.getCategory(),
      value.getAddDate()
    );
  }

  /**
   * Mock result for updating {@link AlbumHelper#MOCK_ALBUM_1}
   * with {@link AlbumHelper#ALBUM_CREATION_VALUE_2} values.
   * 
   * @return 
   */
  public static Album MOCK_UPDATED_ALBUM() {
    final Album oldValue = MOCK_ALBUM_1();
    final AlbumCreation newValue = ALBUM_CREATION_VALUE_2();

    return new Album(
      oldValue.getId(),
      newValue.getVideoId(),
      newValue.getArtist(),
      newValue.getTitle(),
      newValue.getPublished(),
      newValue.getCategory(),
      oldValue.getAddDate()
    );
  }


  // TODO MOVE TO A NEW FILE 'BookmarksHelper'


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
