package com.fs.fsapi.album;

import org.hibernate.validator.constraints.Range;

import com.fs.fsapi.validation.VideoId;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
public class Album {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

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

  @NotBlank
  private String addDate;
}
