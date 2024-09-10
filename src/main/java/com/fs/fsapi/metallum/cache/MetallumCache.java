package com.fs.fsapi.metallum.cache;

import java.util.Optional;

public interface MetallumCache<T> {

  public void put(String artist, String title, T value);

  public Optional<T> get(String artist, String title);

}
