package com.fs.fsapi.entity.release;

import com.fs.fsapi.entity.MetallumUrlEntity;
import com.fs.fsapi.entity.artist.Artist;
import com.fs.fsapi.metallum.result.search.ReleaseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "artistId", "metallumId" })
})
public class Release extends MetallumUrlEntity {

    /*
    public Release(
        String metallumId,
        String name,
        String category,
        Integer year,
        ReleaseType releaseType,
        Integer createdAt
    ) {
        this(
            metallumId,
            name,
            category,
            year,
            releaseType,
            null,
            createdAt,
            null,
            null
        );
    }

    public Release(
        String metallumId,
        String name,
        String category,
        Integer year,
        ReleaseType releaseType,
        String trivia,
        Integer createdAt,
        String url,
        String imageUrl
    ) {
        super(metallumId, name);
        this.category = category;
        this.year = year;
        this.releaseType = releaseType;
        this.trivia = trivia;
        this.createdAt = createdAt;
        this.url = url;
        this.imageUrl = imageUrl;
    }
    */

    @NotBlank(message = "Category is required")
    private String category;

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
    @PositiveOrZero
    private Long createdAt;

    @ManyToOne
    private Artist artist;
}
