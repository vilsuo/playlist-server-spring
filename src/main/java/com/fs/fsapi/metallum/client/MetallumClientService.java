package com.fs.fsapi.metallum.client;

import org.springframework.stereotype.Service;

import com.fs.fsapi.metallum.base.MetallumService;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;
import com.fs.fsapi.metallum.result.ResultRanker;

@Service
public class MetallumClientService
  extends MetallumService<ArtistTitleSearchResponse, String, String>
{

  public MetallumClientService(
    MetallumClient client,
    MetallumClientParser parser,
    ArtistTitleSearchCache cache,
    ResultRanker ranker
  ) {
    super(client, parser, cache, ranker);
  }
}
