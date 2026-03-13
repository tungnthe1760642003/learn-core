package org.example.learncore.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers", indexes = {
    // 2. NON-CLUSTERED INDEX: Đánh index cho email và số điện thoại
    // Vì chúng ta thường xuyên tìm khách hàng qua 2 thông tin này
    @Index(name = "idx_customer_email", columnList = "email"),
    @Index(name = "idx_customer_phone", columnList = "phone")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 1. CLUSTERED INDEX: Tự động đánh vào Primary Key (ID)
    // Dữ liệu sẽ được sắp xếp vật lý theo ID này
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 15)
    private String phone;

    private String address;
}
