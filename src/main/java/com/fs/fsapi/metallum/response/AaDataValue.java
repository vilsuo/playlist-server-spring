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

  private String artistLinkElementOuterHtml;

  private String releaseLinkElementOuterHtml;

  private String releaseType;

  public AaDataValue(List<String> args) {
    this.artistLinkElementOuterHtml = args.get(0);
    this.releaseLinkElementOuterHtml = args.get(1);
    this.releaseType = args.get(2);
  }
}
