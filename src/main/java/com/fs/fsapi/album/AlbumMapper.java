package com.fs.fsapi.album;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.fs.fsapi.bookmark.parser.AlbumParseResult;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlbumMapper {
  
  public Album albumCreationToAlbum(AlbumCreation source);

  public Album albumParseResultToAlbum(AlbumParseResult result);

  public void updateAlbumFromAlbumCreation(AlbumCreation source, @MappingTarget Album target);
}
