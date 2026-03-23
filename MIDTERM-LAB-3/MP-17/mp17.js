/**
 * ============================================================
 * Student   : Rinamae Czel Siana M. Bautista
 * Student ID: 252060651
 * Section   : 9302-AY225
 * Course    : Math 101 / Programming 2
 * Assignment: MP17 – Find Longest Text Entry.
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

"use strict";

const fs       = require("fs");
const readline = require("readline");

// Number of metadata rows to skip before the actual column header
const SKIP_ROWS = 6;

// ── Helper: Ask a question in the terminal ────────────────────
const askQuestion = (rl, question) => {
    return new Promise((resolve) => {
        rl.question(question, (answer) => resolve(answer.trim()));
    });
};

// ── Helper: Parse a CSV line with possible quoted commas ─────
// Handles the "Last,First" quoted name format in the Candidate column.
const parseCSVLine = (line) => {
    const fields  = [];
    let inQuotes  = false;  // true when we are inside a quoted field
    let current   = "";     // buffer for current field being built

    for (const char of line) {
        if (char === '"') {
            inQuotes = !inQuotes;        // toggle: entering or leaving a quoted field
        } else if (char === ',' && !inQuotes) {
            fields.push(current.trim()); // comma outside quotes = field separator
            current = "";
        } else {
            current += char;             // regular character: append to current field
        }
    }
    fields.push(current.trim());         // push the final field

    return fields;
};

// ── Main program ─────────────────────────────────────────────
const main = async () => {

    const rl = readline.createInterface({
        input:  process.stdin,
        output: process.stdout
    });

    console.log("============================================================");
    console.log("  MP17 - Find Longest Text Entry");
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

    // ── Step 3: Split into lines and separate header from data ─
    const allLines = fileContent
        .split(/\r?\n/)
        .map(line => line.replace(/^\uFEFF/, "")); // strip BOM character if present

    // Line at index SKIP_ROWS (6) is the real column header
    const headerLine = allLines[SKIP_ROWS];
    const headers    = headerLine ? parseCSVLine(headerLine) : [];

    // Data rows start at index SKIP_ROWS + 1
    // Filter out completely blank rows
    const dataRows = allLines
        .slice(SKIP_ROWS + 1)
        .filter(line => line.trim().replace(/,/g, "") !== "")
        .map(parseCSVLine); // parse each data row into a field array

    const totalRows    = dataRows.length;
    const totalColumns = headers.length;

    if (totalRows === 0) {
        console.log("\n  ERROR: No data rows found in the dataset.");
        rl.close();
        return;
    }

    console.log(`\n  Total records loaded : ${totalRows}`);
    console.log(`  Total columns found  : ${totalColumns}`);

    console.log("\n============================================================");
    console.log("  LONGEST TEXT ENTRY PER COLUMN");
    console.log("============================================================");

    // ── Step 4: Find the longest value in each column ────────
    for (let col = 0; col < totalColumns; col++) {

        const colName = (headers[col] || "").trim();

        // Skip the blank "Column1" and the three trailing unnamed columns
        if (colName === "" || colName === "Column1") {
            console.log(`  Column ${col + 1}   : (unnamed/empty column - skipped)`);
            console.log("  ----------------------------------------------------------");
            continue;
        }

        // Variables to track the longest entry in this column
        let longestValue  = "";   // text of the longest cell
        let longestLength = 0;    // its character count
        let longestRowNum = -1;   // 1-based row number where it was found

        for (let row = 0; row < dataRows.length; row++) {
            const cells     = dataRows[row];
            const cellValue = cells[col] !== undefined ? cells[col] : "";
            const cellLen   = cellValue.length;

            // Update if this cell is longer than the current longest
            if (cellLen > longestLength) {
                longestLength = cellLen;
                longestValue  = cellValue;
                longestRowNum = row + 1; // convert 0-based index to 1-based row number
            }
        }

        // ── Step 5: Display result for this column ───────────
        const display = longestValue === "" ? "(empty)" : longestValue;

        console.log(`  Column ${String(col + 1).padEnd(3)}: ${colName}`);
        console.log(`  Row Number : ${longestRowNum}`);
        console.log(`  Characters : ${longestLength}`);
        console.log(`  Value      : ${display}`);
        console.log("  ----------------------------------------------------------");
    }

    console.log("============================================================");
    console.log("  Done. Longest entries found for all columns.");
    console.log("============================================================");

    rl.close();
};

main().catch((err) => {
    console.error("  UNEXPECTED ERROR: " + err.message);
    process.exit(1);
});