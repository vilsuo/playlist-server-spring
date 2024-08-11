package com.fs.fsapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fs.fsapi.domain.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {
  
  Optional<Album> findById(Integer id);

  void deleteById(Integer id);

  // https://docs.spring.io/spring-data/commons/reference/repositories/query-methods-details.html
  // https://docs.spring.io/spring-data/commons/reference/repositories/query-keywords-reference.html#appendix.query.method.subject
  boolean existsByArtistAndTitle(String artist, String title);
}
