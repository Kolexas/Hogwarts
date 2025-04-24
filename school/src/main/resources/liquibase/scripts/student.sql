-- liquibase formatted sql

--changeset Kolexas:3
CREATE TABLE student (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255),
    age INT NOT NULL,
    faculty_id INT,
    avatar_id INT,
    FOREIGN KEY (faculty_id) REFERENCES faculty(id),
    FOREIGN KEY (avatar_id) REFERENCES avatar(id)
);
--changeset Kolexas:5
CREATE INDEX student_name_index ON student (name);