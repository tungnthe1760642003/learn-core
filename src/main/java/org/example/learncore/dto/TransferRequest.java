package org.example.learncore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    @NotNull(message = "ID người gửi không được để trống")
    private Long fromId;

    @NotNull(message = "ID người nhận không được để trống")
    private Long toId;

    @Min(value = 1, message = "Số tiền chuyển tối thiểu là 1$")
    @NotNull(message = "Số tiền không được để trống")
    private Double amount;
}
