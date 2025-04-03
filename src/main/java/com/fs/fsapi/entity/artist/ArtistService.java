package com.fs.fsapi.entity.artist;

import com.fs.fsapi.entity.artist.repository.ArtistRepository;
import com.fs.fsapi.entity.release.Release;
import com.fs.fsapi.entity.release.ReleaseParseResult;
import com.fs.fsapi.entity.release.ReleaseService;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository repository;

    private final ReleaseService service;

    private final ArtistMapper mapper;


    public List<Artist> findAll() {
        return repository.findArtistsWithReleases();
    }

    private Artist findOne(Integer id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new CustomDataNotFoundException("Artist was not found"));
    }

    public Optional<Artist> findByMetallumId(String metallumId) {
        return repository.findByMetallumId(metallumId);
    }

    private Artist prepare(ArtistParseResult values) {
        final Artist artist = mapper.artistParseResultToArtist(values);

        final String metallumId = artist.getMetallumId();
        if (repository.findByMetallumId(metallumId).isPresent()) {
            throw new CustomParameterConstraintException(
                "Artist with metallumId '" + metallumId + "' already exists"
            );
        }

        return artist;
    }

    // new Artist & Release
    public Artist create(
        ArtistParseResult artistParseResult,
        ReleaseParseResult releaseParseResult
    ) {
        final Artist artist = prepare(artistParseResult);
        final Release release = service.prepare(releaseParseResult);
        artist.addRelease(release);

        // cascade persist
        return repository.save(artist);
    }

    // new Release to an existing Artist
    public Artist addRelease(String artistMetallumId, ReleaseParseResult parsedRelease) {
        final Artist artist = findByMetallumId(artistMetallumId)
            .orElseThrow(() -> new IllegalStateException(
                "Expected Artist with metallum id '" + artistMetallumId + "' to exist"
            ));

        final Release release = service.prepare(parsedRelease);
        artist.addRelease(release);

        // artist is managed, no need to save...?
        return artist;
    }

    /**
     * Delete Release by id. Deletes also the Artist attached to the Release if there 
     * are no other Releases
     *
     * @param releaseId  the id of the Release to be deleted
     * @return  true if Artist was deleted, false otherwise
     */
    public boolean delete(Integer releaseId) {
        final Release release = service.findOne(releaseId);
        final Artist artist = release.getArtist();

        artist.removeRelease(release);
        if (!artist.hasReleases()) {
            // cascade delete
            log.info("Removing artist " + artist);

            repository.delete(artist);
            return true;
        } else {
            // needed? does orphan removal work?
            repository.save(artist);
        }

        return false;
    }
}
