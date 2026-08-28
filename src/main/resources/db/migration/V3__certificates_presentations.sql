-- Certificates gallery with explicit ordering
CREATE TABLE certificates (
    id         VARCHAR(64)   PRIMARY KEY,
    title      VARCHAR(200)  NOT NULL DEFAULT '',
    issuer     VARCHAR(200)  NOT NULL DEFAULT '',
    year       VARCHAR(20)   NOT NULL DEFAULT '',
    image      TEXT          NOT NULL DEFAULT '',
    sort_order INTEGER       NOT NULL DEFAULT 0
);

-- Uploaded company presentation files, one per language code
CREATE TABLE presentations (
    lang   VARCHAR(10)  PRIMARY KEY,
    url    TEXT         NOT NULL DEFAULT '',
    name   VARCHAR(200) NOT NULL DEFAULT ''
);
