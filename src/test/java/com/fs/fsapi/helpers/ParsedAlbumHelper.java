package com.fs.fsapi.helpers;

import com.fs.fsapi.bookmark.parser.AlbumParseResult;

public class ParsedAlbumHelper {

  public static final AlbumParseResult mockParseResult1 = new AlbumParseResult(
    AlbumHelper.creation1.getVideoId(),
    AlbumHelper.creation1.getArtist(),
    AlbumHelper.creation1.getTitle(),
    AlbumHelper.creation1.getPublished(),
    AlbumHelper.creation1.getCategory(),
    AlbumHelper.mockAddDate1
  );
  
  public static final AlbumParseResult mockParseResult2 = new AlbumParseResult(
    AlbumHelper.creation2.getVideoId(),
    AlbumHelper.creation2.getArtist(),
    AlbumHelper.creation2.getTitle(),
    AlbumHelper.creation2.getPublished(),
    AlbumHelper.creation2.getCategory(),
    AlbumHelper.mockAddDate2
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
