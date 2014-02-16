DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id      TEXT PRIMARY KEY NOT NULL,
    data    TEXT NOT NULL
);

INSERT INTO users (id, data) VALUES ("enda.farrell@gmail.com", "{:key ""value string""}");