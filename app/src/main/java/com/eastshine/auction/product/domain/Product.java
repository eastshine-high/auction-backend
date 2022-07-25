package com.eastshine.auction.product.domain;

import com.eastshine.auction.common.model.BaseEntity;
import com.eastshine.auction.product.domain.category.ProductCategory;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@EqualsAndHashCode(callSuper=false, of = "id")
@Getter
@Entity
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @OneToMany(mappedBy = "productCategoryId.product")
    private List<ProductCategory> productCategories;

    @Column(nullable = false)
    private String name;
    private int price;
    private int stockQuantity;
    private boolean onSale;

    @Builder
    public Product(List<ProductCategory> productCategories, String name, int price, int stockQuantity, boolean onSale) {
        this.productCategories = productCategories;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.onSale = onSale;
    }
}
