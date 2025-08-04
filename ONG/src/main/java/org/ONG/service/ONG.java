package org.ONG.service;

import org.ONG.enums.Status;
import org.ONG.models.*;
import org.ONG.dto.*;
import org.ONG.utils.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDate;

public final class ONG {
    private static volatile ONG instance;

    private ONG() {}

    public static ONG getInstance() {
        if (instance == null) {
            synchronized (ONG.class) {
                if (instance == null) {
                    instance = new ONG();
                }
            }
        }
        return instance;
    }

    public Donation newDonation(NewDonationDTO dto) {
        Donation donation = new Donation(
                dto.getDonorName(),
                dto.getDonorType(),
                dto.getAmount(),
                LocalDate.now(),
                dto.getCategory(),
                Status.RECEIVED
        );

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            session.persist(donation);
            session.getTransaction().commit();
        }

        return donation;
    }

    public Assignment newAssignment(NewAssignmentDTO dto) {
        Donation donation;

        try (Session session = HibernateUtil.getSession()) {
            donation = session.get(Donation.class, dto.getDonationId());

            if (donation == null) throw new IllegalArgumentException("Donation not found");
        }

        if (donation.getStatus() == Status.ASSIGNED) throw new RuntimeException("Assignment already assigned");

        Assignment assignment = new Assignment(
                donation,
                dto.getNotes(),
                dto.getDate()
        );

        donation.setStatus(Status.ASSIGNED);

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            session.persist(assignment);
            session.merge(donation);
            session.getTransaction().commit();
        }

        return assignment;
    }
}
