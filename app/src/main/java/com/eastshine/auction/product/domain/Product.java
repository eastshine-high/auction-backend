package com.eastshine.auction.product.domain;

import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.common.model.BaseEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


@EqualsAndHashCode(callSuper=false, of = "id")
@Getter
@NoArgsConstructor
@Entity
public class Product extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_product_category_category_id"))
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private boolean onSale;

    @Builder
    public Product(Category category, String name, int price, int stockQuantity, boolean onSale) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.onSale = onSale;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
