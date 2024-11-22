package com.springapplication.userapp.model;

/**
 * Sealed interface with two specific types: NoUsername and DuplicatedUsername
 * This approach ensures only two errors occur
 */
public sealed interface UserError permits UserError.NoUsername, UserError.DuplicatedUsername, UserError.NoEmail,
UserError.EmailAlreadyInSystem, UserError.NoPassword, UserError.NoSecondPassword, UserError.SecondPasswordNoMatch {

    record NoUsername() implements UserError {
        @Override
        public String toString() {
            return "Username required.";
        }
    }

    record  DuplicatedUsername() implements UserError {
        @Override
        public String toString() {
            return "Username already taken.";
        }
    }

    record NoEmail() implements UserError {
        @Override
        public String toString() { return "Email required."; }
    }

    record EmailAlreadyInSystem() implements UserError {
        @Override
        public String toString() { return "Email already in the system."; }
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
