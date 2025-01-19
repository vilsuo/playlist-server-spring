package com.fs.fsapi.entity.release;

import com.fs.fsapi.metallum.result.search.ReleaseType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReleaseUpdate {

    @NotNull
    private String metallumId;

    private String name;

    private String category;

    private Integer year;

    private ReleaseType releaseType;

    private String trivia;

    private String url;

    private String imageUrl;
}
