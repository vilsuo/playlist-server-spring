package com.fs.fsapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import com.fs.fsapi.domain.Album;
import com.fs.fsapi.domain.AlbumCreation;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.repository.AlbumRepository;

import jakarta.validation.ConstraintViolationException;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
@DataJpaTest
@Import(ValidationAutoConfiguration.class)
public class AlbumServiceTest {

  @Mock
  private AlbumRepository repository;

  @InjectMocks
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

  private final Integer id = 1947;
  private final String addDate = "2022-05-14T11:40:01.000Z";

  private Album target;

  @BeforeEach
  void setUpTarget() {
    this.target = new Album(
      id,
      "qJVktESKhKY",
      "Devastation",
      "Idolatry",
      1991,
      "Thrash",
      addDate
    );
  }

  @Test
  public void findOneReturnsExistingAlbumTest(){
    Integer id = target.getId();

    given(repository.findById(id)).willReturn(Optional.of(target));

    var result = service.findOne(id);
    assertEquals(target, result);
  }

  @Test
  public void findOneThrowsIfNotFoundTest(){
    Integer id = target.getId();

    given(repository.findById(id)).willReturn(Optional.empty());

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
