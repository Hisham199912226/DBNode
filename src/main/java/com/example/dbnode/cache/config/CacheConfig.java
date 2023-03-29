package com.example.dbnode.cache.config;

import com.example.dbnode.api.client.model.DocumentsCollection;
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
