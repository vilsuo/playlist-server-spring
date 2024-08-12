package com.fs.fsapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fs.fsapi.domain.Album;
import com.fs.fsapi.domain.AlbumCreation;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.repository.AlbumRepository;

import jakarta.validation.ConstraintViolationException;

@Testcontainers
@SpringBootTest
public class AlbumServiceTest {

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
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  // register the database connection properties dynamically obtained 
  // from the Postgres container using Spring Boot’s DynamicPropertyRegistry.
  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @BeforeEach
  void setUpTarget() {
    repository.deleteAll();
  }

  @Test
  public void findOneReturnsExistingAlbumTest(){
    Album target = service.createIfNotExists(newAlbumValues);
    Integer id = target.getId();
    
    //given(repository.findById(id)).willReturn(Optional.of(target));

    var result = service.findOne(id);
    assertEquals(target, result);

    //verify(repository, times(1)).findById(id);
  }

  @Test
  public void findOneThrowsIfNotFoundTest(){
    Integer id = 123;

    //given(repository.findById(id)).willReturn(Optional.empty());

    Exception e = assertThrows(
      CustomDataNotFoundException.class,
      () -> service.findOne(id)
    );

    assertEquals(e.getMessage(), "Album was not found");

    //verify(repository, times(1)).findById(id);
  }

  @Test
  public void findOneThrowsWithNullIdTest(){
    Integer id = null;

    //given(repository.findById(id)).willThrow(Exception.class);

    var e = assertThrows(
      ConstraintViolationException.class,
      () -> service.findOne(id)
    );

    //verify(repository, times(0)).findById(id);
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
