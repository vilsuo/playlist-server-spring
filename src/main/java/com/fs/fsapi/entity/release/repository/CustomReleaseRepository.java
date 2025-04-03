package com.fs.fsapi.entity.release.repository;

import com.fs.fsapi.entity.release.Release;

import java.util.Optional;

public interface CustomReleaseRepository {

    Optional<Release> findReleaseWithArtistByIdAndArtistId(Integer id, Integer artistId);
}
