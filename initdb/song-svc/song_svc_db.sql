CREATE DATABASE song_svc_db;

\c song_svc_db;

CREATE TABLE song
(
    id           SERIAL PRIMARY KEY,
    resource_id  INT,
    artist       VARCHAR(255),
    name         VARCHAR(255),
    album        VARCHAR(255),
    release_year VARCHAR(4),
    duration     VARCHAR(10)
);
