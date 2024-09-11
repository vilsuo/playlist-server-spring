package com.fs.fsapi.album;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {

  // https://docs.spring.io/spring-data/commons/reference/repositories/query-methods-details.html
  // https://docs.spring.io/spring-data/commons/reference/repositories/query-keywords-reference.html#appendix.query.method.subject
  boolean existsByArtistAndTitle(String artist, String title);
}
