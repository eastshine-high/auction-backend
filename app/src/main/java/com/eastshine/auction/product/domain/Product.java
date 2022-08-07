package com.eastshine.auction.product.domain;

import com.eastshine.auction.category.domain.Category;
import com.eastshine.auction.common.model.BaseEntity;
import com.eastshine.auction.product.domain.option.ProductOption;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false, of = "id")
@Table(name = "product")
@Entity
public class Product extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_product_category_category_id"))
    private Category category;

    private String name;

    private Integer price;

    private boolean onSale;

    private Integer stockQuantity;

    private String productOptionsTitle;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> productOptions = new ArrayList();

    @Builder
    public Product(Category category, String name, int price, int stockQuantity, boolean onSale, String productOptionsTitle, List<ProductOption> productOptions) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.onSale = onSale;
        this.productOptionsTitle = productOptionsTitle;
        this.productOptions = productOptions;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void addProductOption(ProductOption productOption) {
        this.productOptions.add(productOption);
        productOption.setProduct(this);
    }
}
