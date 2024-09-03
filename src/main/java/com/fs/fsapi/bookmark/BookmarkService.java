package com.fs.fsapi.bookmark;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fs.fsapi.bookmark.parser.AlbumBase;
import com.fs.fsapi.bookmark.parser.HtmlParserService;
import com.fs.fsapi.bookmark.parser.LinkParserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {
  
  private final HtmlParserService htmlService;

  private final LinkParserService linkService;

  public List<AlbumBase> getAlbumBases(MultipartFile file, String name) throws IOException {
    InputStream fileStream = file.getInputStream();
    return linkService.createAlbumBases(htmlService.createFolderLinks(fileStream, name));
  }
  
}
