package com.fs.fsapi.entity.artist;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArtistParseResult {

    private String metallumId;

    private String name;

    private String country;

    private String url;

    private String imageUrl;
}
