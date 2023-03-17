package com.example.DBNode.config;

import com.example.DBNode.api.model.*;
import com.example.DBNode.cache.CacheLRU;
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
