package com.springapplication.userapp.core.domain.model;

import com.springapplication.userapp.controller.model.TopTrackDTO;
import com.springapplication.userapp.client.model.AuthTokenDTO;

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
}
