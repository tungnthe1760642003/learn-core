package org.example.learncore.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learncore.model.Inventory;
import org.example.learncore.model.UserProfile;
import org.example.learncore.repository.InventoryRepository;
import org.example.learncore.repository.UserProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * SEED DATA: Tự động tạo dữ liệu khi ứng dụng khởi chạy.
 * Để DB tự sinh ID (GenerationType.IDENTITY) giúp tránh xung đột mapping.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Bắt đầu Seed Data cho demo Lock...");

        // 1. Seed Inventory (Cho Pessimistic Lock)
        if (inventoryRepository.count() == 0) {
            Inventory item = Inventory.builder()
                    .name("iPhone 15 Pro Max")
                    .stock(10)
                    .build();
            inventoryRepository.save(item);
            log.info("✅ Đã tạo sản phẩm mẫu: iPhone 15 Pro Max (Stock: 10)");
        }

        // 2. Seed UserProfile (Cho Optimistic Lock)
        if (userProfileRepository.count() == 0) {
            UserProfile profile = UserProfile.builder()
                    .username("nguyen_van_a")
                    .bio("Hello, I am a Java Dev!")
                    .build();
            userProfileRepository.save(profile);
            log.info("✅ Đã tạo profile mẫu: nguyen_van_a");
        }

        log.info("🏁 Seed Data hoàn tất!");
    }
}
