package com.fs.fsapi.metallum.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@EqualsAndHashCode
@AllArgsConstructor
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
