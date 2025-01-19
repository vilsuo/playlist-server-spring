package com.fs.fsapi.entity.artist;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArtistParseResult {

    @NotNull
    private String metallumId;

    private String name;

    private String country;

    private String url;

    private String imageUrl;
}
