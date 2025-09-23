package org.baldzhiyski.product.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private double availableQuantity;

    private BigDecimal price;

    private Double discount;

    @ManyToOne
    @JoinColumn(name = "category.id")
    private Category category;


}
