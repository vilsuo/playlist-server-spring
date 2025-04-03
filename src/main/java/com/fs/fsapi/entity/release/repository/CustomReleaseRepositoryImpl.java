package com.fs.fsapi.entity.release.repository;

import com.fs.fsapi.entity.release.Release;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

public class CustomReleaseRepositoryImpl implements CustomReleaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Release> findReleaseWithArtistByIdAndArtistId(Integer id, Integer artistId) {
        try {
            final Release release = entityManager.createQuery(
                    """
                        select r
                        from Release r
                        join fetch r.artist
                        where r.id = :releaseId
                            and r.artist_id = :artistId
                    """,
                    Release.class
                )
                .setParameter("releaseId", id)
                .setParameter("artistId", artistId)
                .getSingleResult();

            return Optional.of(release);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
