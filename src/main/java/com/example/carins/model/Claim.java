package com.example.carins.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "claim")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Car car;

    private LocalDate claimDate;
    private String description;
    private BigDecimal amount;

    public Claim() {
    }

    public Claim(Car car, LocalDate claimDate, String description, BigDecimal amount) {
        this.car = car;
        this.claimDate = claimDate;
        this.description = description;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public Car getCar() {
        return car;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
