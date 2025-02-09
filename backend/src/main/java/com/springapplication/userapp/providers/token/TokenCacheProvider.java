package com.springapplication.userapp.providers.token;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class TokenCacheProvider {

    public TokenCacheProvider(){}

    public Cache<String, String> generateCache() {
        return CacheBuilder.newBuilder().expireAfterWrite(55, TimeUnit.MINUTES).build();
    }
}
