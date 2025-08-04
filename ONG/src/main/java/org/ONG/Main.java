package org.ONG;

import org.ONG.dto.CollectedByCategoryStatusDTO;
import org.ONG.dto.CollectedByTypeDTO;
import org.ONG.dto.NewAssignmentDTO;
import org.ONG.dto.NewDonationDTO;
import org.ONG.enums.DonorType;
import org.ONG.service.ONG;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final ONG service = ONG.getInstance();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            showMenu();
            int option = sc.nextInt();

            switch (option) {
                case 1 -> newDonation();
                case 2 -> newAssignment();
                case 3 -> getCollectedByDonorType();
                case 4 -> getCollectedByCategoryAndStatus();
                case 5 -> exit = true;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private static void showMenu() {
        System.out.println("Menu");
        System.out.println("1. New donation");
        System.out.println("2. New assignment");
        System.out.println("3. Get total collected by donor type");
        System.out.println("4. Get collected by category and status");
        System.out.println("5. Exit");
    }

    private static void newDonation() {
        System.out.println("New donation");
        sc.nextLine();

        System.out.print("Donor name: ");
        String name = sc.nextLine();

        System.out.print("Donor type (INDIVIDUAL/COMPANY): ");
        DonorType donorType = DonorType.valueOf(sc.nextLine().toUpperCase());

        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(sc.nextLine());

        System.out.print("Category: ");
        String category = sc.nextLine();

        try {
            service.newDonation(new NewDonationDTO(
                    name,
                    donorType,
                    amount,
                    LocalDate.now(),
                    category
            ));

            System.out.println("New donation has been saved successfully");
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void newAssignment() {
        System.out.println("New assignment");
        sc.nextLine();

        System.out.print("Donation id: ");
        long id = sc.nextLong();

        System.out.print("Notes: ");
        String notes = sc.nextLine();

        try {
            service.newAssignment(new NewAssignmentDTO(
                    id,
                    LocalDate.now(),
                    notes
            ));

            System.out.println("New assignment has been saved successfully");
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void getCollectedByDonorType() {
        for (CollectedByTypeDTO dto : service.getTotalCollectedByDonorType()) {
            System.out.println("Type: " + dto.getDonorType());
            System.out.println("Quantity: " + dto.getQuantity());
            System.out.println("Total: " + dto.getTotal());
        }
    }

    private static void getCollectedByCategoryAndStatus() {
        for (CollectedByCategoryStatusDTO dto : service.getCollectedByCategoryAndStatus()) {
            System.out.println("Category: " + dto.getCategory());
            System.out.println("Count assigned: " + dto.getQuantityAssigned());
            System.out.println("Count received: " + dto.getQuantityReceived());
            System.out.println("Total: " + dto.getTotal());
        }
    }
}