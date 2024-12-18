package com.fs.fsapi.metallum.result;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.result.search.ReleaseType;

@Service
public class ResultRanker {

  /**
   * Find a single best search result
   * 
   * @param results search results
   * 
   * @param artist  artist search string
   * @param title  release title search string
   * @return the best search result
   */
  public ArtistTitleSearchResult getBestSearchResult(
    List<ArtistTitleSearchResult> results,
    String artist,
    String title
  ) {
    final int RESULT_THRESHOLD = 0; // stop only on exact match
    final int RESULT_LIMIT = 200; // metallum single page limit is 200 results

    return argMin(results, result -> {
      // case-insensitive
      final int artistDist = levDist(artist.toUpperCase(), result.getArtist().toUpperCase());
      final int titleDist = levDist(title.toUpperCase(), result.getTitle().toUpperCase());

      final int totalDist = artistDist + titleDist;

      // if not exact match, punish by result release type
      return (totalDist != 0)
        ? totalDist + punishByResultType(result)
        : totalDist;
      },
      RESULT_THRESHOLD,
      RESULT_LIMIT
    );
  }

  // SPECIFIC

  private int punishByResultType(ArtistTitleSearchResult result) {
    final ReleaseType releaseType = result.getReleaseType();
      return switch (releaseType) {
          case FULL_LENGTH -> 0;
          case DEMO, EP -> 1;
          case COMPILATION, SINGLE, SPLIT -> 2;
          case BOXED_SET, COLLABORATION -> 3;
          case LIVE_ALBUM -> 4;
          case VIDEO, SPLIT_VIDEO -> 5;
          default -> throw new IllegalStateException(
            "Unexpected Release type '" + releaseType + "'"
          );
      };
  }

  // GENERAL
  
  /**
   * Get the value that produces the smallest value based on the value function
   * 
   * @param <T>  the type of candidate
   * @param iterable  the candidates
   * @param valueFunc  function that assigns an integer value to each
   *                  candidate
   * @param threshold  a stop value for value function
   * @param maxIterations  the maximum number of results to be checked
   * @return the best candidate that minimizes the value function
   */
  private <T> T argMin(Iterable<T> iterable, Function<T, Integer> valueFunc, int threshold, int maxIterations) {
    T best = null;
    int minValue = Integer.MAX_VALUE;

    int currIdx = 0;
    var iter = iterable.iterator();
    while (iter.hasNext() && currIdx < maxIterations) {
      T elem = iter.next();

      final int value = valueFunc.apply(elem);
      if (value <= threshold) { return elem; }
      else if (value < minValue) {
        best = elem;
        minValue = value;
      }
      ++currIdx;
    }

    return best;
  }

  /**
   * Calculates the Levenshtein distance between two strings.
   * 
   * @param a  first string
   * @param b  second string
   * @return the minimum number of single-character edits (insertions, deletions 
   *         or substitutions) required to change one word into the other
   */
  private int levDist(String a, String b) {
    final int lenA = a.length();
    final int lenB = b.length();

    if (lenB == 0) { return lenA; }
    else if (lenA == 0) { return lenB; }
    else {
      final String tailA = a.substring(1);
      final String tailB = b.substring(1);
      final char headA = a.charAt(0);
      final char headB = b.charAt(0);

      if (headA == headB) {
        return levDist(tailA, tailB);
      } else {
        return 1 + Math.min(Math.min(
          levDist(tailA, b),
          levDist(a, tailB)),
          levDist(tailA, tailB)
        );
      }
    }
  }
}
