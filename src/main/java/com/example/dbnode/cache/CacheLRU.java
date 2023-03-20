package com.example.dbnode.cache;


import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class CacheLRU<K, V> implements Cache<K, V> {

    private final LinkedHashMap<K,CacheElement<K,V>> cache;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public CacheLRU(@Value(value = "${cache.capacity:10}") int capacity) {
        this.cache = new LinkedHashMap<K,CacheElement<K,V>>(capacity){
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheElement<K, V>> eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    public void put(K key, V value) {
        CacheElement<K,V> cacheElement = new CacheElement<>(key,value);
        lock.writeLock().lock();
        try {
            putKey(key,cacheElement);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<V> get(K key) {
        CacheElement<K,V> cacheElement;
        lock.readLock().lock();
        try {
            cacheElement = cache.get(key);
            if(cacheElement == null) {
                return Optional.empty();
            }
        } finally {
            lock.readLock().unlock();
        }
        putKey(key,cacheElement);
        return Optional.of(cacheElement.getValue());
    }

    private void putKey(K key, CacheElement<K,V> cacheElement){
        lock.writeLock().lock();
        try {
            cache.remove(key);
            cache.put(key,cacheElement);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void evict(K key){
        lock.writeLock().lock();
        try {
            cache.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return cache.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public LinkedHashMap<K, CacheElement<K, V>> getCache() {
        try {
            lock.readLock().lock();
            return cache;
        }
        finally {
            lock.readLock().unlock();
        }

    }
}