package com.eastshine.auction.category.domain;

import com.eastshine.auction.common.model.BaseEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper=false, of = "id")
@Getter
@Entity
@NoArgsConstructor
public class Category extends BaseEntity {

    @Id
    @Column(name = "category_id")
    private Integer id; // Not auto Generated

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;


    @Column(nullable = false)
    private Integer ordering;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    @Builder
    public Category(Integer id, Category parent, Integer ordering, String name) {
        this.id = id;
        this.parent = parent;

        this.ordering = ordering;
        this.name = name;
    }
}
