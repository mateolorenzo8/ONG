package org.ONG.dto;

import java.time.LocalDate;

public class NewAssignmentDTO {
    private long donationId;
    private LocalDate date;
    private String notes;

    public NewAssignmentDTO(long donationId, LocalDate date, String notes) {
        this.donationId = donationId;
        this.date = date;
        this.notes = notes;
    }

    public long getDonationId() {
        return donationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }
}
