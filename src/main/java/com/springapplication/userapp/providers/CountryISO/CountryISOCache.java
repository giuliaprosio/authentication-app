package com.springapplication.userapp.providers.CountryISO;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class CountryISOCache {

    Map<String, String> countries = new HashMap<>();

    public CountryISOCache(){
        for(String iso : Locale.getISOCountries()){
            Locale l = new Locale("", iso);
            String countryName = l.getDisplayCountry(Locale.ENGLISH).toLowerCase();
            var iso3 = l.getISO3Country();
            countries.put(countryName, iso3);
        }
        countries.put("england", "GBR");
    }

    public String getISOFromCountry(String country){
        return countries.getOrDefault(country.trim().toLowerCase(), "");
    }
}
