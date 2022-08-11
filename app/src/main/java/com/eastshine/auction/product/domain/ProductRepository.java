package com.eastshine.auction.product.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @EntityGraph(attributePaths = {"productOptions"})
    Optional<Product> findEagerById(Long id);
}
