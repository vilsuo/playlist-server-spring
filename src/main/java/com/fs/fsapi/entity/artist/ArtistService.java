package com.fs.fsapi.entity.artist;

import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository repository;

    private final ArtistMapper mapper;

    @NonNull
    public List<Artist> findAll() {
        return repository.findAll();
    }

    @NonNull
    public Artist find(@NotNull Integer id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new CustomDataNotFoundException("Artist was not found"));
    }

    @NonNull
    public Artist create(@Valid @NotNull ArtistParseResult values) {
        final Artist artist = mapper.artistParseResultToArtist(values);

        final String metallumId = artist.getMetallumId();
        if (repository.findByMetallumId(metallumId).isPresent()) {
            throw new CustomParameterConstraintException(
                "Artist with metallumId '" + metallumId + "' already exists"
            );
        }

        return repository.save(artist);
    }

    @NonNull
    public Artist update(@NotNull Integer id, @Valid @NotNull ArtistUpdate values) {
        final Artist artist = repository
            .findById(id)
            .orElseThrow(() -> new CustomDataNotFoundException("Artist was not found"));

        // can not update to an existing metallumId
        final String newMetallumId = values.getMetallumId();
        if (!artist.getMetallumId().equals(newMetallumId)) {
            repository
                .findByMetallumId(newMetallumId)
                .ifPresent(found -> {
                    throw new CustomParameterConstraintException(
                        "Artist with metallumId '" + newMetallumId + "' already exists"
                    );
                });
        }

        mapper.updateArtistFromArtistUpdate(values, artist);
        return repository.save(artist);
    }

    // deletes also all Releases
    public void delete(@NotNull Integer id) {
        repository.deleteById(id);
    }
}
