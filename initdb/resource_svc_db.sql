CREATE DATABASE resource_svc_db;

\c resource_svc_db;

CREATE TABLE resource
(
    id         SERIAL PRIMARY KEY,
    audio_data BYTEA NOT NULL
);
