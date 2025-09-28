package org.baldzhiyski.product.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
@Entity
@Table(name = "product_reservation")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)            private String orderRef;
    private String customerId;
    @Column(nullable = false)            private Integer productId;
    @Column(nullable = false)            private Integer quantity;
    @Column(nullable = false, length=12) private String status; // PENDING/...

    @Column(nullable = false)            private OffsetDateTime expiresAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}
