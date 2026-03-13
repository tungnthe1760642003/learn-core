package org.example.learncore.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * CAO CẤP: Minh họa PESSIMISTIC LOCK (Khóa bi quan)
 * Phù hợp cho: Inventory (Kho hàng), Payment (Thanh toán), Vé máy bay.
 * - Nơi mà CONFLICT xảy ra liên tục và dữ liệu cực kỳ nhạy cảm.
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer stock; // Số lượng tồn kho
}
