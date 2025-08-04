package org.ONG.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.ONG.enums.Status;
import org.ONG.models.*;
import org.ONG.dto.*;
import org.ONG.utils.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    public List<CollectedByTypeDTO> getTotalCollectedByDonorType() {
        try (Session session = HibernateUtil.getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<CollectedByTypeDTO> cq = cb.createQuery(CollectedByTypeDTO.class);
            Root<Donation> root = cq.from(Donation.class);

            Expression<BigDecimal> totalAmount = cb.sum(root.get("amount"));

            cq.multiselect(
                    root.get("donorType"),
                    cb.count(root),
                    totalAmount
            ).groupBy(
                    root.get("donorType")
            ).orderBy(
                    cb.desc(totalAmount)
            );

            return session.createQuery(cq).getResultList();
        }

        public List<CollectedByCategoryStatusDTO> getCollectedByCategoryAndStatus() {
            try (Session session = HibernateUtil.getSession()) {
                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<CollectedByCategoryStatusDTO> cq = cb.createQuery(CollectedByCategoryStatusDTO.class);
                Root<Donation> root = cq.from(Donation.class);

                Expression<Long> countReceived = cb.sum(
                        cb.<Long>selectCase()
                                .when(cb.equal(root.get("status"), Status.RECEIVED), 1L)
                                .otherwise(0L)
                );

                Expression<Long> countAssigned = cb.sum(
                        cb.<Long>selectCase()
                                .when(cb.equal(root.get("status"), Status.ASSIGNED), 1L)
                                .otherwise(0L)
                );

                Expression<BigDecimal> totalAmount = cb.sum(root.get("amount"));

                cq.multiselect(
                        root.get("category"),
                        countReceived,
                        countAssigned,
                        totalAmount
                ).groupBy(
                        root.get("category")
                ).orderBy(
                        cb.desc(totalAmount)
                );
            }
        }
    }
}
