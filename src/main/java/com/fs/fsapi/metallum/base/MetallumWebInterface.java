package com.fs.fsapi.metallum.base;

import com.fs.fsapi.metallum.result.ResultImage;

public interface MetallumWebInterface<T, S, R> {

  public T getSearchResponse(String artist, String title);

  public S getSongs(String titleId);

  public R getSongLyrics(String titleId, String songId);
  
  public ResultImage getArtistLogo(String artistId);

  public String getArtistLogoUrl(String artistId);

  public ResultImage getTitleCover(String titleId);

  public String getTitleCoverUrl(String titleId);
}
