CREATE DATABASE resource_svc_db;

\c resource_svc_db;

DROP TABLE IF EXISTS resource;
CREATE TABLE resource
(
    id          SERIAL PRIMARY KEY,
    s3_location VARCHAR(255) NOT NULL
);
