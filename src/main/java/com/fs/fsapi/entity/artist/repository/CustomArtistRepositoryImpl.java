package com.fs.fsapi.entity.artist.repository;

import com.fs.fsapi.entity.artist.Artist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

public class CustomArtistRepositoryImpl implements CustomArtistRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Artist> findArtistsWithReleases() {
        return entityManager.createQuery(
                """
                    select a
                    from Artist a
                    join fetch a.releases
                """,
                Artist.class
            ).getResultList();
    }

    @Override
    public Optional<Artist> findArtistWithReleases(Integer id) {
        try {
            final Artist artist = entityManager.createQuery(
                    """
                        select a
                        from Artist a
                        join fetch a.releases
                        where a.id = :artistId
                    """,
                    Artist.class
                )
                .setParameter("artistId", id)
                .getSingleResult();

            return Optional.of(artist);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
