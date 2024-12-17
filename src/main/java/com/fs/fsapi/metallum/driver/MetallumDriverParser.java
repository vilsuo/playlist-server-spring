package com.fs.fsapi.metallum.driver;

import java.util.List;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fs.fsapi.exceptions.CustomDataNotFoundException;
import com.fs.fsapi.metallum.base.MetallumParser;
import com.fs.fsapi.metallum.base.MetallumParserInterface;
import com.fs.fsapi.metallum.result.lyrics.InstrumentalLyricsResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.lyrics.NotAvailableLyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

@Service
public class MetallumDriverParser extends MetallumParser
  implements MetallumParserInterface<String, String, String>
{

  @Override
  public List<ArtistTitleSearchResult> parseSearchResults(String htmlTbody) {
    final List<Elements> rowTds = super.readTableBody(htmlTbody, super::isTableRowDataElement);

    // check if any results
    if (rowTds.size() == 1) {
      final Elements tds = rowTds.get(0);
      if (tds.size() == 1) {
        throw new CustomDataNotFoundException("No matches found");
      }
    }

    return super.parseSearchTable(
      rowTds,
      (tds) -> new String[] {
        tds.get(0).html(),
        tds.get(1).html(),
        tds.get(2).ownText()
      }
    );
  }

  @Override
  public LyricsResult parseLyrics(String text) {
    final String value = text.trim();

    if (value.isEmpty()) {
      return new NotAvailableLyricsResult();

    } else if (value.equals("instrumental")) {
      return new InstrumentalLyricsResult();
    }

    final String rowSeparator = "\n";
    return super.parseLyricsAvailableResult(value, rowSeparator);
  }
}
