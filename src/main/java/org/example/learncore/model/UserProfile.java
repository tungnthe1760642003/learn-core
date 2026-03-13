package org.example.learncore.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * CAO CẤP: Minh họa OPTIMISTIC LOCK (Khóa lạc quan)
 * Phù hợp cho: User Profile, Blog Post, Product Info.
 * - Nơi mà CONFLICT hiếm khi xảy ra. 
 * - Ưu tiên hiệu năng (không khóa DB), chỉ kiểm tra khi update.
 */
@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String bio;

    @Version
    private Integer version; // Hibernate tự động quản lý để check conflict
}
