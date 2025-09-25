-- Per-customer discount for a specific product. Overrides product.discount when active & within time window.

CREATE TABLE IF NOT EXISTS customer_product_discount (
                                                         id           INTEGER PRIMARY KEY,
                                                         customer_id  INTEGER      NOT NULL,
                                                         product_id   INTEGER     NOT NULL REFERENCES product(id) ON DELETE CASCADE,
                                                         discount     NUMERIC(4,3) NOT NULL CHECK (discount >= 0 AND discount <= 1), -- 0..1 (e.g., 0.150 = 15%)
                                                         starts_at    TIMESTAMPTZ NULL,
                                                         ends_at      TIMESTAMPTZ NULL,
                                                         active       BOOLEAN     NOT NULL DEFAULT TRUE,

                                                         created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                                         updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Ensure at most one *active* row per (customer, product)
CREATE UNIQUE INDEX IF NOT EXISTS ux_cpd_active
    ON customer_product_discount(customer_id, product_id)
    WHERE active = TRUE;

CREATE INDEX IF NOT EXISTS idx_cpd_customer_product ON customer_product_discount(customer_id, product_id);
CREATE INDEX IF NOT EXISTS idx_cpd_window ON customer_product_discount(starts_at, ends_at);
