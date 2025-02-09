package com.springapplication.userapp.core.domain.model;

/**
 * Sealed interface with two specific types: NoUsername and DuplicatedUsername
 * This approach ensures only two errors occur
 */
public sealed interface UserError permits UserError.NoUsername, UserError.NoEmail,
UserError.AlreadyInSystem, UserError.NoPassword, UserError.NoSecondPassword, UserError.SecondPasswordNoMatch, UserError.GenericError {

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
