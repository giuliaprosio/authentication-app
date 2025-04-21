package com.springapplication.userapp.core.domain.model;

import com.springapplication.userapp.client.model.AlbumDTO;
import com.springapplication.userapp.client.model.ArtistDTO;
import com.springapplication.userapp.client.model.ArtistSimpleDTO;
import com.springapplication.userapp.client.model.AuthTokenDTO;
import com.springapplication.userapp.client.model.TopTracksSpotifyResponseDTO;
import com.springapplication.userapp.client.model.TotalObjectDTO;
import com.springapplication.userapp.controller.model.TopTrackDTO;
import com.springapplication.userapp.client.model.ImageSimpleDTO;
import com.springapplication.userapp.client.model.MusicBrainzDTO;
import com.springapplication.userapp.client.model.ArtistMusicBrainzDTO;
import com.springapplication.userapp.client.model.CountryArtistMusicBrainzDTO;

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
        dto.setName("name");
        dto.setAlbum(createValidAlbumDTO());

        return dto;
    }

    public static AlbumDTO createValidAlbumDTO() {
        var dto = new AlbumDTO();
        dto.setImages(List.of(createValidImageDTO(), createValidImageDTO()));

        return dto;
    }

    public static ImageSimpleDTO createValidImageDTO() {
        var dto = new ImageSimpleDTO();
        dto.setUrl("url");

        return dto;
    }

    public static ArtistSimpleDTO createValidSimpleArtistDTO() {
        var dto = new ArtistSimpleDTO();
        dto.setName("artistName");

        return dto;
    }

    public static TopTrackDTO createValidTrackDTO(TotalObjectDTO totalDTO) {
        var dto = new TopTrackDTO();
        dto.setArtist(totalDTO.getArtists().get(0).getName());
        dto.setImg(totalDTO.getAlbum().getImages().get(0).getUrl());
        dto.setName(totalDTO.getName());

        return dto;
    }

    public static MusicBrainzDTO createInvalidMusicBrainzDTO() {
        return new MusicBrainzDTO();
    }

    public static MusicBrainzDTO createValidMusicBrainzDTO() {
        var dto = new MusicBrainzDTO();
        dto.setArtists(List.of(createValidArtistMusicBrainz()));

        return dto;
    }

    public static ArtistMusicBrainzDTO createValidArtistMusicBrainz() {
        var dto = new ArtistMusicBrainzDTO();
        var area = new CountryArtistMusicBrainzDTO();
        area.setName("england");
        dto.setArea(area);

        return dto;
    }

}
