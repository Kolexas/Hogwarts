-- liquibase formatted sql

--changeset Kolexas:1
CREATE TABLE faculty (
    id INTEGER PRIMARY KEY,
    color VARCHAR(255),
    name VARCHAR(255)
);
--changeset Kolexas:4
CREATE INDEX faculty_name_color_index ON faculty (name,color);