package com.fs.fsapi.metallum.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@NoArgsConstructor
public class AaDataValue {

  private String artistLinkElementString;

  private String titleLinkElementString;

  private String albumType;

  public AaDataValue(List<String> args) {
    this.artistLinkElementString = args.get(0);
    this.titleLinkElementString = args.get(1);
    this.albumType = args.get(2);
  }
}
