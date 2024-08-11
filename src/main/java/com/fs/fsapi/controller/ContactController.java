package com.fs.fsapi.controller;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fs.fsapi.constant.Constant;
import com.fs.fsapi.domain.Contact;
import com.fs.fsapi.service.ContactService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {
  
  private final ContactService contactService;

  @PostMapping
  public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
    //return ResponseEntity.ok().body(contactService.createContact(contact));
    return ResponseEntity
      .created(URI.create("/contacts/userID"))
      .body(contactService.createContact(contact));
  }

  @GetMapping
  public ResponseEntity<Page<Contact>> getContacts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity.ok().body(contactService.getAllContacts(page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Contact> getContact(@PathVariable String id) {
    return ResponseEntity.ok().body(contactService.getContact(id));
  }

  @PutMapping("/photo")
  public ResponseEntity<String> uploadPhoto(
    @RequestParam String id,
    @RequestParam MultipartFile file
  ) {
    return ResponseEntity.ok().body(contactService.uploadPhoto(id, file));
  }

  @GetMapping(
    path = "/image/{filename}",
    produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE }
  )
  public byte[] getPhoto(@PathVariable String filename) throws IOException {
    return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY, filename));
  }
}