package com.example.carins;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.CarService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarInsuranceApplicationTests {

    @Autowired
    CarService service;
    @Autowired
    CarRepository carRepo;
    @Autowired
    InsurancePolicyRepository policyRepo;

    @Test
    void insuranceValidityBasic() {
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-06-01")));
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2025-06-01")));
        assertFalse(service.isInsuranceValid(2L, LocalDate.parse("2025-02-01")));
    }

    @Test
    void creatingPolicyWithoutEndDateFails() {
        Car car = carRepo.findById(1L).orElseThrow();
        InsurancePolicy policy = new InsurancePolicy(car, "TestProvider", LocalDate.now(), null);
        assertThrows(ConstraintViolationException.class, () -> {
            policyRepo.saveAndFlush(policy);
        }, "endDate is required");
    }
}
