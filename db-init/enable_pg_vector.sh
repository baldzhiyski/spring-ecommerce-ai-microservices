\connect ai_db
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS spring_ai_chat_memory (
  conversation_id VARCHAR(255) NOT NULL,
  content         TEXT         NOT NULL,
  type            VARCHAR(64)  NOT NULL,
  "timestamp"     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_sacm_conv_ts
  ON spring_ai_chat_memory (conversation_id, "timestamp");
