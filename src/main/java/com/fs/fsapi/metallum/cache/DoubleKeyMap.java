package com.fs.fsapi.metallum.cache;

import java.util.Optional;

public interface DoubleKeyMap<K1, K2, T> {

  public void put(K1 key1, K2 key2, T value);

  public Optional<T> get(K1 key1, K2 key2);

  //public void clear(K1 key1, K2 key2);

  public void clear();

}
