package com.example.carins.service;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class PolicyExpiryLogger {
    private static final Logger log = LoggerFactory.getLogger(PolicyExpiryLogger.class);
    private final InsurancePolicyRepository policyRepo;
    private final Set<Long> loggedPolicies = new HashSet<>();

    public PolicyExpiryLogger(InsurancePolicyRepository policyRepo) {
        this.policyRepo = policyRepo;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void logExpiredPolicies() {
        LocalDateTime now = LocalDateTime.now();
        for (InsurancePolicy policy : policyRepo.findAll()) {
            if (policy.getEndDate() == null || loggedPolicies.contains(policy.getId()))
                continue;
            LocalDateTime expiry = policy.getEndDate().atTime(LocalTime.MIDNIGHT);
            if (expiry.isBefore(now) && expiry.plusDays(1).isAfter(now)) {
                log.info("Policy {} for car {} expired on {}", policy.getId(), policy.getCar().getId(),
                        policy.getEndDate());
                loggedPolicies.add(policy.getId());
            }
        }
    }
}
