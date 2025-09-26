package org.baldzhiyski.product.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="product_reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReservation {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY) Long id;
    @Column(nullable=false) String orderRef;
    String customerId;
    @Column(nullable=false) Integer productId;
    @Column(nullable=false) Integer quantity;
    @Column(nullable=false,length=12) String status; // PENDING/CONFIRMED/CANCELED/EXPIRED
    @Column(nullable=false)
    OffsetDateTime expiresAt;
    @Column(nullable=false) OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(nullable=false) OffsetDateTime updatedAt = OffsetDateTime.now();
    @PreUpdate void touch(){ this.updatedAt = OffsetDateTime.now(); }
}
