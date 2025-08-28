package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.InsurancePolicyDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class InsurancePolicyController {
    @Autowired
    InsurancePolicyRepository policyRepo;
    @Autowired
    CarRepository carRepo;

    @PostMapping
    public ResponseEntity<?> createPolicy(@Valid @RequestBody InsurancePolicyDto dto) {
        try {
            Car car = carRepo.findById(dto.carId).orElse(null);
            if (car == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Car not found"));
            }
            InsurancePolicy policy = new InsurancePolicy(car, dto.provider, dto.startDate, dto.endDate);
            policy = policyRepo.save(policy);
            return ResponseEntity.ok(new PolicyResponse(policy.getId(), policy.getCar().getId(),
                    policy.getProvider(), policy.getStartDate().toString(), policy.getEndDate().toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error creating policy: " + e.getMessage()));
        }
    }

    public record PolicyResponse(Long id, Long carId, String provider, String startDate, String endDate) {
    }

    public record ErrorResponse(String message) {
    }
}
