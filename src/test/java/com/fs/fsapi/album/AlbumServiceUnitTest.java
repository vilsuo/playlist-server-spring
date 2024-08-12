package com.fs.fsapi.album;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fs.fsapi.exceptions.CustomDataNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceUnitTest {
  
  @Mock
  private AlbumRepository repository;

  @Mock
  private AlbumMapper mapper;

  @InjectMocks
  private AlbumService service;

  private final Album target = new Album(
    123,
    "qJVktESKhKY",
    "Devastation",
    "Idolatry",
    1991,
    "Thrash",
    "2022-05-14T11:40:01.000Z"
  );

  @Test
  public void findOneReturnsExistingAlbumTest(){
    Integer id = target.getId();
    
    when(repository.findById(id)).thenReturn(Optional.of(target));

    var result = service.findOne(id);
    assertEquals(target, result);

    verify(repository, times(1)).findById(id);
  }

  @Test
  public void findOneThrowsIfNotFoundTest(){
    Integer id = 123;

    when(repository.findById(id)).thenReturn(Optional.empty());

    Exception e = assertThrows(
      CustomDataNotFoundException.class,
      () -> service.findOne(id)
    );

    assertEquals(e.getMessage(), "Album was not found");

    verify(repository, times(1)).findById(id);
  }

  @Test
  public void findOneThrowsWithNullIdTest(){
    Integer id = null;

    when(repository.findById(id)).thenThrow(NullPointerException.class);

    var e = assertThrows(
      NullPointerException.class,
      () -> service.findOne(id)
    );

    verify(repository, times(1)).findById(id);
  }
}
