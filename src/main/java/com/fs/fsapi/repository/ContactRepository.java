package com.fs.fsapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fs.fsapi.domain.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
  
  Optional<Contact> findById(String id);
}
