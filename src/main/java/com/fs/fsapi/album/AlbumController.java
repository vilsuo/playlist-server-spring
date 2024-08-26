package com.fs.fsapi.album;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

  private final AlbumService service;

  @GetMapping
  public ResponseEntity<List<Album>> getAll() {
    return ResponseEntity
      .ok()
      .body(service.findAll());
  }
  
  @PostMapping
  public ResponseEntity<Album> postAlbum(@Valid @RequestBody AlbumCreation values) {
    Album album = service.create(values);

    URI uri = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(album.getId()).toUri();
    
    return ResponseEntity
      .created(uri)
      .body(album);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Album> getAlbum(@PathVariable Integer id) {
    return ResponseEntity
      .ok()
      .body(service.findOne(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Album> putAlbum(
    @PathVariable Integer id,
    @Valid @RequestBody AlbumCreation values
  ) {
    return ResponseEntity
      .ok()
      .body(service.update(id, values));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAlbum(@PathVariable Integer id) {
    service.remove(id);

    return ResponseEntity
      .noContent()
      .build();
  }

  @GetMapping("/download")
  public ResponseEntity<List<Album>> downloadAlbums() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(
      HttpHeaders.CONTENT_DISPOSITION,
      String.format("attachment; filename=\"%s\"", createFilename())
    );

    return ResponseEntity
      .ok()
      .headers(headers)
      .body(service.findAll());
  }

  private String createFilename() {
    return String.format("albums-%d.json", Instant.now().toEpochMilli());
  }
  
}
