package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.service.CarService;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.HistoryEvent;
import org.springframework.http.ResponseEntity;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.ClaimRepository;
import com.example.carins.model.Claim;
import com.example.carins.web.dto.ClaimDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;

    @Autowired
    private ClaimRepository claimRepo;
    @Autowired
    private CarRepository carRepo;
    @Autowired
    private InsurancePolicyRepository policyRepo;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        var car = carRepo.findById(carId).orElse(null);
        if (car == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Car not found"));
        }
        LocalDate d;
        try {
            d = LocalDate.parse(date);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid date format, use YYYY-MM-DD"));
        }
        if (d.getYear() < 1900 || d.getYear() > 2100) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Date out of supported range (1900-2100)"));
        }
        boolean valid = service.isInsuranceValid(carId, d);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));
    }

    @GetMapping("/cars/{carId}/history")
    public ResponseEntity<?> getCarHistory(@PathVariable Long carId) {
        var car = carRepo.findById(carId).orElse(null);
        if (car == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Car not found"));
        }
        var claims = claimRepo.findAll().stream()
                .filter(c -> c.getCar().getId().equals(carId))
                .map(HistoryEvent::fromClaim)
                .toList();
        var policies = policyRepo.findByCarId(carId).stream()
                .map(HistoryEvent::fromPolicy)
                .toList();
        var allEvents = new java.util.ArrayList<HistoryEvent>();
        allEvents.addAll(claims);
        allEvents.addAll(policies);
        allEvents.sort(java.util.Comparator.comparing(e -> e.date));
        return ResponseEntity.ok(new CarHistoryResponse(carId, allEvents));
    }

    public record CarHistoryResponse(Long carId, java.util.List<HistoryEvent> events) {
    }

    @PostMapping("/cars/{carId}/claims")
    public ResponseEntity<?> registerClaim(@PathVariable Long carId, @Valid @RequestBody ClaimDto dto) {
        var car = carRepo.findById(carId).orElse(null);
        if (car == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Car not found"));
        }
        Claim claim = new Claim(car, dto.claimDate, dto.description, dto.amount);
        claim = claimRepo.save(claim);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(claim.getId()).toUri();
        return ResponseEntity.created(location).body(new ClaimResponse(claim));
    }

    @GetMapping("/cars/{carId}/claims")
    public ResponseEntity<?> getClaimsForCar(@PathVariable Long carId) {
        var car = carRepo.findById(carId).orElse(null);
        if (car == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Car not found"));
        }
        var claims = claimRepo.findAll().stream()
                .filter(c -> c.getCar().getId().equals(carId))
                .map(ClaimResponse::new)
                .toList();
        return ResponseEntity.ok(claims);
    }

    public record ClaimResponse(Long id, Long carId, String claimDate, String description, String amount) {
        public ClaimResponse(Claim c) {
            this(c.getId(), c.getCar().getId(), c.getClaimDate().toString(), c.getDescription(),
                    c.getAmount().toPlainString());
        }
    }

    public record ErrorResponse(String message) {
    }

    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {
    }
}
