package com.fs.fsapi.album;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.fs.fsapi.exceptions.CustomDataNotFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class AlbumService {

  private final AlbumRepository repository;

  private final AlbumMapper mapper;

  public List<Album> findAll() {
    return repository.findAll();
  }

  public Album findOne(@NotNull Integer id) {
    return repository
      .findById(id)
      .orElseThrow(() ->
        new CustomDataNotFoundException("Album was not found")
      );
  }

  public Album create(@Valid AlbumCreation values) {
    if (values == null) {
      throw new IllegalArgumentException("Expected creation value to be present");
    }

    Album album = mapper.albumCreationToAlbum(values);
    album.setAddDate(createAddDate());

    return repository.save(album);
  }

  public Album update(@NotNull Integer id, @Valid AlbumCreation values) {
    if (values == null) {
      throw new IllegalArgumentException("Expected update value to be present");
    }

    Album album = findOne(id);

    mapper.updateAlbumFromAlbumCreation(values, album);
    return repository.save(album);
  }

  public void remove(@NotNull Integer id) {
    repository.deleteById(id);
  }

  private String createAddDate() {
    return Instant.now().toString();
  }

}
