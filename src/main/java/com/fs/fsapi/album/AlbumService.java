package com.fs.fsapi.album;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.fs.fsapi.DateTimeString;
import com.fs.fsapi.bookmark.parser.AlbumParseResult;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;

import jakarta.transaction.Transactional;
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

  /**
   * Create an album.
   * 
   * @param value  values to use for album creation
   * @return the created album
   * @throws CustomParameterConstraintException if album with given artist and 
   * title already exists
   */
  public Album create(@Valid AlbumCreation value) {
    if (value == null) {
      throw new IllegalArgumentException("Expected creation value to be present");
    }

    final String artist = value.getArtist();
    final String title = value.getTitle();

    boolean checkDublicate = (artist != null) && (title != null);
    if (checkDublicate && repository.existsByArtistAndTitle(artist, title)) {
      throw new CustomParameterConstraintException(
        "Album with artist '" + artist + "' and title '" + title + "' already exists"
      );
    }

    final Album album = mapper.albumCreationToAlbum(value);
    album.setAddDate(DateTimeString.create());

    return repository.save(album);
  }

  /**
   * Create album from parsed album value. Album is created iff such album does not exist 
   * by artist and title. 
   * 
   * @param value  parsed album value
   * @return Optional containing the created album if it was created
   */
  private Optional<Album> create(AlbumParseResult value) {
    if (value == null) {
      throw new IllegalArgumentException("Expected creation value to be present");
    }

    final String artist = value.getArtist();
    final String title = value.getTitle();

    boolean checkForDublicate = (artist != null) && (title != null);
    if (checkForDublicate && repository.existsByArtistAndTitle(artist, title)) {
      return Optional.empty();
    }

    final Album album = mapper.albumParseResultToAlbum(value);
    return Optional.of(repository.save(album));
  }

  @Transactional
  public List<Album> createMany(List<AlbumParseResult> values) {
    return values.stream()
      .map(value -> this.create(value))
      .filter(Optional::isPresent)
      .map(opt -> opt.get())
      .collect(Collectors.toList());
  }

  public Album update(@NotNull Integer id, @Valid AlbumCreation value) {
    if (value == null) {
      throw new IllegalArgumentException("Expected update value to be present");
    }

    final Album album = findOne(id);

    mapper.updateAlbumFromAlbumCreation(value, album);
    return repository.save(album);
  }

  public void remove(@NotNull Integer id) {
    repository.deleteById(id);
  }

}
