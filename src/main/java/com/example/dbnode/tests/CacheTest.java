package com.example.dbnode.tests;

import com.example.dbnode.cache.CacheLRU;

public class CacheTest {
    public static void main(String[] args) {
        CacheLRU<String,String> cache = new CacheLRU<>(5);
        cache.put("key1","val1");
        cache.put("key2","val2");
        cache.put("key3","val3");
        cache.put("key4","val4");
        cache.put("key5","val5");
        cache.put("key6","val6");
        cache.put("key7","val7");

        cache.getCache().forEach((key, value) -> System.out.print(key + " ")); // key3 key4 key5 key6 key7
        System.out.println();

        cache.get("key3");
        cache.getCache().forEach((key, value) -> System.out.print(key + " ")); // key4 key5 key6 key7 key3
        System.out.println();

        cache.put("key8","val8");
        cache.getCache().forEach((key, value) -> System.out.print(key + " ")); // key5 key6 key7 key3 key8
        System.out.println();
        System.out.println();

        if(cache.get("key8").isPresent())
            System.out.println("key8 was found");  //key8 was found
        System.out.println();

        if(!cache.get("key1").isPresent())
            System.out.println("key1 not found");  //key1 not found
        System.out.println();

        cache.getCache().forEach((key, value) -> System.out.print(key + " ")); // key5 key6 key7 key3 key8
        System.out.println();
        System.out.println();

        cache.evict("key5");

        cache.getCache().forEach((key, value) -> System.out.print(key + " ")); // key6 key7 key3 key8
        System.out.println();
        System.out.println();



    }
}
