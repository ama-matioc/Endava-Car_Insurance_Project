package com.example.carins.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class InsurancePolicyDto {
    @NotNull(message = "carId is required")
    public Long carId;
    public String provider;
    @NotNull(message = "startDate is required")
    public LocalDate startDate;
    @NotNull(message = "endDate is required")
    public LocalDate endDate;
}
