package com.eastshine.auction.product.domain.product.option;

import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    @Query("select o from ProductOption o where o.product.id = :productId order by o.ordering")
    public List<ProductOption> findByProductId(@Param("productId") Long productId);
}
