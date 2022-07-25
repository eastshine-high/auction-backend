package com.eastshine.auction.product.domain.category;

import com.eastshine.auction.common.model.BaseEntity;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class ProductCategory extends BaseEntity {

    @EmbeddedId
    private ProductCategoryId productCategoryId;

    public ProductCategory(ProductCategoryId productCategoryId) {
        this.productCategoryId = productCategoryId;
    }
}
