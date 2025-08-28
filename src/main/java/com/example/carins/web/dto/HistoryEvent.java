package com.example.carins.web.dto;

import com.example.carins.model.Claim;
import com.example.carins.model.InsurancePolicy;

public class HistoryEvent {
    public String type;
    public String date;
    public String description;
    public String extra;

    public HistoryEvent(String type, String date, String description, String extra) {
        this.type = type;
        this.date = date;
        this.description = description;
        this.extra = extra;
    }

    public static HistoryEvent fromClaim(Claim c) {
        return new HistoryEvent("claim", c.getClaimDate().toString(),
                c.getDescription(), c.getAmount().toPlainString());
    }

    public static HistoryEvent fromPolicy(InsurancePolicy p) {
        return new HistoryEvent("policy", p.getStartDate().toString(),
                p.getProvider(), "valid: " + p.getStartDate() + " to " + p.getEndDate());
    }
}
