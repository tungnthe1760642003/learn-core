package org.example.learncore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learncore.repository.ProductNoIndexRepository;
import org.example.learncore.repository.ProductWithIndexRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexPerformanceService {

    private final ProductNoIndexRepository noIndexRepo;
    private final ProductWithIndexRepository withIndexRepo;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public String seedData(int count) {
        log.info("🚀 Bắt đầu nạp {} bản ghi vào mỗi bảng...", count);
        
        // Xoá dữ liệu cũ
        noIndexRepo.deleteAllInBatch();
        withIndexRepo.deleteAllInBatch();

        // Dùng Batch Insert để nhanh hơn (vì chúng ta đang test Index chứ không phải test tốc độ Insert)
        String sqlNo = "INSERT INTO product_no_index (sku, name) VALUES (?, ?)";
        String sqlWith = "INSERT INTO product_with_index (sku, name) VALUES (?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String sku = "PROD-" + i;
            String name = "Product Name " + i;
            batchArgs.add(new Object[]{sku, name});
            
            if (i % 1000 == 0) {
                jdbcTemplate.batchUpdate(sqlNo, batchArgs);
                jdbcTemplate.batchUpdate(sqlWith, batchArgs);
                batchArgs.clear();
                log.info("Đã nạp {}/{} bản ghi...", i, count);
            }
        }
        
        return "Đã nạp thành công " + count + " bản ghi vào mỗi bảng.";
    }

    public long benchmarkNoIndex(String sku) {
        long start = System.currentTimeMillis();
        noIndexRepo.findBySku(sku);
        return System.currentTimeMillis() - start;
    }

    public long benchmarkWithIndex(String sku) {
        long start = System.currentTimeMillis();
        withIndexRepo.findBySku(sku);
        return System.currentTimeMillis() - start;
    }

    public long benchmarkClustered(Long id) {
        long start = System.currentTimeMillis();
        withIndexRepo.findById(id);
        return System.currentTimeMillis() - start;
    }
}
