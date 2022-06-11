package com.eastshine.auction.category.domain;

import com.eastshine.auction.common.model.BaseEntity;
import lombok.Builder;
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

@Getter
@Entity
@NoArgsConstructor
public class Category extends BaseEntity {

    @Id
    @Column(name = "category_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parentId;

    @Column(nullable = false)
    private Integer ordering;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "parentId")
    private List<Category> children = new ArrayList<>();

    @Builder
    public Category(Integer id, Category parentId, Integer ordering, String name) {
        this.id = id;
        this.parentId = parentId;
        this.ordering = ordering;
        this.name = name;
    }
}
