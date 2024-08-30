package com.fs.fsapi.bookmark;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {
  
  private final JsoupService jsoup;

  private final LinkParserService parser;

  public List<AlbumBase> getAlbumBases(MultipartFile file, String name) throws IOException {
    return parser.createAlbumBases(jsoup.createFolderLinks(file, name));
  }
  
}
