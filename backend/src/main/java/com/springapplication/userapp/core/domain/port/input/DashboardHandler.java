package com.springapplication.userapp.core.domain.port.input;

import com.springapplication.userapp.core.domain.model.error.UserError;
import io.vavr.control.Either;

import java.util.ArrayList;

public interface DashboardHandler {

    Either<UserError, ArrayList<com.springapplication.userapp.controller.model.TopTrackDTO>> handleSpotifyData(String username);

}
