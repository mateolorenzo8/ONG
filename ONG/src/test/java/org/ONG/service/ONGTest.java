package org.ONG.service;

import org.ONG.dto.CollectedByCategoryStatusDTO;
import org.ONG.dto.CollectedByTypeDTO;
import org.ONG.dto.NewAssignmentDTO;
import org.ONG.dto.NewDonationDTO;
import org.ONG.enums.DonorType;
import org.ONG.enums.Status;
import org.ONG.models.Assignment;
import org.ONG.models.Donation;
import org.ONG.utils.HibernateUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ONGTest {
    Session session;
    ONG service;
    Donation donationAssigned;
    Donation donationReceived;

    @BeforeEach
    void setUp() {
        session = HibernateUtil.getSession();
        service = ONG.getInstance();

        donationAssigned = new Donation(
                "Mateo",
                DonorType.INDIVIDUAL,
                new BigDecimal("10000"),
                LocalDate.now(),
                "Educacion",
                Status.ASSIGNED
        );

        donationReceived = new Donation(
                "Mateo",
                DonorType.INDIVIDUAL,
                new BigDecimal("10000"),
                LocalDate.now(),
                "Educacion",
                Status.RECEIVED
        );

        session.beginTransaction();
        session.persist(donationAssigned);
        session.persist(donationReceived);
        session.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (session != null && session.isOpen()) {
            session.beginTransaction();
            session.createQuery("delete from Assignment").executeUpdate();
            session.createQuery("delete from Donation").executeUpdate();
            session.getTransaction().commit();
            session.close();
        }
    }

    @Test
    void testGetInstance() {
        ONG instance = ONG.getInstance();
        ONG instance2 = ONG.getInstance();

        assertSame(instance, instance2);
    }

    @Test
    void testNewDonation() {
        Donation res = service.newDonation(new NewDonationDTO(
                "Mateo",
                DonorType.INDIVIDUAL,
                new BigDecimal("10000"),
                LocalDate.now(),
                "Educacion"
        ));

        assertNotNull(res);
        assertEquals("Mateo", res.getDonorName());
        assertEquals(DonorType.INDIVIDUAL, res.getDonorType());
    }

    @Test
    void testNewAssignmentUnexistingDonation() {
        assertThrows(IllegalArgumentException.class, () -> service.newAssignment(new NewAssignmentDTO(
                1000L,
                LocalDate.now(),
                "Lorem Ipsum"
        )));
    }

    @Test
    void testNewAssignmentAlreadyAssignedDonation() {
        assertThrows(RuntimeException.class, () -> service.newAssignment(new NewAssignmentDTO(
                donationAssigned.getId(),
                LocalDate.now(),
                "Lorem Ipsum"
        )));
    }

    @Test
    void testNewAssignmentSuccessfully() {
        Assignment res = service.newAssignment(new NewAssignmentDTO(
                donationReceived.getId(),
                LocalDate.now(),
                "Lorem Ipsum"
        ));

        assertNotNull(res);
        assertEquals(donationReceived.getId(), res.getDonation().getId());
        assertEquals(Status.ASSIGNED, res.getDonation().getStatus());
    }

    @Test
    void testGetTotalCollectedByDonorType() {
        List<CollectedByTypeDTO> res =  service.getTotalCollectedByDonorType();

        BigDecimal total = donationReceived.getAmount().add(donationAssigned.getAmount());

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(DonorType.INDIVIDUAL, res.get(0).getDonorType());
        assertEquals(2, res.get(0).getQuantity());
        assertEquals(0, res.get(0).getTotal().compareTo(total));
    }

    @Test
    void testGetCollectedByCategoryAndStatus() {
        List<CollectedByCategoryStatusDTO> res = service.getCollectedByCategoryAndStatus();

        BigDecimal total = donationReceived.getAmount().add(donationAssigned.getAmount());

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Educacion", res.get(0).getCategory());
        assertEquals(1, res.get(0).getQuantityReceived());
        assertEquals(1, res.get(0).getQuantityAssigned());
        assertEquals(0, res.get(0).getTotal().compareTo(total));
    }
}