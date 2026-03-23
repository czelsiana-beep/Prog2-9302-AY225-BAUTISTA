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

"use strict";

const fs       = require("fs");       // Node.js File System module for reading files
const readline = require("readline"); // Module for reading user input from terminal

// ── Number of metadata rows to skip before the real header ───
// Rows 1-6 are metadata (school name, result count, etc.)
// Row 7 is the actual column header
const SKIP_ROWS = 6;

// ── Helper: Ask a question in the terminal ────────────────────
// Wraps readline.question() in a Promise for use with async/await.
const askQuestion = (rl, question) => {
    return new Promise((resolve) => {
        rl.question(question, (answer) => resolve(answer.trim()));
    });
};

// ── Helper: Parse a CSV line with possible quoted commas ─────
// Handles the "Last,First" quoted candidate name format.
// Returns an array of trimmed field values.
const parseCSVLine = (line) => {
    const fields  = [];
    let inQuotes  = false;   // true when inside a quoted field
    let current   = "";      // buffer for the current field being built

    for (const char of line) {
        if (char === '"') {
            inQuotes = !inQuotes;       // toggle quote mode
        } else if (char === ',' && !inQuotes) {
            fields.push(current.trim()); // end of field - save and reset buffer
            current = "";
        } else {
            current += char;            // append regular character to buffer
        }
    }
    fields.push(current.trim());        // push the final field after last comma

    return fields;
};

// ── Helper: Fisher-Yates shuffle ─────────────────────────────
// Randomly shuffles an array in-place to produce a random order.
// Used to select random rows without duplicates.
const shuffleArray = (arr) => {
    for (let i = arr.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1)); // random index 0 to i
        [arr[i], arr[j]] = [arr[j], arr[i]];           // swap elements
    }
    return arr;
};

// ── Helper: Pad or truncate a string to a fixed width ────────
// Used to align columns in the formatted table output.
const col = (str, width) => {
    const s = String(str);
    return s.length > width ? s.substring(0, width) : s.padEnd(width);
};

// ── Main program ─────────────────────────────────────────────
const main = async () => {

    const rl = readline.createInterface({
        input:  process.stdin,
        output: process.stdout
    });

    console.log("============================================================");
    console.log("  MP16 - Random Dataset Sampler");
    console.log("  Student: Rinamae Czel Siana M. Bautista | 9302-AY225");
    console.log("  Dataset: Pearson VUE Exam Results");
    console.log("============================================================");

    // ── Step 1: Ask for the CSV file path ────────────────────
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

    // ── Step 3: Split into lines and skip metadata rows ───────
    // Split by newline (handles both Windows \r\n and Unix \n)
    const allLines = fileContent
        .split(/\r?\n/)
        .map(line => line.replace(/^\uFEFF/, "")); // strip BOM if present

    // Lines 0-5 (index) are metadata; line 6 (index) is the real header
    // Lines 7+ (index) are data rows
    const dataLines = allLines
        .slice(SKIP_ROWS + 1)                           // skip metadata + header
        .filter(line => line.trim().replace(/,/g, "") !== ""); // skip blank rows

    const totalRows = dataLines.length;

    if (totalRows === 0) {
        console.log("\n  ERROR: No data rows found in the dataset.");
        rl.close();
        return;
    }

    console.log(`\n  Total exam records loaded: ${totalRows}`);

    // ── Step 4: Ask how many rows to sample ───────────────────
    let sampleSize = 0;

    while (true) {
        const input  = await askQuestion(rl, `  Enter number of random rows to sample (1-${totalRows}): `);
        const parsed = parseInt(input, 10);

        if (isNaN(parsed) || parsed < 1 || parsed > totalRows) {
            console.log(`  Please enter a number between 1 and ${totalRows}.`);
        } else {
            sampleSize = parsed;
            break; // valid input - exit loop
        }
    }

    // ── Step 5: Shuffle and pick the first N rows ─────────────
    // Spread operator (...) creates a copy so original array is not mutated.
    // After shuffling, slice takes the first sampleSize rows.
    const shuffled = shuffleArray([...dataLines]);
    const sampled  = shuffled.slice(0, sampleSize);

    // ── Step 6: Display results in formatted table ────────────
    console.log("\n============================================================");
    console.log(`  RANDOM SAMPLE - ${sampleSize} of ${totalRows} record(s)`);
    console.log("============================================================");

    // Print formatted column header labels
    console.log(
        "  " +
        col("#", 4) +
        col("Candidate", 22) +
        col("Type", 10) +
        col("Exam", 42) +
        col("Score", 6) +
        col("Result", 6)
    );
    console.log("  " + "-".repeat(90));

    sampled.forEach((line, i) => {
        // Parse each CSV row handling quoted "Last,First" candidate names
        const cells = parseCSVLine(line);

        // Extract columns by their index position
        const candidate = cells[0] || "";  // Candidate name (quoted "Last,First")
        const type      = cells[1] || "";  // Student / Faculty / NTE
        const exam      = cells[3] || "";  // Exam name
        const score     = cells[6] || "";  // Numeric score
        const result    = cells[7] || "";  // PASS or FAIL

        console.log(
            "  " +
            col(`[${i + 1}]`, 4) +
            col(candidate, 22) +
            col(type, 10) +
            col(exam, 42) +
            col(score, 6) +
            col(result, 6)
        );
    });

    console.log("============================================================");
    console.log(`  Done. ${sampleSize} random record(s) displayed.`);
    console.log("============================================================");

    rl.close();
};

// Run main and catch any unhandled errors
main().catch((err) => {
    console.error("  UNEXPECTED ERROR: " + err.message);
    process.exit(1);
});