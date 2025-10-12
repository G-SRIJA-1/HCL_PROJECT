package com.example.kb;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.*;

public class KnowledgeBaseSystem {
    private static MongoDatabase getDatabase() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        return mongoClient.getDatabase("knowledge_base");
    }

    public static void main(String[] args) {
        MongoDatabase db = getDatabase();
        MongoCollection<Document> collection = db.getCollection("faqs");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Knowledge Base System ---");
            System.out.println("1. Add FAQ");
            System.out.println("2. View All FAQs");
            System.out.println("3. Search FAQ by Question");
            System.out.println("4. Delete FAQ");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int ch = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (ch) {
                case 1:
                    addFAQ(collection, sc);
                    break;
                case 2:
                    viewAllFAQs(collection);
                    break;
                case 3:
                    searchFAQ(collection, sc);
                    break;
                case 4:
                    deleteFAQ(collection, sc);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void addFAQ(MongoCollection<Document> collection, Scanner sc) {
        System.out.print("Enter Question: ");
        String question = sc.nextLine();
        System.out.print("Enter Answer: ");
        String answer = sc.nextLine();
        System.out.print("Enter Category (HR/IT/Finance/General): ");
        String category = sc.nextLine();

        Document faq = new Document("question", question)
                .append("answer", answer)
                .append("category", category)
                .append("date_added", new Date());

        collection.insertOne(faq);
        System.out.println("✅ FAQ Added Successfully!");
    }

    private static void viewAllFAQs(MongoCollection<Document> collection) {
        System.out.println("\n--- All FAQs ---");
        for (Document doc : collection.find()) {
            System.out.println("Category: " + doc.getString("category"));
            System.out.println("Question: " + doc.getString("question"));
            System.out.println("Answer: " + doc.getString("answer"));
            System.out.println("Date Added: " + doc.getDate("date_added"));
            System.out.println("---------------------------");
        }
    }

    private static void searchFAQ(MongoCollection<Document> collection, Scanner sc) {
        System.out.print("Enter keyword to search in questions: ");
        String keyword = sc.nextLine();

        FindIterable<Document> results = collection.find(Filters.regex("question", ".*" + keyword + ".*", "i"));
        boolean found = false;
        for (Document doc : results) {
            System.out.println("Category: " + doc.getString("category"));
            System.out.println("Question: " + doc.getString("question"));
            System.out.println("Answer: " + doc.getString("answer"));
            System.out.println("---------------------------");
            found = true;
        }
        if (!found) {
            System.out.println("No matching FAQ found.");
        }
    }

    private static void deleteFAQ(MongoCollection<Document> collection, Scanner sc) {
        System.out.print("Enter the exact question to delete: ");
        String question = sc.nextLine();

        collection.deleteOne(Filters.eq("question", question));
        System.out.println("FAQ deleted successfully (if it existed).");
    }
}
