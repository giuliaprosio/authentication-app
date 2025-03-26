package com.springapplication.userapp.core.domain.model.error;

import java.util.Optional;

public sealed interface AdaptersError extends UserError permits AdaptersError.DatabaseError {

     record DatabaseError(String message, Optional<Throwable> exception) implements AdaptersError {}

}
