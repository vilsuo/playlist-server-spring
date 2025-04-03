package com.fs.fsapi.entity;

import com.fs.fsapi.entity.artist.Artist;
import com.fs.fsapi.entity.artist.ArtistParseResult;
import com.fs.fsapi.entity.artist.ArtistService;
import com.fs.fsapi.entity.release.ReleaseParseResult;
import com.fs.fsapi.metallum.client.MetallumClientService;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/*
TODO
    - add route for adding by metallum release url

 */

@Slf4j
@RestController
@RequestMapping("/artist")
@RequiredArgsConstructor
public class TestController {

    private final ArtistService service;

    private final MetallumClientService client;

    @GetMapping
    public ResponseEntity<List<Artist>> getAll() {
        return ResponseEntity
            .ok()
            .body(service.findAll());
    }

    @PostMapping
    public ResponseEntity<Artist> create(@RequestBody MetallumSearchBody body) {

        final ArtistTitleSearchResult result = client.searchByArtistAndTitle(
            body.getArtistName(),
            body.getReleaseName()
        );

        final var a = new ArtistParseResult(
            result.getArtistId(),
            result.getArtist(),
            "PLACEHOLDER COUNTRY",
            result.getArtistHref(),
            null
        );

        final var b = new ReleaseParseResult(
            result.getTitleId(),
            result.getTitle(),
            2025,
            result.getReleaseType(),
            "PLACEHOLDER TRIVIA",
            result.getTitleHref(),
            null
        );

        log.info(a.toString());
        log.info(b.toString());

        final Artist artist = service.create(
            a, b
        );

        final URI uri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(artist.getId()).toUri();

        return ResponseEntity
            .created(uri)
            .body(artist);
    }
}
