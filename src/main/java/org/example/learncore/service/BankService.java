package org.example.learncore.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learncore.model.BankAccount;
import org.example.learncore.repository.BankAccountRepository;
import org.example.learncore.dto.ErrorCode;
import org.example.learncore.exception.AppException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankService {

    private final BankAccountRepository bankAccountRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManager entityManager;

    @Transactional
    public void initAccounts() {
        bankAccountRepository.deleteAll();
        // User A: Bình thường
        bankAccountRepository.save(new BankAccount(null, "User A", 1000.0, false));
        // User B: Bị khoá
        bankAccountRepository.save(new BankAccount(null, "User B (BLOCKED)", 500.0, true));
        log.info("✅ Khởi tạo: User A (1000), User B (500 - ĐÃ KHOÁ)");
    }

    /**
     * DEMO TRANSACTION: Chuyển tiền
     * Nếu shouldFail = true, hệ thống sẽ lỗi sau khi trừ tiền A nhưng trước khi cộng tiền B.
     */
    @Transactional(rollbackFor = Exception.class)
    public void transferMoney(Long fromId, Long toId, Double amount) {
        log.info("🚀 Giao dịch: Chuyển {} từ ID {} -> ID {}", amount, fromId, toId);

        // 1. CHIẾM KHÓA (LOCK) tài khoản người gửi ngay lập tức
        BankAccount fromAccount = bankAccountRepository.findByIdWithLock(fromId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND));
        
        // Refresh để tránh dữ liệu cũ trong cache
        entityManager.refresh(fromAccount);

        if (fromAccount.getBalance() < amount) {
            log.error("❌ Số dư không đủ! (A: {}, Cần: {})", fromAccount.getBalance(), amount);
            throw new AppException(ErrorCode.INSUFFICIENT_FUNDS);
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        bankAccountRepository.save(fromAccount);
        log.info("DONE STEP 1: Đã khóa và trừ tiền từ A.");

        // 2. CHIẾM KHÓA (LOCK) người nhận
        BankAccount toAccount = bankAccountRepository.findByIdWithLock(toId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND));
        
        entityManager.refresh(toAccount);

        if (toAccount.isBlocked()) {
            log.error("❌ Tài khoản đích {} đang bị KHOÁ!", toId);
            throw new AppException(ErrorCode.ACCOUNT_BLOCKED);
        }

        // 3. Cộng tiền người nhận
        toAccount.setBalance(toAccount.getBalance() + amount);
        bankAccountRepository.save(toAccount);
        
        log.info("DONE STEP 2: Đã cộng tiền cho B. Giao dịch THÀNH CÔNG.");
    }

    /**
     * DEMO TRANSACTION THỦ CÔNG (Manual): 
     * Bạn sẽ thấy rõ lệnh BEGIN, COMMIT và ROLLBACK bằng code.
     */
    public void manualTransfer(Long fromId, Long toId, Double amount, boolean shouldFail) {
        log.info("🛠️ Bắt đầu giao dịch THỦ CÔNG...");

        // 1. Tự tay mở một Transaction (BEGIN)
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            // Tương đương bước 1: Trừ tiền A
            BankAccount fromAccount = bankAccountRepository.findById(fromId)
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND));
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            bankAccountRepository.save(fromAccount);
            log.info("[Manual] Đã trừ tiền A.");

            if (shouldFail) {
                throw new RuntimeException("Lỗi giả lập!");
            }

            // Tương đương bước 2: Cộng tiền B
            BankAccount toAccount = bankAccountRepository.findById(toId)
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND));
            toAccount.setBalance(toAccount.getBalance() + amount);
            bankAccountRepository.save(toAccount);
            log.info("[Manual] Đã cộng tiền B.");

            // 2. Mọi thứ ổn -> Tự tay gọi COMMIT
            transactionManager.commit(status);
            log.info("✅ Đã gọi lệnh COMMIT thành công!");

        } catch (Exception e) {
            // 3. Có lỗi -> Tự tay gọi ROLLBACK
            transactionManager.rollback(status);
            log.error("❌ Đã gọi lệnh ROLLBACK do có lỗi: {}", e.getMessage());
            throw e; // Ném tiếp lỗi ra để Controller biết
        }
    }
}
