package com.fs.fsapi.metallum.response;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
public class AaDataValue {

  private String artistLinkElementOuterHtml;

  private String titleLinkElementOuterHtml;

  private String releaseType;

  public AaDataValue(List<String> args) {
    this.artistLinkElementOuterHtml = args.get(0);
    this.titleLinkElementOuterHtml = args.get(1);
    this.releaseType = args.get(2);
  }
}
