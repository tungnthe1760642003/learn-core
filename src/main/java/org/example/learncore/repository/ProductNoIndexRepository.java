package org.example.learncore.repository;

import org.example.learncore.model.ProductNoIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductNoIndexRepository extends JpaRepository<ProductNoIndex, Long> {
    Optional<ProductNoIndex> findBySku(String sku);
}
