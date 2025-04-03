package com.fs.fsapi.entity.artist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fs.fsapi.entity.MetallumUrlEntity;
import com.fs.fsapi.entity.release.Release;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Slf4j
public class Artist extends MetallumUrlEntity {

    @NotBlank(message = "Country is required")
    private String country;

    // bidirectional association, inverse side
    @OneToMany(
        mappedBy = "artist", // @ManyToOne side is responsible for handling this association
        cascade = { CascadeType.ALL },
        orphanRemoval = true
    )
    @JsonIgnoreProperties(
        value = {
            "artist",
            "hibernateLazyInitializer",
            "handler"
        },
        allowSetters = true
    )
    @JsonManagedReference
    private List<Release> releases = new ArrayList<>();

    /*
    only synchronized bidirectional associations are guaranteed to be persisted
    properly in the database
    */

    // used to synchronize both sides of the bidirectional association
    public void addRelease(@NotNull Release release) {
        if (!this.releases.contains(release)) {
            log.info("Adding release " + release + " to artist " + this);

            this.releases.add(release);
            release.setArtist(this);
        } else {
            log.error(
                "Can not add release " + release + " to artist " + this
                + ": artist already has this release"
            );
        }
    }

    // used to synchronize both sides of the bidirectional association
    public void removeRelease(@NotNull Release release) {
        if (this.releases.contains(release)) {
            log.info("Removing release " + release + " from artist " + this);

            this.releases.remove(release);
            release.setArtist(null);
        } else {
            log.error(
                "Can not remove release " + release + " from artist " + this
                + ": artist does not have this release"
            );
        }
    }

    public boolean hasReleases() {
        return !this.releases.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Artist other)) {
            return false;
        }

        EqualsBuilder eb = new EqualsBuilder();
        eb.append(getMetallumId(), other.getMetallumId());
        return eb.isEquals();
    }
}
