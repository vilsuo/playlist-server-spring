package com.fs.fsapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    /*
    GenerationType.SEQUENCE
    - supported by PostgreSQL, not MySQL
    - is table free and the same sequence can be assigned to multiple columns or tables
    - may preallocate values to improve performance
    - may define an incremental step, allowing us to benefit from a “pooled” Hilo algorithm
    - doesn’t restrict Hibernate JDBC batching
    - doesn’t restrict Hibernate inheritance models
    */

    @Id
    @SequenceGenerator(name = "sequence")
    @GeneratedValue(
        generator = "sequence",
        strategy = GenerationType.SEQUENCE
    )
    private Integer id;
}
