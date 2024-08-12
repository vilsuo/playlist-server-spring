package com.fs.fsapi.album;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fs.fsapi.exceptions.CustomDataNotFoundException;

import jakarta.validation.ConstraintViolationException;

@Testcontainers
@SpringBootTest
public class AlbumServiceIntegrationTest {

  @Autowired
  private AlbumRepository repository;

  @Autowired
  private AlbumService service;

  private final AlbumCreation albumValues = new AlbumCreation(
    "JMAbKMSuVfI",
    "Massacra",
    "Signs of the Decline",
    1992,
    "Death"
  );

  private final AlbumCreation newAlbumValues = new AlbumCreation(
    "qJVktESKhKY",
    "Devastation",
    "Idolatry",
    1991,
    "Thrash"
  );

  @Container
  @ServiceConnection
  public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

  @BeforeAll
  public static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  public static void afterAll() {
    postgres.stop();
  }

  @BeforeEach
  public void setUpTarget() {
    repository.deleteAll();
  }

  @Test
  public void findOneReturnsExistingAlbumTest(){
    Album target = service.createIfNotExists(newAlbumValues);
    Integer id = target.getId();
    
    var result = service.findOne(id);
    assertEquals(target, result);
  }

  @Test
  public void findOneThrowsIfNotFoundTest(){
    Integer id = 123;

    Exception e = assertThrows(
      CustomDataNotFoundException.class,
      () -> service.findOne(id)
    );

    assertEquals(e.getMessage(), "Album was not found");
  }

  @Test
  public void findOneThrowsWithNullIdTest(){
    Integer id = null;

    var e = assertThrows(
      ConstraintViolationException.class,
      () -> service.findOne(id)
    );
  }

  /*
  @Test
  public void creatingThrowsExceptionWithInvalidValue(){
    AlbumCreation invalid = null;


    assertThrows(
      ConstraintViolationException.class,
      () -> service.createIfNotExists(invalid)
    );
  }
  */

}
