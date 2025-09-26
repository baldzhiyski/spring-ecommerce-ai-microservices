-- reservations (no stock decrement yet)
CREATE TABLE IF NOT EXISTS product_reservation (
                                                   id          BIGSERIAL PRIMARY KEY,
                                                   order_ref   VARCHAR   NOT NULL,
                                                   customer_id VARCHAR       NULL,
                                                   product_id  INTEGER      NOT NULL REFERENCES product(id) ON DELETE CASCADE,
                                                   quantity    INTEGER      NOT NULL CHECK (quantity > 0),
                                                   status      VARCHAR(12)  NOT NULL,         -- PENDING | CONFIRMED | CANCELED | EXPIRED
                                                   expires_at  TIMESTAMPTZ  NOT NULL,
                                                   created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                                   updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_resv_ref_status ON product_reservation(order_ref, status);
CREATE INDEX IF NOT EXISTS idx_resv_product     ON product_reservation(product_id, status);

-- Optional: ensure one PENDING row per (order_ref, product) to keep things simple
CREATE UNIQUE INDEX IF NOT EXISTS ux_resv_ref_product_pending
    ON product_reservation(order_ref, product_id)
    WHERE status = 'PENDING';
