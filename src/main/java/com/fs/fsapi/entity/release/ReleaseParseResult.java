package com.fs.fsapi.entity.release;

import com.fs.fsapi.metallum.result.search.ReleaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReleaseParseResult {

    private String metallumId;

    private String name;

    private Integer year;

    private ReleaseType releaseType;

    private String trivia;

    private String url;

    private String imageUrl;
}
