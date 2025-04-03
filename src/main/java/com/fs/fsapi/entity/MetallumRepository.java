package com.fs.fsapi.entity;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/*
If many repositories in your application should have the same set of methods you can define
your own base interface to inherit from. Such an interface must be annotated with @NoRepositoryBean.
This prevents Spring Data to try to create an instance of it directly and failing because it can’t
determine the entity for that repository, since it still contains a generic type variable.
*/

@NoRepositoryBean
public interface MetallumRepository<T extends MetallumEntity> extends JpaRepository<T, Integer> {

    Optional<T> findByMetallumId(@NonNull String metallumId);
}
