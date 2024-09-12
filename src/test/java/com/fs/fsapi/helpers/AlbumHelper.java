package com.fs.fsapi.helpers;

import com.fs.fsapi.bookmark.parser.AlbumResult;

public class AlbumHelper {
  
  private static final AlbumResult a1 = new AlbumResult(
    "IdRn9IYWuaQ",
    "Annihilator",
    "Alice In Hell",
    1989,
    BookmarksFileHelper.ValidHeader.CONTAINER.getTextContent(),
    "2022-05-21T09:53:56Z"
  );

  private static final AlbumResult a2 = new AlbumResult(
    "5av2oGfw34g",
    "A.O.D",
    "Altars of Destruction",
    1988,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:53:53Z"
  );

  private static final AlbumResult a3 = new AlbumResult(
    "zopfZLQibWw",
    "Nuclear Assault",
    "Survive",
    1988,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:57:16Z"
  );

  private static final AlbumResult a4 = new AlbumResult(
    "Zof79HxNpMs",
    "Exodus",
    "Fabulous Disaster",
    1989,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:58:02Z"
  );

  private static final AlbumResult a5 = new AlbumResult(
    "DopHEl-BCGQ",
    "Angel Dust",
    "Into the Dark Past",
    1986,
    BookmarksFileHelper.ValidHeader.CHILD.getTextContent(),
    "2024-03-25T14:56:57Z"
  );

  private static final AlbumResult a6 = new AlbumResult(
    "MV3yQFU3Z6s",
    "Paradox",
    "Product of Imagination",
    1987,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:57:36Z"
  );

  public static final AlbumResult[] VALID_FILE_ROOT_RESULTS = { a1, a2, a3, a4, a5, a6 };
  public static final AlbumResult[] VALID_FILE_CONTAINER_RESULTS = { a1, a2, a3, a4, a5, a6 };
  public static final AlbumResult[] VALID_FILE_PARENT_RESULTS = { a2, a3, a4, a5, a6 };
  public static final AlbumResult[] VALID_FILE_CHILD_RESULTS = { a5 };
  public static final AlbumResult[] VALID_FILE_EMPTY_RESULTS = { };
}
