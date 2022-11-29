package com.eastshine.auction.product.domain.item;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.exception.UnauthorizedException;
import com.eastshine.auction.common.model.BaseEntity;
import com.eastshine.auction.product.domain.item.fragment.ShippingFragment;
import com.eastshine.auction.product.domain.item.option.ItemOption;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;


@ToString
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false, of = "id")
@Table(name = "item")
@Entity
public class Item extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    private Integer categoryId;
    private String name;
    private Integer price;
    private boolean onSale;
    private Integer stockQuantity;
    @Embedded private ShippingFragment shippingFragment;
    private String itemOptionsTitle;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOption> itemOptions = new ArrayList();

    @Builder
    public Item(
            Integer categoryId,
            String name,
            int price,
            int stockQuantity,
            boolean onSale,
            ShippingFragment shippingFragment,
            String itemOptionsTitle
    ) {
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.onSale = onSale;
        this.shippingFragment = shippingFragment;
        this.itemOptionsTitle = itemOptionsTitle;
    }

    public void addItemOption(ItemOption itemOption) {
        itemOption.setItem(this);
        this.itemOptions.add(itemOption);
    }

    public void setItemOptions(List<ItemOption> itemOptions) {
        this.itemOptions = itemOptions;
    }

    public void validateAccessibleUser(Long userId) {
        if(super.createdBy != userId){
            throw new UnauthorizedException(ErrorCode.ITEM_INACCESSIBLE);
        }
    }

    public void decreaseStockQuantity(int quantity) {
        if (this.stockQuantity - quantity < 0) {
            throw new InvalidArgumentException(ErrorCode.ITEM_STOCK_QUANTITY_ERROR);
        }

        this.stockQuantity -= quantity;
    }

    public void increaseStockQuantity(int quantity) {
        this.stockQuantity += quantity;
    }
}
