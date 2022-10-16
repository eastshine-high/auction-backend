package com.eastshine.auction.product.domain.item.option;

import com.eastshine.auction.common.exception.ErrorCode;
import com.eastshine.auction.common.exception.InvalidArgumentException;
import com.eastshine.auction.common.model.BaseEntity;
import com.eastshine.auction.product.domain.item.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@ToString
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false, of = "id")
@Table(name = "item_option")
@Entity
public class ItemOption extends BaseEntity {

    @Id @Column(name = "item_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "item_id", foreignKey = @ForeignKey(name = "fk_item_option_item"))
    private Item item;
    private String itemOptionName;
    private Integer stockQuantity;
    private Integer ordering;

    @Builder
    public ItemOption(
            Item item,
            String itemOptionName,
            Integer stockQuantity,
            Integer ordering
    ) {
        this.item = item;
        this.itemOptionName = itemOptionName;
        this.stockQuantity = stockQuantity;
        this.ordering = ordering;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void decreaseStockQuantity(int quantity) {
        if (this.stockQuantity - quantity < 0) {
            throw new InvalidArgumentException(ErrorCode.ITEM_STOCK_QUANTITY_ERROR);
        }

        this.stockQuantity -= quantity;
    }
}
