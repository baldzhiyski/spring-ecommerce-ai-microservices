package org.baldzhiyski.product.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "customer_product_discount")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal discount; // 0..1 (e.g., 0.150)

    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    void touch() { this.updatedAt = OffsetDateTime.now(); }
}
