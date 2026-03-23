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
 * Note: "Column1" is always empty by design in this dataset
 *   and is excluded from the empty-field check.
 * ============================================================
 */

"use strict";

const fs       = require("fs");
const readline = require("readline");

// Number of metadata rows to skip before the actual column header
const SKIP_ROWS = 6;

// Column indices to CHECK for empty values (0-based)
// Candidate(0), Type(1), Exam(3), Language(4),
// Date(5), Score(6), Result(7), Time Used(8)
// SKIPPED: Column1(2) - intentionally blank; trailing cols (9,10,11)
const CHECK_COLUMNS = [0, 1, 3, 4, 5, 6, 7, 8];

// ── Helper: Ask a question in the terminal ────────────────────
const askQuestion = (rl, question) => {
    return new Promise((resolve) => {
        rl.question(question, (answer) => resolve(answer.trim()));
    });
};

// ── Helper: Parse a CSV line with possible quoted commas ─────
// Handles the "Last,First" quoted candidate name format.
const parseCSVLine = (line) => {
    const fields  = [];
    let inQuotes  = false;  // true when inside a quoted field
    let current   = "";     // buffer for the current field

    for (const char of line) {
        if (char === '"') {
            inQuotes = !inQuotes;        // toggle quote mode
        } else if (char === ',' && !inQuotes) {
            fields.push(current.trim()); // end of field
            current = "";
        } else {
            current += char;             // append character to buffer
        }
    }
    fields.push(current.trim());         // push the last field

    return fields;
};

// ── Helper: Pad/truncate a string to a fixed width for alignment
const col = (str, width) => {
    const s = String(str || "");
    return s.length > width ? s.substring(0, width) : s.padEnd(width);
};

// ── Helper: Print one formatted table row ────────────────────
const printRow = (num, cells, tag = "") => {
    const candidate = cells[0] || "";
    const type      = cells[1] || "";
    const exam      = cells[3] || "";
    const score     = cells[6] || "";
    const result    = cells[7] || "";

    const prefix = tag
        ? `  [${tag.padEnd(7)} ${String(num).padStart(3)}] `
        : `  [${String(num).padStart(3)}] `;

    console.log(
        prefix +
        col(candidate, 22) +
        col(type, 10) +
        col(exam, 38) +
        col(score, 6) +
        col(result, 6)
    );
};

// ── Helper: Print the formatted table column header ──────────
const printTableHeader = () => {
    console.log(
        "  " +
        col("Candidate", 22) +
        col("Type", 10) +
        col("Exam", 38) +
        col("Score", 6) +
        col("Result", 6)
    );
    console.log("  " + "-".repeat(88));
};

// ── Main program ─────────────────────────────────────────────
const main = async () => {

    const rl = readline.createInterface({
        input:  process.stdin,
        output: process.stdout
    });

    console.log("============================================================");
    console.log("  MP18 - Remove Rows with Empty Fields");
    console.log("  Student: Rinamae Czel Siana M. Bautista | 9302-AY225");
    console.log("  Dataset: Pearson VUE Exam Results");
    console.log("============================================================");

    // ── Step 1: Ask for CSV file path ────────────────────────
    const filePath = await askQuestion(rl, "  Enter CSV file path: ");

    // ── Step 2: Read the CSV file ─────────────────────────────
    let fileContent;
    try {
        fileContent = fs.readFileSync(filePath, "utf8");
    } catch (err) {
        console.log("\n  ERROR: Cannot read file - " + err.message);
        console.log("  Please check the file path and try again.");
        rl.close();
        return;
    }

    // ── Step 3: Split into lines and skip metadata ────────────
    const allLines = fileContent
        .split(/\r?\n/)
        .map(line => line.replace(/^\uFEFF/, "")); // strip BOM if present

    // Data rows start after the header (index SKIP_ROWS + 1)
    // Filter out completely blank rows
    const dataLines = allLines
        .slice(SKIP_ROWS + 1)
        .filter(line => line.trim().replace(/,/g, "") !== "");

    const totalRows = dataLines.length;

    if (totalRows === 0) {
        console.log("\n  ERROR: No data rows found in the dataset.");
        rl.close();
        return;
    }

    // ── Step 4: Separate clean rows from rows with empty fields ─
    // For each row, check only the meaningful column indices.
    // If any checked column is empty, the row goes to removedRows.
    const cleanRows   = []; // rows where all main columns have values
    const removedRows = []; // rows where at least one main column is empty

    dataLines.forEach((line) => {
        const cells    = parseCSVLine(line);
        // Check if any of the required columns is empty
        const hasEmpty = CHECK_COLUMNS.some(idx => {
            const val = cells[idx] !== undefined ? cells[idx].trim() : "";
            return val === "";
        });

        if (hasEmpty) {
            removedRows.push(cells); // flag for removal
        } else {
            cleanRows.push(cells);   // keep in clean dataset
        }
    });

    // ── Step 5: Display summary ───────────────────────────────
    console.log("\n============================================================");
    console.log("  SUMMARY");
    console.log("============================================================");
    console.log(`  Total records in dataset  : ${totalRows}`);
    console.log(`  Records with empty fields : ${removedRows.length}`);
    console.log(`  Clean records remaining   : ${cleanRows.length}`);

    // ── Step 6: Show removed rows ─────────────────────────────
    if (removedRows.length > 0) {
        console.log("\n============================================================");
        console.log("  REMOVED RECORDS (had at least one empty field)");
        console.log("============================================================");
        printTableHeader();
        removedRows.forEach((cells, i) => printRow(i + 1, cells, "REMOVED"));
    } else {
        console.log("\n  No rows removed - all records are complete.");
    }

    // ── Step 7: Show clean dataset ────────────────────────────
    console.log("\n============================================================");
    console.log("  CLEAN DATASET (all main fields present)");
    console.log("============================================================");
    printTableHeader();

    if (cleanRows.length === 0) {
        console.log("  (All rows had empty fields - no clean rows remaining.)");
    } else {
        cleanRows.forEach((cells, i) => printRow(i + 1, cells));
    }

    console.log("============================================================");
    console.log(`  Done. ${removedRows.length} record(s) removed. ${cleanRows.length} clean record(s) remaining.`);
    console.log("============================================================");

    rl.close();
};

main().catch((err) => {
    console.error("  UNEXPECTED ERROR: " + err.message);
    process.exit(1);
});