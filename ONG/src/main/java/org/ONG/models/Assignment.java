package org.ONG.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "assignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "donation_id")
    private Donation donation;

    @Column(name = "notes")
    private String notes;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    public Assignment() {}

    public Assignment(Donation donation, String notes, LocalDate assignedDate) {
        this.donation = donation;
        this.notes = notes;
        this.assignedDate = assignedDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }
}
