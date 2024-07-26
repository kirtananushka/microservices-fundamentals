DROP TABLE IF EXISTS storage;

CREATE TABLE storage
(
    id           SERIAL PRIMARY KEY,
    storage_type VARCHAR(50)  NOT NULL,
    bucket       VARCHAR(100) NOT NULL,
    path         VARCHAR(255) NOT NULL
);

INSERT INTO storage (storage_type, bucket, path)
VALUES ('STAGING', 'staging-bucket', 'files'),
       ('PERMANENT', 'permanent-bucket', 'files');