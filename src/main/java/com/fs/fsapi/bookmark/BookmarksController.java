package com.fs.fsapi.bookmark;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fs.fsapi.album.Album;
import com.fs.fsapi.album.AlbumService;
import com.fs.fsapi.bookmark.parser.AlbumResult;
import com.fs.fsapi.exceptions.CustomInvalidMediaTypeException;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarksController {

  private final AlbumService albumService;

  private final BookmarksService bookmarkService;
  
  private final Tika tika = new Tika();
  private final String SUPPORTED_MEDIA_TYPE = "text";

  @PostMapping
  public ResponseEntity<List<Album>> uploadBookmarks(
    @RequestParam MultipartFile file,
    @NotEmpty @RequestParam String name
  ) throws IOException {
    
    MediaType mediaType = detectMediaType(file);
    if (!mediaType.getType().equals(SUPPORTED_MEDIA_TYPE)) {
      throw new CustomInvalidMediaTypeException(
        mediaType,
        SUPPORTED_MEDIA_TYPE + "/*"
      );
    }

    List<AlbumResult> values = bookmarkService.getAlbumBases(file, name);

    return ResponseEntity
      .ok()
      .body(albumService.createMany(values));
  }

  private MediaType detectMediaType(MultipartFile file) {
    final String filename = file.getOriginalFilename();

    try {
      InputStream inputStream = file.getInputStream();
      
      Metadata metadata = new Metadata();
      metadata.set(
        TikaCoreProperties.RESOURCE_NAME_KEY,
        filename
      );
      
      MediaType mediaType = tika.getDetector()
        .detect(TikaInputStream.get(inputStream), metadata);

      log.info(
        "Detected mediatype '" + mediaType.getBaseType()
        + "' for file '" + filename + "'"
      );

      return mediaType;

    } catch (IOException ex) {
      log.info("Error detecting mediatype for file '" + filename + "'", ex);

      throw new RuntimeException(
        "Can not read file '" + filename + "'"
      );
    }
  }
}
