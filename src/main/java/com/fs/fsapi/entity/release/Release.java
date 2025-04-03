package com.fs.fsapi.entity.release;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fs.fsapi.entity.MetallumUrlEntity;
import com.fs.fsapi.entity.artist.Artist;
import com.fs.fsapi.metallum.result.search.ReleaseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.validator.constraints.Range;

/*
TODO:
    - add multiple categories (Separate table) join ManyToMany
*/

@Entity
@Getter
@Setter
public class Release extends MetallumUrlEntity {

    @NotNull(message = "Release year is required")
    @Range(
        min = 1900,
        max = 2099,
        message = "Release year must be between {min} and {max}"
    )
    private Integer year;

    @NotNull
    private ReleaseType releaseType;

    private String trivia;

    /**
     * Added timestamp (epoch seconds)
     */
    @NotNull
    @Positive
    @Column(updatable = false)
    private Long createdAt;

    // bidirectional association, owning side
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "artist_id",
        nullable = false,
        updatable = false
    )
    @JsonIgnoreProperties(
        value = {
            "releases",
            "hibernateLazyInitializer",
            "handler"
        },
        allowSetters = true
    )
    @JsonBackReference
    private Artist artist;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Release other)) {
            return false;
        }

        EqualsBuilder eb = new EqualsBuilder();
        eb.append(getMetallumId(), other.getMetallumId());
        return eb.isEquals();
    }
}
