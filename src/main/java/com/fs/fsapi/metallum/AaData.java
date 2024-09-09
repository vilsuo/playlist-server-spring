package com.fs.fsapi.metallum;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@NoArgsConstructor
public class AaData {

  private String artistName;

  private String albumTitle;

  private String albumType;

  public AaData(List<String> args) {
    this.artistName = args.get(0);
    this.albumTitle = args.get(1);
    this.albumType = args.get(2);
  }
}
