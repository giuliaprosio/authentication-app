package com.springapplication.userapp.core.domain.model.error;


/**
 * Sealed interface with two specific types: NoUsername and DuplicatedUsername
 * This approach ensures only two errors occur
 */
public sealed interface UserError permits AdaptersError, UserError.AlreadyInSystem, UserError.GenericError, UserError.NoEmail, UserError.NoPassword, UserError.NoSecondPassword, UserError.NoUsername, UserError.SecondPasswordNoMatch
         {

    record GenericError(String error) implements UserError {
        @Override
        public String toString() {
            return error;
        }
    }

    record AlreadyInSystem() implements UserError {
        @Override
        public String toString() { return "Username or email already in system"; }
    }

    record NoUsername() implements UserError {
        @Override
        public String toString() {
            return "Username required.";
        }
    }

    record NoEmail() implements UserError {
        @Override
        public String toString() { return "Email required."; }
    }

    record NoPassword() implements UserError {
        @Override
        public String toString() { return "Password required."; }
    }

    record NoSecondPassword() implements UserError {
        @Override
        public String toString() { return "Second password check required."; }
    }

    record SecondPasswordNoMatch() implements  UserError {
        @Override
        public String toString() { return "The passwords are not matching."; }
    }

}
