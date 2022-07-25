package com.eastshine.auction.product.domain.category;

import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.product.domain.Product;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@EqualsAndHashCode(of = {"product", "category"})
@NoArgsConstructor
@Embeddable
public class ProductCategoryId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_product_category_category_category_id"))
    private Product product;

    @ManyToOne
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_product_category_product_product_id"))
    private Category category;

    public ProductCategoryId(Product product, Category category) {
        this.product = product;
        this.category = category;
    }
}
