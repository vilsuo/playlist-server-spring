package com.fs.fsapi.entity.artist.repository;

import com.fs.fsapi.entity.MetallumRepository;
import com.fs.fsapi.entity.artist.Artist;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends MetallumRepository<Artist>, CustomArtistRepository {

}
