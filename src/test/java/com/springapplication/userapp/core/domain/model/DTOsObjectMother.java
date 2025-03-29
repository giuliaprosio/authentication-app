package com.springapplication.userapp.core.domain.model;

import com.springapplication.userapp.controller.model.TopTrackDTO;
import com.springapplication.userapp.client.model.AuthTokenDTO;
import com.springapplication.userapp.client.model.TopTracksSpotifyResponseDTO;
import com.springapplication.userapp.client.model.ArtistDTO;
import com.springapplication.userapp.client.model.TotalObjectDTO;
import com.springapplication.userapp.client.model.ArtistSimpleDTO;

import java.util.List;
import java.util.UUID;

public class DTOsObjectMother {
    public static TopTrackDTO createValidTopTrackDTO(){
        var track = new TopTrackDTO();
        track.setImg(UUID.randomUUID().toString());
        track.setName(UUID.randomUUID().toString());
        return track;
    }

    public static AuthTokenDTO createValidAuthTokenDTO(){
        var dto = new AuthTokenDTO();
        dto.setAccessToken(UUID.randomUUID().toString());
        dto.setRefreshToken(UUID.randomUUID().toString());
        dto.setTokenType("auth");
        dto.setExpiresIn(3600);

        return dto;
    }

    public static TopTracksSpotifyResponseDTO createValidSpotifyTracksResponseDTO() {
        var dto = new TopTracksSpotifyResponseDTO();
        var artist = createValidArtistDTO();

        dto.setItems(List.of(artist));

        return dto;
    }

    public static ArtistDTO createValidArtistDTO() {
        var dto = new ArtistDTO();
        dto.setName("randomName");
        dto.setId("id");
        return dto;
    }

    public static TotalObjectDTO createValidTotalObjectDTO() {
        var dto = new TotalObjectDTO();
        var artist = createValidSimpleArtistDTO();
        dto.setArtists(List.of(artist));

        return dto;
    }

    public static ArtistSimpleDTO createValidSimpleArtistDTO() {
        var dto = new ArtistSimpleDTO();
        dto.setName("artistName");

        return dto;
    }
}
