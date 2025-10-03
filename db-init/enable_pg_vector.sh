\connect vectordb
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS policies (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    embedding VECTOR(1536)  -- adapt dimension to your embedding model
);
