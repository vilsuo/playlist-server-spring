package com.fs.fsapi.entity.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {

    Optional<Artist> findByMetallumId(String metallumId);

    //@Query("SELECT COUNT(DISTINCT r.id) FROM Artist a LEFT JOIN Release r ON a.id = r.artist_id WHERE a.id = ?1")
    //long countReleases(Integer id);
}
