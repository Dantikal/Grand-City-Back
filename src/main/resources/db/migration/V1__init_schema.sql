-- Grand City backend — initial schema

CREATE TABLE agents (
    id            VARCHAR(64)     PRIMARY KEY,
    slug          VARCHAR(120)    NOT NULL UNIQUE,
    name          VARCHAR(160)    NOT NULL,
    role          VARCHAR(160)    NOT NULL,
    bio           TEXT            NOT NULL DEFAULT '',
    long_bio      TEXT            NOT NULL DEFAULT '',
    photo         TEXT            NOT NULL DEFAULT '',
    email         VARCHAR(160)    NOT NULL,
    phone         VARCHAR(60)     NOT NULL DEFAULT '',
    specialties   TEXT[]          NOT NULL DEFAULT '{}',
    areas         TEXT[]          NOT NULL DEFAULT '{}',
    sales_count   INTEGER         NOT NULL DEFAULT 0,
    rating        NUMERIC(2,1)    NOT NULL DEFAULT 0,
    since         INTEGER         NOT NULL
);

CREATE TABLE properties (
    id            VARCHAR(64)     PRIMARY KEY,
    slug          VARCHAR(160)    NOT NULL UNIQUE,
    title         VARCHAR(120)    NOT NULL,
    area          VARCHAR(200)    NOT NULL,
    city          VARCHAR(120)    NOT NULL,
    price         NUMERIC(14,2)   NOT NULL,
    rent_period   VARCHAR(20),
    category      VARCHAR(20)     NOT NULL,
    listing_type  VARCHAR(20)     NOT NULL,
    kind          VARCHAR(30)     NOT NULL,
    status        VARCHAR(20)     NOT NULL,
    beds          INTEGER         NOT NULL DEFAULT 0,
    baths         INTEGER         NOT NULL DEFAULT 0,
    sqft          INTEGER         NOT NULL,
    images        TEXT[]          NOT NULL DEFAULT '{}',
    description   TEXT            NOT NULL DEFAULT '',
    features      TEXT[]          NOT NULL DEFAULT '{}',
    agent_id      VARCHAR(64)     REFERENCES agents(id) ON DELETE SET NULL,
    lat           NUMERIC(10,6),
    lng           NUMERIC(10,6),
    featured      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE INDEX idx_properties_category      ON properties (category);
CREATE INDEX idx_properties_listing_type  ON properties (listing_type);
CREATE INDEX idx_properties_kind          ON properties (kind);
CREATE INDEX idx_properties_price         ON properties (price);
CREATE INDEX idx_properties_featured      ON properties (featured);
CREATE INDEX idx_properties_created_at    ON properties (created_at);

CREATE TABLE requests (
    id            BIGSERIAL       PRIMARY KEY,
    name          VARCHAR(80)     NOT NULL,
    email         VARCHAR(160)    NOT NULL,
    phone         VARCHAR(60),
    kind          VARCHAR(20)     NOT NULL DEFAULT 'general',
    message       TEXT            NOT NULL,
    status        VARCHAR(20)     NOT NULL DEFAULT 'new',
    created_at    TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE INDEX idx_requests_status     ON requests (status);
CREATE INDEX idx_requests_created_at ON requests (created_at);

CREATE TABLE bookings (
    id            BIGSERIAL       PRIMARY KEY,
    property_id   VARCHAR(64)     NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    name          VARCHAR(80)     NOT NULL,
    email         VARCHAR(160)    NOT NULL,
    phone         VARCHAR(60)     NOT NULL,
    date          VARCHAR(30)     NOT NULL,
    message       TEXT,
    created_at    TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE INDEX idx_bookings_property_id ON bookings (property_id);

CREATE TABLE admin_users (
    id              BIGSERIAL     PRIMARY KEY,
    username        VARCHAR(80)   NOT NULL UNIQUE,
    password_hash   VARCHAR(200)  NOT NULL
);
