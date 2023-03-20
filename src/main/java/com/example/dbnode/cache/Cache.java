package com.example.dbnode.cache;

import java.util.Optional;

public interface Cache<K, V> {

    void put(K key, V value);

    Optional<V> get(K key);

    void evict(K key);

    int size();

    boolean isEmpty();

    void clear();
}
