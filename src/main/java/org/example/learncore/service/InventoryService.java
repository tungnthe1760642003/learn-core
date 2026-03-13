package org.example.learncore.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learncore.model.Inventory;
import org.example.learncore.repository.InventoryRepository;
import org.example.learncore.dto.ErrorCode;
import org.example.learncore.exception.AppException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final EntityManager entityManager;

    @Transactional
    public void resetInventory(Integer initialStock) {
        Inventory item = inventoryRepository.findAll().stream().findFirst().orElse(new Inventory());
        item.setName("iPhone 15 Pro Max");
        item.setStock(initialStock);
        inventoryRepository.save(item);
        log.info("[Pessimistic] Đã reset Inventory với stock {}", initialStock);
    }

    @Transactional
    public void purchasePessimistic(Integer quantity) throws InterruptedException {
        log.info("[Pessimistic] Transaction BẮT ĐẦU...");

        // 1. Tìm ID của sản phẩm đầu tiên (không lock, chỉ lấy ID)
        Long productId = inventoryRepository.findAll().stream()
                .findFirst()
                .map(Inventory::getId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND));

        // 2. Thực hiện LOCK dựa trên ID vừa tìm được
        Inventory item = inventoryRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND));

        // 3. QUAN TRỌNG: Ép Hibernate refresh lại dữ liệu từ DB để tránh L1 cache cũ
        entityManager.refresh(item);

        log.info("[Pessimistic] Đã LOCK sản phẩm ID: {}. Tồn kho ĐỌC ĐƯỢC: {}", item.getId(), item.getStock());

        Thread.sleep(5000);

        if (item.getStock() >= quantity) {
            item.setStock(item.getStock() - quantity);
            inventoryRepository.save(item);
            log.info("[Pessimistic] Mua hàng thành công. Tồn kho mới: {}", item.getStock());
        } else {
            log.warn("[Pessimistic] Hết hàng!");
        }
    }
}
