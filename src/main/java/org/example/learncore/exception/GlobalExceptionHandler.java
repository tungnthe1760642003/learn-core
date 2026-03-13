package org.example.learncore.exception;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.example.learncore.dto.ApiResponse;
import org.example.learncore.dto.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Catch Custom Business Exceptions
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // 2. Catch Optimistic Locking Exceptions (Spring specific)
    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    ResponseEntity<ApiResponse<?>> handlingOptimisticLockingFailureException(ObjectOptimisticLockingFailureException exception) {
        ErrorCode errorCode = ErrorCode.OPTIMISTIC_LOCK_STALE;
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // 3. Catch Validation Exceptions
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldError().getDefaultMessage();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(ErrorCode.INVALID_KEY.getCode()) // Or define a new VALIDATION_ERROR code
                .message(message)
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 4. Catch All Other Unhandled Exceptions
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException exception) {
        log.error("💥 Unhandled Exception: ", exception);
        
        // Gửi báo động lỗi này lên Sentry (Production Monitoring)
        Sentry.captureException(exception);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .build();

        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(apiResponse);
    }
}
