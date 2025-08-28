package com.example.carins;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.CarService;
import com.example.carins.web.CarController;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

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
    @Autowired
    CarController carController;

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

    @Test
    void insuranceValidityReturns404ForNonExistentCar() {
        var response = carController.isInsuranceValid(999L, "2025-01-01");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void insuranceValidityReturns400ForInvalidDateFormat() {
        var response = carController.isInsuranceValid(1L, "invalid-date");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void insuranceValidityReturns400ForDateOutOfRange() {
        var response = carController.isInsuranceValid(1L, "1800-01-01");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void carHistoryReturns404ForNonExistentCar() {
        var response = carController.getCarHistory(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
