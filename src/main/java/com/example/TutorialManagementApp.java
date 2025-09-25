package com.example;

import com.example.dao.TutorialDAO;
import com.example.dao.TutorialDAOImpl;
import com.example.exceptions.DatabaseOperationException;
import com.example.exceptions.TutorialNotFoundException;
import com.example.model.Tutorial;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TutorialManagementApp {

    private static TutorialDAO tutorialDAO = new TutorialDAOImpl();
    private static Scanner scanner = new Scanner(System.in);
    static void main(String[] args) {
        int choice;
        do {
            displayMenu();
            choice = getUserChoice();

            try {
                switch (choice) {
                    case 1:
                        addTutorial();
                        break;
                    case 2:
                        viewAllTutorials();
                        break;
                    case 3:
                        viewTutorialById();
                        break;
                    case 4:
                        updateTutorial();
                        break;
                    case 5:
                        deleteTutorial();
                        break;
                    case 0:
                        System.out.println("Exiting Tutorial Management System.Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (DatabaseOperationException e) {
                System.err.println("Database Error: " + e.getMessage());
                // Optionally log the full stack trace for debugging
// e.printStackTrace();
            } catch (TutorialNotFoundException e) {
                System.err.println("Error: " + e.getMessage());
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter the correct data type.");
                        scanner.nextLine(); // Clear the invalid input from the scanner
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " +
                        e.getMessage());
                // e.printStackTrace();
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Consume the newline left by nextInt() or clear previous input
        } while (choice != 0);
        scanner.close();
    }
    private static void displayMenu() {
        System.out.println("\n--- Tutorial Management System ---");
        System.out.println("1. Add New Tutorial");
        System.out.println("2. View All Tutorials");
        System.out.println("3. View Tutorial by ID");
        System.out.println("4. Update Tutorial");
        System.out.println("5. Delete Tutorial");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
    private static int getUserChoice() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Consume the invalid input
            return -1; // Return an invalid choice
        } finally {
            scanner.nextLine(); // Consume the newline character left after nextInt()
        }
    }
    private static void addTutorial() throws DatabaseOperationException {
        System.out.println("\n--- Add New Tutorial ---");
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        System.out.print("Enter URL: ");
        String url = scanner.nextLine();
        System.out.print("Enter Published Date (YYYY-MM-DD, leave blank if unknown): ");
        String dateStr = scanner.nextLine();
        LocalDate publishedDate = null;
        if (!dateStr.trim().isEmpty()) {
            try {
                publishedDate = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date format. Date will be set to null.");
            }
        }
        Tutorial newTutorial = new Tutorial(title, author, url, publishedDate);
        tutorialDAO.addTutorial(newTutorial);
        System.out.println("Tutorial added successfully! ID: " +
                newTutorial.getId());
    }
    private static void viewAllTutorials() throws
            DatabaseOperationException {
        System.out.println("\n--- All Tutorials ---");
        ArrayList<Tutorial> tutorials = tutorialDAO.getAllTutorials();
        if (tutorials.isEmpty()) {
            System.out.println("No tutorials found.");
        } else {
            for (Tutorial t : tutorials) {
                System.out.println(t);
            }
        }
    }
    private static void viewTutorialById() throws
            TutorialNotFoundException, DatabaseOperationException {
        System.out.println("\n--- View Tutorial by ID ---");
        System.out.print("Enter Tutorial ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        Tutorial tutorial = tutorialDAO.getTutorialById(id);
        System.out.println("Found Tutorial: " + tutorial);
    }
    private static void updateTutorial() throws TutorialNotFoundException,
            DatabaseOperationException {
        System.out.println("\n--- Update Tutorial ---");
        System.out.print("Enter Tutorial ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        Tutorial existingTutorial = tutorialDAO.getTutorialById(id);
        System.out.println("Existing Tutorial: " + existingTutorial);
        System.out.print("Enter New Title (leave blank to keep current: '" +
                existingTutorial.getTitle() + "'): ");
        String newTitle = scanner.nextLine();
        if (!newTitle.trim().isEmpty()) {
            existingTutorial.setTitle(newTitle);
        }
        System.out.print("Enter New Author (leave blank to keep current: '"
                + existingTutorial.getAuthor() + "'): ");
        String newAuthor = scanner.nextLine();
        if (!newAuthor.trim().isEmpty()) {
            existingTutorial.setAuthor(newAuthor);
        }
        System.out.print("Enter New URL (leave blank to keep current: '" +
                existingTutorial.getUrl() + "'): ");
        String newUrl = scanner.nextLine();
        if (!newUrl.trim().isEmpty()) {
            existingTutorial.setUrl(newUrl);
        }
        System.out.print("Enter New Published Date (YYYY-MM-DD, leave blank to keep current: " + existingTutorial.getPublishedDate() + "): ");
        String newDateStr = scanner.nextLine();
        if (!newDateStr.trim().isEmpty()) {
            try {
                existingTutorial.setPublishedDate(LocalDate.parse(newDateStr));
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date format. Keeping current date.");
            }
        }
        tutorialDAO.updateTutorial(existingTutorial);
        System.out.println("Tutorial updated successfully!");
    }
    private static void deleteTutorial() throws TutorialNotFoundException,
            DatabaseOperationException {
        System.out.println("\n--- Delete Tutorial ---");
        System.out.print("Enter Tutorial ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        tutorialDAO.deleteTutorial(id);
        System.out.println("Tutorial with ID " + id + " deleted successfully!");
    }
}

