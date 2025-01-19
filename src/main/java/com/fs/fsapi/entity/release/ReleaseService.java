package com.fs.fsapi.entity.release;

import com.fs.fsapi.entity.artist.Artist;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;

@Validated
@Service
@RequiredArgsConstructor
public class ReleaseService {

    final private ReleaseRepository repository;

    final private ReleaseMapper mapper;

    @NonNull
    public List<Release> findAll() {
        return repository.findAll();
    }

    @NonNull
    public Release find(@NotNull Integer id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new CustomDataNotFoundException("Release was not found"));
    }

    @NonNull
    public Release create(@Valid @NotNull ReleaseParseResult values, @NotNull Artist artist) {
        final Release release = mapper.releaseParseResultToRelease(values);

        final String metallumId = release.getMetallumId();
        final Integer artistId = artist.getId();
        if (repository.findByArtistIdAndMetallumId(artistId, metallumId).isPresent()) {
            throw new CustomParameterConstraintException(
                "Release with artistId '" + artistId + "' and metallumId '" + metallumId + "' already exists"
            );
        }

        release.setArtist(artist);
        release.setCreatedAt(this.createTimestamp());

        return repository.save(release);
    }

    @NonNull
    public Release update(@NotNull Integer id, @Valid @NotNull ReleaseUpdate values) {
        final Release release = repository
            .findById(id)
            .orElseThrow(() -> new CustomDataNotFoundException("Release was not found"));

        // can not update to an existing release
        final String newMetallumId = values.getMetallumId();
        final Artist artist = release.getArtist();
        if (!artist.getMetallumId().equals(newMetallumId)) {
            final Integer artistId = artist.getId();
            repository
                .findByArtistIdAndMetallumId(artistId, newMetallumId)
                .ifPresent(found -> {
                    throw new CustomParameterConstraintException(
                        "Release with artistId '" + artistId + "' and metallumId '" + newMetallumId + "' already exists"
                    );
                });
        }

        mapper.updateReleaseFromReleaseUpdate(values, release);
        return repository.save(release);
    }

    public void delete(@NotNull Integer id) {
        repository.deleteById(id);
    }

    private long createTimestamp() {
        return Instant.now().getEpochSecond();
    }
}
