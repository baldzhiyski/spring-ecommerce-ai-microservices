package org.baldzhiyski.payment.payment;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Stripe PaymentIntent id (or Session payment_intent).
     * Not always unique in your setup → don’t mark unique.
     */
    @Column(nullable = false)
    private String paymentRef;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private BigDecimal amount;

    /**
     * Your internal order reference.
     * Use this to enforce "one success email per order".
     */
    @Column(nullable = false,unique = true)
    private String orderRef;

    /**
     * Flag to ensure success email is sent once per order.
     */
    @Column(nullable = false)
    private boolean successEmailSent = false;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;
}
