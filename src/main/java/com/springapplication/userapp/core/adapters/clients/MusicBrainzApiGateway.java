package com.springapplication.userapp.core.adapters.clients;

import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.providers.countryISO.CountryISOCache;
import com.springapplication.userapp.providers.logging.Logger;
import com.springapplication.userapp.providers.logging.LoggerFactory;
import io.vavr.control.Either;
import org.springframework.stereotype.Component;
import com.springapplication.userapp.controller.model.TopTrackDTO;
import com.springapplication.userapp.client.model.TotalObjectDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
class MusicBrainzApiGateway {

    private final ClientBuilder clientBuilder;
    private final CountryISOCache countryISOCache;

    private final Logger logger = LoggerFactory.getLogger(MusicBrainzApiGateway.class);

    MusicBrainzApiGateway(ClientBuilder clientBuilder, CountryISOCache countryISOCache) {
        this.clientBuilder = clientBuilder;
        this.countryISOCache = countryISOCache;
    }

    Either<UserError, TopTrackDTO> getArtistCountry(TotalObjectDTO dto) {
        var topTrackDTO = new TopTrackDTO();
        topTrackDTO.setName(dto.getName());
        String artistName = dto.getArtists().get(0).getName();
        topTrackDTO.setArtist(artistName);
        topTrackDTO.setImg(dto.getAlbum().getImages().get(1).getUrl());

        var maybeCountry = syncMusicBrainz(artistName);

        return maybeCountry
                .flatMap(country -> {
                    var iso = setCountryISO(country);
                    if(iso.isLeft()) return Either.left(iso.getLeft());
                    topTrackDTO.setCountry(iso.get());
                    return Either.right(topTrackDTO);
                });
    }

    private Either<UserError, String> setCountryISO(String country){
        String iso = countryISOCache.getISOFromCountry(country);
        if(iso == null){
            logger.error("No ISO for country name: " + country);
            var error = new UserError.GenericError("No ISO for country name: " + country);
            return Either.left(error);
        }

        return Either.right(iso);
    }

    private Either<UserError, String> syncMusicBrainz(String artist){
        return getCountrySync(artist).block();
    }

    private Mono<Either<UserError, String>> getCountrySync(String artist){
        return getArtistCountryDto(artist)
                .flatMap(dto -> {
                    var country = dto.getArtists().get(0).getArea().getName();
                    if(country == null){
                        var error = new UserError.GenericError("Error parsing Music Brainz");
                        logger.warn("Error parsing Music Brainz json");
                        return Mono.just(Either.left(error));
                    }
                    return Mono.just(Either.right(country));
                });
    }

    private Mono<com.springapplication.userapp.client.model.MusicBrainzDTO> getArtistCountryDto(String artist){
        WebClient musicBrainzClient = clientBuilder.buildClient("/artist", "musicBrainz");

        return musicBrainzClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", artist)
                        .queryParam("fmt", "json")
                        .build())
                .retrieve()
                .bodyToMono(com.springapplication.userapp.client.model.MusicBrainzDTO.class);
    }


}
