package com.eastshine.auction.product.repository.item;

import com.eastshine.auction.product.domain.item.Item;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    @EntityGraph(attributePaths = {"itemOptions"})
    Optional<Item> findEagerById(Long id);
}
