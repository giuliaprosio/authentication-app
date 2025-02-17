package com.springapplication.userapp.core.domain.port.output;

import com.springapplication.userapp.client.model.AuthTokenDTO;
import com.springapplication.userapp.controller.model.TopTrackDTO;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import io.vavr.control.Either;

import java.util.ArrayList;

/**
 * Gateway to Spotify API
 */
public interface SpotifyGateway {

    Either<UserError, AuthTokenDTO> getRefreshToken(String code, String state, String redirect_uri);

    Either<UserError, ArrayList<TopTrackDTO>> getTopTrack(User user);

}
