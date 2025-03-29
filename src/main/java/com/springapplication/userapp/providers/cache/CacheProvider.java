package com.springapplication.userapp.providers.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.springapplication.userapp.controller.model.TopTrackDTO;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheProvider {

    public CacheProvider(){}

    public Cache<String, String> generateTokenCache() {
        return CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
    }

    public Cache<String, ArrayList<TopTrackDTO>> generateTracksCache() {
        return CacheBuilder.newBuilder().build();
    }
}
