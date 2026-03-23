/**
 * ============================================================
 * Student   : Rinamae Czel Siana M. Bautista
 * Student ID: 252060651
 * Section   : 9302-AY225
 * Course    : Math 101 / Programming 2
 * Assignment: MP16 – Random Dataset Sampler
 * Date      : March 2026
 *
 * Description:
 *   Reads the Pearson VUE exam results CSV file, skips the
 *   6 metadata rows at the top, asks the user how many random
 *   rows to sample, then displays those randomly chosen rows
 *   in a formatted table (no duplicate rows per run).
 *
 * Dataset columns (Row 7 = header):
 *   Candidate | Student/Faculty/NTE | Column1 | Exam |
 *   Language  | Exam Date | Score | Result | Time Used
 * ============================================================
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class MP16_RandomSampler {

    // Number of metadata rows to skip before the real column header
    // Rows 1-6 are metadata/junk; Row 7 is the actual header
    private static final int SKIP_ROWS = 6;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================================");
        System.out.println("  MP16 - Random Dataset Sampler");
        System.out.println("  Student: Rinamae Czel Siana M. Bautista | 9302-AY225");
        System.out.println("  Dataset: Pearson VUE Exam Results");
        System.out.println("============================================================");

        // ── Step 1: Ask user for CSV file path ───────────────────────
        System.out.print("  Enter CSV file path: ");
        String filePath = scanner.nextLine().trim();

        // ── Step 2: Read and parse the CSV file ──────────────────────
        // 'dataRows' - all valid data rows loaded after the header
        List<String> dataRows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            int    lineNum    = 0;
            int    headerLine = SKIP_ROWS + 1; // line 7 is the real header

            while ((line = reader.readLine()) != null) {
                lineNum++;

                // Skip the first 6 metadata rows (school name, result count, etc.)
                if (lineNum < headerLine) continue;

                // Line 7 is the actual column header row
                if (lineNum == headerLine) {
                    continue;
                }

                // Skip completely blank rows (trailing empty line at end of file)
                if (line.trim().replace(",", "").isEmpty()) continue;

                // Add valid data rows to the list
                dataRows.add(line.replace("\r", ""));
            }

        } catch (IOException e) {
            System.out.println("\n  ERROR: Cannot read file - " + e.getMessage());
            System.out.println("  Please check the file path and try again.");
            scanner.close();
            return;
        }

        // ── Step 3: Validate that data was loaded ────────────────────
        int totalRows = dataRows.size();

        if (totalRows == 0) {
            System.out.println("\n  ERROR: No data rows found in the dataset.");
            scanner.close();
            return;
        }

        System.out.println("\n  Total exam records loaded: " + totalRows);

        // ── Step 4: Ask how many rows to sample ──────────────────────
        int sampleSize = 0;

        while (true) {
            System.out.print("  Enter number of random rows to sample (1-" + totalRows + "): ");
            String input = scanner.nextLine().trim();

            try {
                sampleSize = Integer.parseInt(input);
                if (sampleSize < 1 || sampleSize > totalRows) {
                    System.out.println("  Please enter a number between 1 and " + totalRows + ".");
                } else {
                    break; // valid input - exit loop
                }
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a whole number.");
            }
        }

        // ── Step 5: Shuffle and pick the first N rows ────────────────
        // Collections.shuffle() randomizes the order of all data rows.
        // Taking the first 'sampleSize' rows from the shuffled list
        // gives a random sample without any duplicate rows.
        Collections.shuffle(dataRows);
        List<String> sampled = dataRows.subList(0, sampleSize);

        // ── Step 6: Display the sampled rows ─────────────────────────
        System.out.println("\n============================================================");
        System.out.printf ("  RANDOM SAMPLE - %d of %d record(s)%n", sampleSize, totalRows);
        System.out.println("============================================================");

        // Print formatted column headers for readable output
        System.out.printf("  %-22s %-10s %-42s %-6s %-6s%n",
                "Candidate", "Type", "Exam", "Score", "Result");
        System.out.println("  " + "-".repeat(92));

        for (int i = 0; i < sampled.size(); i++) {
            // Parse each CSV row (handles quoted fields like "Last,First")
            String[] cells = parseCSVLine(sampled.get(i));

            // Extract relevant columns by index
            String candidate = getCell(cells, 0);  // Candidate name (quoted "Last,First")
            String type      = getCell(cells, 1);  // Student / Faculty / NTE
            String exam      = getCell(cells, 3);  // Exam name
            String score     = getCell(cells, 6);  // Numeric score
            String result    = getCell(cells, 7);  // PASS or FAIL

            System.out.printf("  [%3d] %-22s %-10s %-42s %-6s %-6s%n",
                    (i + 1), candidate, type, exam, score, result);
        }

        System.out.println("============================================================");
        System.out.println("  Done. " + sampleSize + " random record(s) displayed.");
        System.out.println("============================================================");

        scanner.close();
    }

    // ── Helper: Parse a CSV line that may contain quoted commas ──
    // The Candidate column uses "Last,First" format wrapped in quotes.
    // This method correctly handles that by tracking quote state.
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean      inQuotes = false;   // true when we are inside a quoted field
        StringBuilder sb      = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;      // toggle: entering or leaving a quoted field
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString().trim()); // comma outside quotes = field separator
                sb.setLength(0);                  // reset buffer for next field
            } else {
                sb.append(c);             // regular character - append to current field
            }
        }
        fields.add(sb.toString().trim()); // add the final field after the last comma

        return fields.toArray(new String[0]);
    }

    // ── Helper: Safely get a cell value by column index ──────────
    // Returns empty string instead of throwing ArrayIndexOutOfBoundsException.
    private static String getCell(String[] cells, int index) {
        return (index < cells.length) ? cells[index] : "";
    }
}