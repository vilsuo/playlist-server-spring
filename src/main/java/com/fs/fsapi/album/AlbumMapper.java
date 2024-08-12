package com.fs.fsapi.album;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AlbumMapper {
  
  public Album albumCreationToAlbum(AlbumCreation source);

  public void updateAlbumFromAlbumCreation(AlbumCreation source, @MappingTarget Album target);
}
