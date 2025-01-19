package com.fs.fsapi.entity.release;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Integer> {

    Optional<Release> findByArtistIdAndMetallumId(Integer artistId, String metallumId);

}
