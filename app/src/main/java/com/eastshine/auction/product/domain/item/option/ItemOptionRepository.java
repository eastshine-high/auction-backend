package com.eastshine.auction.product.domain.item.option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {

    @Query("select o from ItemOption o where o.item.id = :itemId order by o.ordering")
    public List<ItemOption> findByItemId(@Param("itemId") Long itemId);
}
