package com.fs.fsapi.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MetallumSearchBody {

    @NotBlank(message = "Artist name is required")
    private String artistName;

    @NotBlank(message = "Release name is required")
    private String releaseName;
}
