package com.fs.fsapi.helpers;

import com.fs.fsapi.album.Album;
import com.fs.fsapi.album.AlbumCreation;
import com.fs.fsapi.bookmark.parser.AlbumParseResult;

public class AlbumHelper {

  /**
   * Mocked id to be assigned to an album upon saving.
   */
  public static final Integer mockId1 = 123;

   /**
   * Mocked id to be assigned to an album upon saving.
   */
  public static final Integer mockId2 = 124;

  /**
   * Mocked add date to be assigned to an album before saving.
   */
  public static final String mockAddDate1 = "2024-08-14T10:33:57.604056616Z";

  /**
   * Mocked add date to be assigned to an album before saving.
   */
  public static final String mockAddDate2 = "2024-09-05T22:26:40.701123555Z";

  public static final AlbumCreation creation1 = new AlbumCreation(
    "JMAbKMSuVfI",
    "Massacra",
    "Signs of the Decline",
    1992,
    "Death"
  );

  public static final AlbumCreation creation2 = new AlbumCreation(
    "qJVktESKhKY",
    "Devastation",
    "Idolatry",
    1991,
    "Thrash"
  );

  /**
   * Mock mapper result for mapping album creation value
   * {@link AlbumHelper#creation1} to album.
   */
  public static final Album mockMappedAlbum1 = new Album(
    null,
    creation1.getVideoId(),
    creation1.getArtist(),
    creation1.getTitle(),
    creation1.getPublished(),
    creation1.getCategory(),
    null
  );

  /**
   * Mock mapper result for mapping album creation value
   * {@link AlbumHelper#creation2} to album.
   */
  public static final Album mockMappedAlbum2 = new Album(
    null,
    creation2.getVideoId(),
    creation2.getArtist(),
    creation2.getTitle(),
    creation2.getPublished(),
    creation2.getCategory(),
    null
  );

  /**
   * Mock mapper result {@link AlbumHelper#mockMappedAlbum1} with
   * added add date {@link AlbumHelper#mockAddDate1} value to album.
   */
  public static final Album mockMappedAlbumWithAddDate1 = new Album(
    mockMappedAlbum1.getId(),
    mockMappedAlbum1.getVideoId(),
    mockMappedAlbum1.getArtist(),
    mockMappedAlbum1.getTitle(),
    mockMappedAlbum1.getPublished(),
    mockMappedAlbum1.getCategory(),
    mockAddDate1
  );

  /**
   * Mock mapper result {@link AlbumHelper#mockMappedAlbum2} with
   * added add date {@link AlbumHelper#mockAddDate2} value to album.
   */
  public static final Album mockMappedAlbumWithAddDate2 = new Album(
    mockMappedAlbum2.getId(),
    mockMappedAlbum2.getVideoId(),
    mockMappedAlbum2.getArtist(),
    mockMappedAlbum2.getTitle(),
    mockMappedAlbum2.getPublished(),
    mockMappedAlbum2.getCategory(),
    mockAddDate2
  );

  /**
   * Mock created album from {@link AlbumHelper#mockMappedAlbumWithAddDate1}
   * with id {@link AlbumHelper#mockId1}.
   */
  public static final Album mockAlbum1 = new Album(
    mockId1,
    mockMappedAlbumWithAddDate1.getVideoId(),
    mockMappedAlbumWithAddDate1.getArtist(),
    mockMappedAlbumWithAddDate1.getTitle(),
    mockMappedAlbumWithAddDate1.getPublished(),
    mockMappedAlbumWithAddDate1.getCategory(),
    mockMappedAlbumWithAddDate1.getAddDate()
  );

  /**
   * Mock created album from {@link AlbumHelper#mockMappedAlbumWithAddDate2}
   * with id {@link AlbumHelper#mockId2}.
   */
  public static final Album mockAlbum2 = new Album(
    mockId2,
    mockMappedAlbumWithAddDate2.getVideoId(),
    mockMappedAlbumWithAddDate2.getArtist(),
    mockMappedAlbumWithAddDate2.getTitle(),
    mockMappedAlbumWithAddDate2.getPublished(),
    mockMappedAlbumWithAddDate2.getCategory(),
    mockMappedAlbumWithAddDate2.getAddDate()
  );

  /**
   * Mock result for updating {@link AlbumHelper#mockAlbum1}
   * with {@link AlbumHelper#creation2} values.
   */
  public static final Album mockUpdatedAlbum = new Album(
    mockAlbum1.getId(),
    creation2.getVideoId(),
    creation2.getArtist(),
    creation2.getTitle(),
    creation2.getPublished(),
    creation2.getCategory(),
    mockAlbum1.getAddDate()
  );

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
