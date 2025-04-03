package com.fs.fsapi.entity.artist.repository;

import com.fs.fsapi.entity.artist.Artist;

import java.util.List;
import java.util.Optional;

public interface CustomArtistRepository {

    List<Artist> findArtistsWithReleases();

    Optional<Artist> findArtistWithReleases(Integer id);
}
