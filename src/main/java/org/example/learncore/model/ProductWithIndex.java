package org.example.learncore.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_with_index", indexes = {
    @Index(name = "idx_product_sku", columnList = "sku")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku; // Chúng ta sẽ tìm kiếm theo cái này (Có Non-Clustered Index)
    private String name;
}
