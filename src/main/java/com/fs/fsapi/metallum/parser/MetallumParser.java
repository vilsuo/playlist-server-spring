package com.fs.fsapi.metallum.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fs.fsapi.bookmark.parser.LinkElement;
import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.exceptions.CustomMetallumException;
import com.fs.fsapi.metallum.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.response.AaDataValue;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MetallumParser {

  public ArtistTitleSearchResult getSearchResult(
    ArtistTitleSearchResponse response, String artist, String title
  ) {
    if (!response.getError().isBlank()) {
      log.info(
        "Error " + response.getError() + " in album '"
        + title + "' by '" + artist + "' search result"
      );

      throw new CustomMetallumException(response.getError());
    }

    switch (response.getTotalRecords()) {
      case 0: {
        throw new CustomDataNotFoundException(
          "Album '" + title + "' by '" + artist + "' was not found"
        );
      }
      case 1: {
        // return the only result
        AaDataValue data = response.getFirstDataValue();
        return createResponse(data);
      }
      default: {
        log.info("Found multiple results for album '" + title + "' by '" + artist);

        // return the first result... should do narrowing?
        AaDataValue data = response.getFirstDataValue();
        return createResponse(data);
      }
    }
  }

  private ArtistTitleSearchResult createResponse(AaDataValue data) {
    return new ArtistTitleSearchResult(
      createLinkElement(data.getArtistLinkElementString()),
      createLinkElement(data.getTitleLinkElementString()),
      data.getAlbumType()
    );
  }

  private LinkElement createLinkElement(String html) {
    return new LinkElement(Jsoup.parse(html).selectFirst("a"));
  }

  public List<SongResult> getSongs(String html) {
    Document doc = Jsoup.parse(html);

    Element tbody = doc.select(".table_lyrics > tbody").first();
    List<SongResult> songs = new ArrayList<>();

    tbody.children().stream()
      .forEach(tr -> {
        if (tr.hasClass("even") || tr.hasClass("odd")) {
          Elements tds = tr.children();

          final String songTitle = tds.get(1).ownText();
          final String songDuration = tds.get(2).ownText();

          songs.add(new SongResult(songTitle, songDuration));
        }
      });

    return songs;
  }
}
