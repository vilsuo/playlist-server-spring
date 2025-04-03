package com.fs.fsapi.entity.release;

import com.fs.fsapi.entity.release.repository.ReleaseRepository;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomParameterConstraintException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReleaseService {

    final private ReleaseRepository repository;

    final private ReleaseMapper mapper;


    public Release findOne(Integer id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new CustomDataNotFoundException("Release was not found"));
    }

    public Release prepare(ReleaseParseResult values) {
        final Release release = mapper.releaseParseResultToRelease(values);

        final String metallumId = release.getMetallumId();
        if (repository.findByMetallumId(metallumId).isPresent()) {
            throw new CustomParameterConstraintException(
                "Release with metallumId '" + metallumId + "' already exists"
            );
        }

        release.setCreatedAt(createTimestamp());
        return release;
    }

    private long createTimestamp() {
        return Instant.now().getEpochSecond();
    }
}
