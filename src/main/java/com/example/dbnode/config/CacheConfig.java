package com.example.dbnode.config;

import com.example.dbnode.api.model.*;
import com.example.dbnode.cache.CacheLRU;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
@Configuration
public class CacheConfig {
    @Bean
    @Scope("prototype")
    @Qualifier("collectionCache")
    public CacheLRU<String, DocumentsCollection> collectionCache() {
        return new CacheLRU<>(8);
    }

}
