/**
 * ============================================================
 * Student   : Rinamae Czel Siana M. Bautista
 * Student ID: 252060651
 * Section   : 9302-AY225
 * Course    : Math 101 / Programming 2
 * Assignment: MP17 – Find Longest Text Entry
 * Date      : March 2026
 *
 * Description:
 *   Reads the Pearson VUE exam results CSV, skips the 6
 *   metadata rows, then for EACH column finds and displays
 *   the cell with the longest text value along with its
 *   row number, column name, and character count.
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
import java.util.List;
import java.util.Scanner;

public class MP17_LongestEntry {

    // Number of metadata rows to skip before the real column header
    private static final int SKIP_ROWS = 6;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================================");
        System.out.println("  MP17 - Find Longest Text Entry");
        System.out.println("  Student: Rinamae Czel Siana M. Bautista | 9302-AY225");
        System.out.println("  Dataset: Pearson VUE Exam Results");
        System.out.println("============================================================");

        // ── Step 1: Ask user for CSV file path ───────────────────────
        System.out.print("  Enter CSV file path: ");
        String filePath = scanner.nextLine().trim();

        // ── Step 2: Read and parse the CSV file ──────────────────────
        // 'headers'  - array of column names from line 7
        // 'rows'     - list of all data rows, each parsed into a String[]
        String[]       headers = null;
        List<String[]> rows    = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            int    lineNum    = 0;
            int    headerLine = SKIP_ROWS + 1; // line 7 is the actual column header

            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.replace("\r", ""); // strip carriage return

                // Skip the first 6 metadata rows
                if (lineNum < headerLine) continue;

                // Line 7 is the column header row
                if (lineNum == headerLine) {
                    headers = parseCSVLine(line);
                    continue;
                }

                // Skip blank rows at end of file
                if (line.trim().replace(",", "").isEmpty()) continue;

                // Parse each data row and add to the list
                rows.add(parseCSVLine(line));
            }

        } catch (IOException e) {
            System.out.println("\n  ERROR: Cannot read file - " + e.getMessage());
            System.out.println("  Please check the file path and try again.");
            scanner.close();
            return;
        }

        // ── Step 3: Validate dataset ─────────────────────────────────
        if (headers == null || rows.isEmpty()) {
            System.out.println("\n  ERROR: No data found in the dataset.");
            scanner.close();
            return;
        }

        int totalRows    = rows.size();
        int totalColumns = headers.length;

        System.out.println("\n  Total records loaded : " + totalRows);
        System.out.println("  Total columns found  : " + totalColumns);

        System.out.println("\n============================================================");
        System.out.println("  LONGEST TEXT ENTRY PER COLUMN");
        System.out.println("============================================================");

        // ── Step 4: Loop through every column ────────────────────────
        for (int col = 0; col < totalColumns; col++) {

            // Variables to track the longest entry in this column
            String longestValue  = "";   // text of the longest cell
            int    longestLength = 0;    // character count of the longest cell
            int    longestRowNum = -1;   // 1-based row number where it was found

            // Loop through every data row for the current column
            for (int row = 0; row < rows.size(); row++) {
                String[] cells     = rows.get(row);
                String   cellValue = (col < cells.length) ? cells[col] : "";
                int      cellLen   = cellValue.length();

                // Update tracking variables if this cell is longer
                if (cellLen > longestLength) {
                    longestLength = cellLen;
                    longestValue  = cellValue;
                    longestRowNum = row + 1; // convert 0-index to 1-based row number
                }
            }

            // ── Step 5: Display result for this column ──────────────
            String colName = headers[col].trim();

            // Skip the blank "Column1" and the trailing empty columns
            if (colName.isEmpty()) {
                System.out.printf("  Column %-3d : (unnamed/empty column - skipped)%n", col + 1);
                System.out.println("  ----------------------------------------------------------");
                continue;
            }

            System.out.printf("  Column %-3d : %s%n",          col + 1, colName);
            System.out.printf("  Row Number : %d%n",            longestRowNum);
            System.out.printf("  Characters : %d%n",            longestLength);
            System.out.printf("  Value      : %s%n",
                    longestValue.isEmpty() ? "(empty)" : longestValue);
            System.out.println("  ----------------------------------------------------------");
        }

        System.out.println("============================================================");
        System.out.println("  Done. Longest entries found for all columns.");
        System.out.println("============================================================");

        scanner.close();
    }

    // ── Helper: Parse a CSV line with possible quoted commas ─────
    // Correctly handles the "Last,First" quoted candidate name format.
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean      inQuotes = false;  // tracks whether inside a quoted field
        StringBuilder sb      = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;       // enter or exit quoted field
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString().trim()); // save current field
                sb.setLength(0);                  // reset buffer
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString().trim()); // save the last field

        return fields.toArray(new String[0]);
    }
}