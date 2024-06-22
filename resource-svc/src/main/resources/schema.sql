DROP TABLE IF EXISTS resource;

CREATE TABLE resource
(
    id         SERIAL PRIMARY KEY,
    audio_data BYTEA NOT NULL
);
