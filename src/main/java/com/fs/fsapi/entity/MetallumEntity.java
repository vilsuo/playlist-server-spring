package com.fs.fsapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
@MappedSuperclass
public abstract class MetallumEntity extends BaseEntity {

    /**
     * Entity business key. Must be set from the very moment
     * the Entity is created and then never change it
     * <p>
     * Using the JPA entity business key for <code>equals</code> and
     * <code>hashCode</code> is always the best choice
     */
    @NotBlank(message = "Metallum id is required")
    @Column(
        unique = true,
        updatable = false
    )
    private String metallumId;

    @NotBlank(message = "Name is required")
    private String name;

    /*
    Business key equality (this case)
    https://vladmihalcea.com/hibernate-facts-equals-and-hashcode/

    Entity identifier (non-business key -case)
    https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    */

    @Override
    public final int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(getMetallumId());
        return hcb.toHashCode();
    }

    /*
    An entity must be equal to itself across all JPA object states
    https://vladmihalcea.com/a-beginners-guide-to-jpa-hibernate-entity-state-transitions/

    Java equality contract
    - reflexive: for any non-null reference value x, x.equals(x) should return true.
    - symmetric: for any non-null reference values x and y, x.equals(y) should return
      true if and only if y.equals(x) returns true.
    - transitive: for any non-null reference values x, y, and z, if x.equals(y) returns
      true and y.equals(z) returns true, then x.equals(z) should return true.
    - consistent: for any non-null reference values x and y, multiple invocations of
      x.equals(y) consistently return true or consistently return false, provided no
      information used in equals comparisons on the objects is modified.
    - For any non-null reference value x, x.equals(null) should return false.
    */

    @Override
    public boolean equals(Object obj) {
        throw new NotImplementedException("Equals method not implemented");
    }

    @Override
    public final String toString() {
      return "#" + this.metallumId + " - " + this.name;
    }
}
