package com.fs.fsapi.bookmark;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fs.fsapi.bookmark.parser.AlbumParseResult;
import com.fs.fsapi.bookmark.parser.BookmarksFileParserService;
import com.fs.fsapi.bookmark.parser.BookmarksLinkParserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarksService {
  
  private final BookmarksFileParserService fileParser;

  private final BookmarksLinkParserService linkParser;

  public List<AlbumParseResult> getAlbumBases(MultipartFile file, String name) throws IOException {
    InputStream fileStream = file.getInputStream();
    return linkParser.parse(fileParser.parse(fileStream, name));
  }
  
}
