package com.fs.fsapi.entity;

import com.fs.fsapi.entity.artist.Artist;
import com.fs.fsapi.entity.artist.ArtistParseResult;
import com.fs.fsapi.entity.artist.ArtistService;
import com.fs.fsapi.entity.release.Release;
import com.fs.fsapi.entity.release.ReleaseParseResult;
import com.fs.fsapi.entity.release.ReleaseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/*
TODO:
    - make methods transactional
    - implement required methods:
        - expect artist & release is found from metallum
*/

@Validated
@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistReleaseService {

    private final ArtistService artistService;

    private final ReleaseService releaseService;

    // populates Releases
    @NonNull
    public List<Artist> findAll() {
        return this.artistService.findAll();
    }

    // new Artist & Release
    @NonNull
    public Artist create(
        @Valid @NotNull ArtistParseResult artistParseResult,
        @Valid @NotNull ReleaseParseResult releaseParseResult
    ) {
        if (artistParseResult.getMetallumId() == null || releaseParseResult.getMetallumId() == null) {
            return  null;
        }

        final Artist artist = artistService.create(artistParseResult);
        final Release release = releaseService.create(releaseParseResult, artist);

        artist.addRelease(release);
        return artistService.save(artist);
    }

    // new Release
    public Artist addRelease(Artist artist, ReleaseParseResult parsedRelease) {

    }

    @NonNull
    public Artist update(@NotNull  Integer artistId, @NotNull Integer releaseId) {

    }

    public void delete(@NotNull Integer artistId, @NotNull Integer releaseId) {
        final Artist artist = this.artistService.find(artistId);
        final List<Release> releases = artist.getReleases();

        // 1. remove release
        releases.remove(releaseService.find(releaseId));

        // 2. if artist does not have any releases, remove artist
        if (releases.isEmpty()) {

        } else {

        }
    }
}
