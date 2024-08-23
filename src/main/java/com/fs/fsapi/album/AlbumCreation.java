package com.fs.fsapi.album;

import org.hibernate.validator.constraints.Range;

import com.fs.fsapi.validation.VideoId;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
public class AlbumCreation {

  @NotNull(message = "Video id is required")
  @VideoId
  private String videoId;

  @NotBlank(message = "Artist name is required")
  private String artist;

  @NotBlank(message = "Title is required")
  private String title;

  @NotNull(message = "Publish year is required")
  @Range(
    min = 1000,
    max = 9999,
    message = "Album must be published between {min} and {max}"
  )
  private Integer published;

  @NotBlank(message = "Category is required")
  private String category;
}
