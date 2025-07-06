-- liquibase formatted sql
-- changeset gp:001-create-users-table
CREATE TABLE users (
    id BINARY(16) NOT NULL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255)
);

-- rollback DROP TABLE IF EXISTS `users_table`