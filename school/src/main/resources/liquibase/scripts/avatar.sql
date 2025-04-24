-- liquibase formatted sql

--changeset Kolexas:2
CREATE TABLE avatar (
    id INTEGER PRIMARY KEY,
    filePath VARCHAR(255),
    fileSize VARCHAR(255),
    mediaType VARCHAR(255),
    data bytea
);
