package com.fs.fsapi;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.fs.fsapi.domain.Album;
import com.fs.fsapi.domain.AlbumCreation;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
  
  Album albumCreationToAlbum(AlbumCreation source);

  void updateAlbumFromAlbumCreation(AlbumCreation source, @MappingTarget Album target);
}
