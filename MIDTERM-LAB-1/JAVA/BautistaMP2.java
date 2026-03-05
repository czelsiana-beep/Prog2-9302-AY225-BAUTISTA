// BAUTISTA_MP2.java
// Monthly Performance Analyzer
// Machine Problem #2 - Prog2-9302-AY225-FABREGAS
// University of Perpetual Help System DALTA - Molino Campus

import java.util.*;
import java.io.*;

public class BautistaMP2 {

    static final String[] MONTH_NAMES = {
        "", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("       MONTHLY PERFORMANCE ANALYZER        ");
        System.out.println("       Video Game Sales 2024 Dataset        ");
        System.out.println("============================================\n");

        File file = getValidFile();
        List<DataRecord> records = loadDataset(file);

        if (records == null || records.isEmpty()) {
            System.out.println("No valid records found in the dataset.");
            return;
        }

        System.out.println("Records loaded: " + records.size() + "\n");
        displayMonthlySummary(records);
    }

    public static File getValidFile() {
        try (Scanner input = new Scanner(System.in)) {
            File file = null;

            while (true) {
                System.out.print("Enter dataset file path: ");
                String path = input.nextLine().trim();

                try {
                    file = new File(path);

                    if (!file.exists()) {
                        System.out.println("Error: File does not exist. Please try again.\n");
                        continue;
                    }
                    if (!file.canRead()) {
                        System.out.println("Error: File is not readable. Please try again.\n");
                        continue;
                    }
                    if (!file.getName().toLowerCase().endsWith(".csv")) {
                        System.out.println("Error: File must be a CSV file (.csv). Please try again.\n");
                        continue;
                    }
                    if (!hasValidCSVFormat(file)) {
                        System.out.println("Error: File is not a valid Video Game Sales CSV. Please try again.\n");
                        continue;
                    }

                    System.out.println("File found and validated. Processing...\n");
                    break;

                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage() + ". Please try again.\n");
                }
            }

            return file;
        }
    }

    public static boolean hasValidCSVFormat(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null) return false;
            String lower = headerLine.toLowerCase();
            return lower.contains("total_sales") && lower.contains("release_date");
        } catch (IOException e) {
            return false;
        }
    }

    public static List<DataRecord> loadDataset(File file) {
        List<DataRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null) return records;

            String[] headers   = parseCSVLine(headerLine);
            int titleIdx       = findIndex(headers, "title");
            int consoleIdx     = findIndex(headers, "console");
            int genreIdx       = findIndex(headers, "genre");
            int publisherIdx   = findIndex(headers, "publisher");
            int totalSalesIdx  = findIndex(headers, "total_sales");
            int releaseDateIdx = findIndex(headers, "release_date");

            String line;
            int lineNum = 1;

            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] cols = parseCSVLine(line);
                    int maxIdx = Math.max(totalSalesIdx, releaseDateIdx);
                    if (cols.length <= maxIdx) continue;

                    String releaseDate = getCol(cols, releaseDateIdx);
                    String salesStr    = getCol(cols, totalSalesIdx);

                    if (releaseDate.isEmpty() || salesStr.isEmpty()) continue;

                    double sales = Double.parseDouble(salesStr);

                    DataRecord record = new DataRecord(
                        getCol(cols, titleIdx),
                        getCol(cols, consoleIdx),
                        getCol(cols, genreIdx),
                        getCol(cols, publisherIdx),
                        sales,
                        releaseDate
                    );

                    records.add(record);

                } catch (NumberFormatException e) {
                    // Skip rows with invalid numeric data
                } catch (Exception e) {
                    System.out.println("Warning: Skipping line " + lineNum + " - " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return records;
    }

    public static void displayMonthlySummary(List<DataRecord> records) {
        Map<Integer, Double> monthlySales = new TreeMap<>();

        for (DataRecord r : records) {
            int month = r.getMonth();
            if (month < 1 || month > 12) continue;
            monthlySales.put(month, monthlySales.getOrDefault(month, 0.0) + r.getTotalSales());
        }

        if (monthlySales.isEmpty()) {
            System.out.println("No monthly data available.");
            return;
        }

        System.out.println("============================================");
        System.out.println("    MONTHLY SALES SUMMARY (in millions)    ");
        System.out.println("============================================");
        System.out.printf("%-15s %15s%n", "Month", "Total Sales (M)");
        System.out.println("--------------------------------------------");

        int bestMonth = -1;
        double bestSales = -1;

        for (Map.Entry<Integer, Double> entry : monthlySales.entrySet()) {
            int month = entry.getKey();
            double sales = entry.getValue();
            System.out.printf("%-15s %15.2f%n", MONTH_NAMES[month], sales);
            if (sales > bestSales) {
                bestSales = sales;
                bestMonth = month;
            }
        }

        System.out.println("============================================");
        System.out.printf("%n>>> Best-Performing Month : %s%n", MONTH_NAMES[bestMonth]);
        System.out.printf(">>> Total Sales           : %.2f million units%n", bestSales);
        System.out.println("============================================");
    }

    public static int findIndex(String[] headers, String name) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    public static String getCol(String[] cols, int idx) {
        if (idx < 0 || idx >= cols.length) return "";
        return cols[idx].trim();
    }

    public static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(ch);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
}