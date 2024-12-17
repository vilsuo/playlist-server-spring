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

  @GetMapping("/artist/{artistId}/title/{titleId}/songs")
  public ResponseEntity<List<SongResult>> searchSongs(
    @PathVariable String artistId,
    @PathVariable String titleId
  );

  @GetMapping("/artist/{artistId}/title/{titleId}/songs/{songId}/lyrics")
  public ResponseEntity<LyricsResult> searchSongLyrics(
    @PathVariable String artistId,
    @PathVariable String titleId,
    @PathVariable String songId
  );

  @GetMapping("/artist/{artistId}/logo")
  public ResponseEntity<byte[]> searchArtistLogo(@PathVariable String artistId);

  @GetMapping("/artist/{artistId}/logo/url")
  public ResponseEntity<String> getArtistLogoUrl(@PathVariable String artistId);

  @GetMapping("/artist/{artistId}/title/{titleId}/cover")
  public ResponseEntity<byte[]> searchTitleCover(
    @PathVariable String artistId,
    @PathVariable String titleId
  );

  @GetMapping("/artist/{artistId}/title/{titleId}/cover/url")
  public ResponseEntity<String> getTitleCoverUrl(
    @PathVariable String artistId,
    @PathVariable String titleId
  );
}