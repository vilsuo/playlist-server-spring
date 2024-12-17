package com.fs.fsapi.metallum.base;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

@RequestMapping("/default")
public interface MetallumControllerInterface {

  @GetMapping("/search")
  public ResponseEntity<ArtistTitleSearchResult> search(
    @RequestParam String artist,
    @RequestParam String title
  );

  @GetMapping("/songs/{titleId}")
  public ResponseEntity<List<SongResult>> searchSongs(@PathVariable String titleId);

  @GetMapping("/songs/{titleId}/lyrics/{songId}")
  public ResponseEntity<LyricsResult> searchSongLyrics(
    @PathVariable String titleId,
    @PathVariable String songId
  );

  @GetMapping(value = "/logo/{artistId}")
  public ResponseEntity<byte[]> searchArtistLogo(@PathVariable String artistId);

  @GetMapping("/logo/{artistId}/url")
  public ResponseEntity<String> getArtistLogoUrl(@PathVariable String artistId);

  @GetMapping(value = "/cover/{titleId}")
  public ResponseEntity<byte[]> searchTitleCover(@PathVariable String titleId);

  @GetMapping("/cover/{titleId}/url")
  public ResponseEntity<String> getTitleCoverUrl(@PathVariable String titleId);
}