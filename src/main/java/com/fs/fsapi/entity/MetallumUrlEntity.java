package com.fs.fsapi.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@MappedSuperclass
public abstract class MetallumUrlEntity extends MetallumEntity {

    @NotNull(message = "URL is required")
    @URL(message = "URL must be valid")
    private String url;

    @URL(message = "Image URL must be valid")
    private String imageUrl;
}
