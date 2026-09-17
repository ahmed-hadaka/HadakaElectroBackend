package com.hadaka_electro.common.entities.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "product_images")
@ToString(exclude = "product")
@NoArgsConstructor
public class ProductImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductImages(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    public String getImagePath() {
        return "../product_images/" + product.getId() + "/extras/" + name;
    }
}
