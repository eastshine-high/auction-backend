package com.eastshine.auction.product.domain.option;

import com.eastshine.auction.common.model.BaseEntity;
import com.eastshine.auction.product.domain.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false, of = "id")
@Table(name = "product_option")
@Entity
public class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_product_option_product"))
    private Product product;
    private String productOptionName;
    private Integer stockQuantity;
    private Integer ordering;

    @Builder
    public ProductOption(
            Product product,
            String productOptionName,
            Integer stockQuantity,
            Integer ordering
    ) {
        this.product = product;
        this.productOptionName = productOptionName;
        this.stockQuantity = stockQuantity;
        this.ordering = ordering;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
