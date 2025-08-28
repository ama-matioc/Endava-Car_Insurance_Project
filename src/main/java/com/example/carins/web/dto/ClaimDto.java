package com.example.carins.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ClaimDto {
    @NotNull(message = "claimDate is required")
    public LocalDate claimDate;
    @NotBlank(message = "description is required")
    public String description;
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "must be positive")
    public BigDecimal amount;
}
