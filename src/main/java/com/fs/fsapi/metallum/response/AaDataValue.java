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

  private String releaseLinkElementString;

  private String releaseType;

  public AaDataValue(List<String> args) {
    this.artistLinkElementString = args.get(0);
    this.releaseLinkElementString = args.get(1);
    this.releaseType = args.get(2);
  }
}
