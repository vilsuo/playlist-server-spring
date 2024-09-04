package com.fs.fsapi.album;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.fs.fsapi.bookmark.parser.AlbumBase;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AlbumMapper {
  
  public Album albumCreationToAlbum(AlbumCreation source);

  public Album albumBaseToAlbum(AlbumBase base);

  public void updateAlbumFromAlbumCreation(AlbumCreation source, @MappingTarget Album target);
}
