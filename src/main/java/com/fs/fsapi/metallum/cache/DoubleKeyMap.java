package com.fs.fsapi.metallum.cache;

import java.util.Optional;

public interface DoubleKeyMap<T> {

  public void put(String key1, String key2, T value);

  public Optional<T> get(String key1, String key2);

}
