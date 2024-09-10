package com.fs.fsapi.helpers;

import com.fs.fsapi.bookmark.parser.AlbumBase;

public class AlbumHelper {
  
  private static final AlbumBase a1 = new AlbumBase(
    "IdRn9IYWuaQ",
    "Annihilator",
    "Alice In Hell",
    1989,
    BookmarksFileHelper.ValidHeader.CONTAINER.getTextContent(),
    "2022-05-21T09:53:56Z"
  );

  private static final AlbumBase a2 = new AlbumBase(
    "5av2oGfw34g",
    "A.O.D",
    "Altars of Destruction",
    1988,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:53:53Z"
  );

  private static final AlbumBase a3 = new AlbumBase(
    "zopfZLQibWw",
    "Nuclear Assault",
    "Survive",
    1988,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:57:16Z"
  );

  private static final AlbumBase a4 = new AlbumBase(
    "Zof79HxNpMs",
    "Exodus",
    "Fabulous Disaster",
    1989,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:58:02Z"
  );

  private static final AlbumBase a5 = new AlbumBase(
    "DopHEl-BCGQ",
    "Angel Dust",
    "Into the Dark Past",
    1986,
    BookmarksFileHelper.ValidHeader.CHILD.getTextContent(),
    "2024-03-25T14:56:57Z"
  );

  private static final AlbumBase a6 = new AlbumBase(
    "MV3yQFU3Z6s",
    "Paradox",
    "Product of Imagination",
    1987,
    BookmarksFileHelper.ValidHeader.PARENT.getTextContent(),
    "2024-03-25T14:57:36Z"
  );

  public static final AlbumBase[] VALID_FILE_ROOT_ALBUMBASES = { a1, a2, a3, a4, a5, a6 };
  public static final AlbumBase[] VALID_FILE_CONTAINER_ALBUMBASES = { a1, a2, a3, a4, a5, a6 };
  public static final AlbumBase[] VALID_FILE_PARENT_ALBUMBASES = { a2, a3, a4, a5, a6 };
  public static final AlbumBase[] VALID_FILE_CHILD_ALBUMBASES = { a5 };
  public static final AlbumBase[] VALID_FILE_EMPTY_ALBUMBASES = { };
}
