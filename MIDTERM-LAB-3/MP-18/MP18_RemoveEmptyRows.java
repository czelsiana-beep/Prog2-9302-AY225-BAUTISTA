/**
 * ============================================================
 * Student   : Rinamae Czel Siana M. Bautista
 * Student ID: 252060651
 * Section   : 9302-AY225
 * Course    : Math 101 / Programming 2
 * Assignment: MP18 – Remove Rows with Empty Fields
 * Date      : March 2026
 *
 * Description:
 *   Reads the Pearson VUE exam results CSV, skips the 6
 *   metadata rows, identifies all rows that have at least
 *   one empty/blank field in the MAIN columns (ignoring the
 *   3 trailing unnamed columns), then displays the clean
 *   dataset and a summary of removed rows.
 *
 * Dataset columns (Row 7 = header):
 *   Candidate | Student/Faculty/NTE | Column1 | Exam |
 *   Language  | Exam Date | Score | Result | Time Used
 *
 * Note: "Column1" in the dataset is always empty by design.
 *   It is excluded from the empty-field check.
 * ============================================================
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MP18_RemoveEmptyRows {

    // Number of metadata rows to skip before the real column header
    private static final int SKIP_ROWS = 6;

    // Columns to CHECK for empty values (0-based index)
    // We check: Candidate(0), Type(1), Exam(3), Language(4),
    //           Exam Date(5), Score(6), Result(7), Time Used(8)
    // We SKIP Column1(2) because it is intentionally blank in this dataset
    // We SKIP trailing unnamed columns (9, 10, 11)
    private static final int[] CHECK_COLUMNS = {0, 1, 3, 4, 5, 6, 7, 8};

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================================");
        System.out.println("  MP18 - Remove Rows with Empty Fields");
        System.out.println("  Student: Rinamae Czel Siana M. Bautista | 9302-AY225");
        System.out.println("  Dataset: Pearson VUE Exam Results");
        System.out.println("============================================================");

        // ── Step 1: Ask user for CSV file path ───────────────────────
        System.out.print("  Enter CSV file path: ");
        String filePath = scanner.nextLine().trim();

        // ── Step 2: Read and parse the CSV file ──────────────────────
        // 'header'   - the column names row (line 7)
        // 'allRows'  - every data row parsed into String[] of cell values
        String         header  = null;
        List<String[]> allRows = new ArrayList<>();
        List<String>   rawLines = new ArrayList<>(); // raw line strings for display

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            int    lineNum    = 0;
            int    headerLine = SKIP_ROWS + 1;

            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.replace("\r", ""); // strip carriage return

                if (lineNum < headerLine) continue; // skip metadata rows

                if (lineNum == headerLine) {
                    header = line;  // save header row
                    continue;
                }

                // Skip completely blank rows
                if (line.trim().replace(",", "").isEmpty()) continue;

                // Save raw line and parsed cells
                rawLines.add(line);
                allRows.add(parseCSVLine(line));
            }

        } catch (IOException e) {
            System.out.println("\n  ERROR: Cannot read file - " + e.getMessage());
            System.out.println("  Please check the file path and try again.");
            scanner.close();
            return;
        }

        // ── Step 3: Validate dataset ─────────────────────────────────
        if (header == null || allRows.isEmpty()) {
            System.out.println("\n  ERROR: No data found in the dataset.");
            scanner.close();
            return;
        }

        int totalRows = allRows.size();

        // ── Step 4: Separate clean rows from rows with empty fields ───
        // 'cleanRaw'   - raw lines where all main columns have a value
        // 'cleanParsed'- parsed version of clean rows (for table display)
        // 'removedRaw' - raw lines where at least one main column is empty
        List<String>   cleanRaw    = new ArrayList<>();
        List<String[]> cleanParsed = new ArrayList<>();
        List<String>   removedRaw  = new ArrayList<>();
        List<String[]> removedParsed = new ArrayList<>();

        for (int i = 0; i < allRows.size(); i++) {
            String[]  cells    = allRows.get(i);
            boolean   hasEmpty = false;

            // Check only the meaningful columns (skip Column1 and trailing empties)
            for (int colIdx : CHECK_COLUMNS) {
                String val = (colIdx < cells.length) ? cells[colIdx].trim() : "";
                if (val.isEmpty()) {
                    hasEmpty = true;
                    break; // one empty field is enough to flag the row
                }
            }

            if (hasEmpty) {
                removedRaw.add(rawLines.get(i));
                removedParsed.add(cells);
            } else {
                cleanRaw.add(rawLines.get(i));
                cleanParsed.add(cells);
            }
        }

        // ── Step 5: Display summary ───────────────────────────────────
        System.out.println("\n============================================================");
        System.out.println("  SUMMARY");
        System.out.println("============================================================");
        System.out.println("  Total records in dataset  : " + totalRows);
        System.out.println("  Records with empty fields : " + removedRaw.size());
        System.out.println("  Clean records remaining   : " + cleanRaw.size());

        // ── Step 6: Show removed rows ─────────────────────────────────
        if (!removedRaw.isEmpty()) {
            System.out.println("\n============================================================");
            System.out.println("  REMOVED RECORDS (had at least one empty field)");
            System.out.println("============================================================");
            printTableHeader();

            for (int i = 0; i < removedParsed.size(); i++) {
                printRow(i + 1, removedParsed.get(i), "REMOVED");
            }
        } else {
            System.out.println("\n  No rows removed - all records are complete.");
        }

        // ── Step 7: Show clean dataset ───────────────────────────────
        System.out.println("\n============================================================");
        System.out.println("  CLEAN DATASET (all main fields present)");
        System.out.println("============================================================");
        printTableHeader();

        if (cleanParsed.isEmpty()) {
            System.out.println("  (All rows had empty fields - no clean rows remaining.)");
        } else {
            for (int i = 0; i < cleanParsed.size(); i++) {
                printRow(i + 1, cleanParsed.get(i), "");
            }
        }

        System.out.println("============================================================");
        System.out.printf ("  Done. %d record(s) removed. %d clean record(s) remaining.%n",
                removedRaw.size(), cleanRaw.size());
        System.out.println("============================================================");

        scanner.close();
    }

    // ── Helper: Print the formatted table header ──────────────────
    private static void printTableHeader() {
        System.out.printf("  %-22s %-10s %-38s %-6s %-6s%n",
                "Candidate", "Type", "Exam", "Score", "Result");
        System.out.println("  " + "-".repeat(88));
    }

    // ── Helper: Print one formatted data row ─────────────────────
    private static void printRow(int num, String[] cells, String tag) {
        String candidate = getCell(cells, 0);
        String type      = getCell(cells, 1);
        String exam      = getCell(cells, 3);
        String score     = getCell(cells, 6);
        String result    = getCell(cells, 7);

        String prefix = tag.isEmpty()
                ? String.format("  [%3d] ", num)
                : String.format("  [%-7s %3d] ", tag, num);

        System.out.printf("%s%-22s %-10s %-38s %-6s %-6s%n",
                prefix, candidate, type, exam, score, result);
    }

    // ── Helper: Parse a CSV line with possible quoted commas ──────
    private static String[] parseCSVLine(String line) {
        List<String>  fields   = new ArrayList<>();
        boolean       inQuotes = false;
        StringBuilder sb       = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString().trim());

        return fields.toArray(new String[0]);
    }

    // ── Helper: Safely get a cell value by index ─────────────────
    private static String getCell(String[] cells, int index) {
        return (index < cells.length) ? cells[index] : "";
    }
}