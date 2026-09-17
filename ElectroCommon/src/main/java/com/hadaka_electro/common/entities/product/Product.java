package com.hadaka_electro.common.entities.product;

import com.hadaka_electro.common.entities.AbstractAuditableEntity;
import com.hadaka_electro.common.entities.Brand;
import com.hadaka_electro.common.entities.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "products")
@EqualsAndHashCode(exclude = {"brand", "category", "productDetails", "productImages"})
@EntityListeners(AuditingEntityListener.class)
public class Product extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, message = "Product name must be between 2 and 256 characters")
    private String name;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Product alias cannot be blank")
    @Size(min = 2, message = "Product alias must be between 2 and 256 characters")
    private String alias;

    @Column(nullable = false, name = "short_description", length = 2000)
    @Size(min = 5, message = "The minimum sh-desc length is 5")
    @NotBlank(message = "The sh-desc can not be blank")
    private String shortDescription;

    @Column(nullable = false, name = "full_description", length = 5000)
    @Size(min = 10, message = "The minimum full-desc length is 10")
    @NotBlank(message = "The full-desc can not be blank")
    private String fullDescription;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    private Boolean enabled;

    @Column(name = "in_stock")
    private Boolean inStock;

    private double price;

    private double cost;

    @Column(name = "discount_percent")
    private double discountPercent;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImages> productImages = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductDetails> productDetails = new HashSet<>();

    private double length;

    private double width;

    private double height;

    private double weight;

    public Product(String name, String alias, String shortDescription, String fullDescription, String mainImage,
                   LocalDateTime createdAt, String createdBy) {
        super();
        this.name = name;
        this.alias = alias;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.mainImage = mainImage;
        this.setCreatedAt(createdAt);
        this.setCreatedBy(createdBy);
    }

    public String getMainImagePath() {
        if (id <= 0 || mainImage == null) {
            return "/assets/images/default-product.png";
        }
        return "../product_images/" + id + "/" + mainImage;
    }

    public double getPriceAfterDiscount() {
        return price -= (price * (discountPercent / 100));
    }

}
