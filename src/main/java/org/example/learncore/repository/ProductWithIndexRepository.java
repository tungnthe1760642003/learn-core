package org.example.learncore.repository;

import org.example.learncore.model.ProductWithIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductWithIndexRepository extends JpaRepository<ProductWithIndex, Long> {
    Optional<ProductWithIndex> findBySku(String sku);
}
