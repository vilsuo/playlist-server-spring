package com.fs.fsapi.entity.release.repository;

import com.fs.fsapi.entity.MetallumRepository;
import com.fs.fsapi.entity.release.Release;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseRepository extends MetallumRepository<Release>, CustomReleaseRepository {

}
