package org.example.learncore.controller;

import lombok.RequiredArgsConstructor;
import org.example.learncore.dto.ApiResponse;
import org.example.learncore.dto.TransferRequest;
import org.example.learncore.service.BankService;
import org.example.learncore.service.InventoryService;
import org.example.learncore.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class BankingDemoController {

    private final InventoryService inventoryService;
    private final UserProfileService userProfileService;
    private final BankService bankService;

    @PostMapping("/reset")
    public ApiResponse<String> resetData() {
        inventoryService.resetInventory(10);
        userProfileService.resetProfile();
        return ApiResponse.success("Dữ liệu đã được reset: Inventory (10 items), Profile (Bio: Java Dev)");
    }

    /**
     * Test Pessimistic Lock: Transaction 2 sẽ bị BLOCK (chờ)
     */
    @PostMapping("/pessimistic/purchase")
    public ApiResponse<String> purchase(@RequestParam("quantity") Integer quantity) throws InterruptedException {
        inventoryService.purchasePessimistic(quantity);
        return ApiResponse.success("Mua hàng thành công (Pessimistic)");
    }

    /**
     * Test Optimistic Lock: Cả 2 cùng chạy, nhưng người thứ 2 sẽ FAIL (409 Conflict)
     */
    @PostMapping("/optimistic/update-bio")
    public ApiResponse<String> updateBio(@RequestParam("bio") String bio) throws InterruptedException {
        userProfileService.updateBioOptimistic(bio);
        return ApiResponse.success("Update Bio thành công (Optimistic)");
    }

    /**
     * DEMO TRANSACTION: Khởi tạo tài khoản
     */
    @PostMapping("/transaction/init")
    public ApiResponse<String> initBank() {
        bankService.initAccounts();
        return ApiResponse.success("Đã khởi tạo User A (1000) và User B (500)");
    }

    /**
     * DEMO TRANSACTION: Chuyển tiền (Có thể giả lập rollback)
     */
    @PostMapping("/transaction/transfer")
    public ApiResponse<String> transfer(@RequestBody @Valid TransferRequest request) {
        bankService.transferMoney(request.getFromId(), request.getToId(), request.getAmount());
        return ApiResponse.success("Chuyển tiền THÀNH CÔNG!");
    }

    /**
     * DEMO TRANSACTION: Chuyển tiền (Thủ công - Hiện rõ Commit/Rollback)
     */
    @PostMapping("/transaction/manual-transfer")
    public ApiResponse<String> manualTransfer(
            @RequestParam("amount") Double amount,
            @RequestParam(value = "fail", defaultValue = "false") boolean fail) {
        bankService.manualTransfer(1L, 2L, amount, fail);
        return ApiResponse.success("Chuyển tiền THỦ CÔNG THÀNH CÔNG!");
    }
}
