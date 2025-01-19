package com.fs.fsapi.entity.artist;

import com.fs.fsapi.entity.MetallumUrlEntity;
import com.fs.fsapi.entity.release.Release;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "metallumId" })
})
public class Artist extends MetallumUrlEntity {

    /*
    public Artist(String metallumId,String name) {
        this(metallumId, name, null, null, null);
    };

    public Artist(
        String metallumId,
        String name,
        String country,
        String url,
        String imageUrl
    ) {
        super(metallumId, name);
        this.country = country;
        this.url = url;
        this.imageUrl = imageUrl;
        this.releases = new ArrayList<>();
    }
    */

    @NotBlank(message = "Country is required")
    private String country;

    // https://medium.com/jpa-java-persistence-api-guide/cascadetype-managing-related-entities-in-jpa-and-spring-data-b7fe446aedbd
    @OneToMany(
        mappedBy = "artist",
        cascade = { CascadeType.ALL },
        orphanRemoval = true
    )
    private List<Release> releases;

    public void addRelease(@NotNull Release release) {
        this.releases.add(release);
        release.setArtist(this);
    }

    public void removeRelease(@NotNull Release release) {
        this.releases.removeIf(r -> r.getId().equals(release.getId()));
        release.setArtist(null);
    }

    public boolean hasReleases() {
        return !this.releases.isEmpty();
    }
}
