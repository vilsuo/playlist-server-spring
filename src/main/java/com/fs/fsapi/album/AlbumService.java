package com.fs.fsapi.album;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
TODO
- check that null values are not updated to album
- throw custom errors
  - not found
  - already exists
  - validation
  - check for null values
- create a common base class (for Album & AlbumCreation) where validation happens?

- handle addDate
  - manually create or create by annotation?
  - save as Date object or String
  - add validation?
*/

@Service
@Validated
@Slf4j
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

  public Album createIfNotExists(@Valid AlbumCreation values) {
    String artist = values.getArtist();
    String title = values.getTitle();

    if (repository.existsByArtistAndTitle(artist, title)) {
      throw new CustomParameterConstraintException(String.format(
        "Artist '%s' already has an album with a title '%s'", artist, title
      ));
    }

    Album album = mapper.albumCreationToAlbum(values);

    // set add date to current date
    var now = LocalDateTime.now();
    album.setAddDate(now.toString());

    return repository.save(album);
  }

  public Album update(@NotNull Integer id, @Valid AlbumCreation values) {
    Album album = findOne(id);

    mapper.updateAlbumFromAlbumCreation(values, album);
    return repository.save(album);
  }

  public void remove(@NotNull Integer id) {
    repository.deleteById(id);
  }
}
