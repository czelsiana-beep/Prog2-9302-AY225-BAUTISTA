'use strict';

const fs = require('fs');
const readline = require('readline');
const path = require('path');

const MONTH_NAMES = [
  '', 'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

// -------------------------------------------------------
// MODULE: DataRecord - represents one game sales record
// -------------------------------------------------------
function DataRecord(title, console_, genre, publisher, totalSales, releaseDate) {
  this.title = title;
  this.console = console_;
  this.genre = genre;
  this.publisher = publisher;
  this.totalSales = totalSales;
  this.releaseDate = releaseDate;

  this.getMonth = function () {
    try {
      const parts = this.releaseDate.split('-');
      if (parts.length < 2) return -1;
      return parseInt(parts[1]);
    } catch {
      return -1;
    }
  };
}

// -------------------------------------------------------
// MODULE: CSV Parser (handles quoted fields)
// -------------------------------------------------------
function parseCSVLine(line) {
  const result = [];
  let current = '';
  let inQuotes = false;

  for (let i = 0; i < line.length; i++) {
    const ch = line[i];
    if (ch === '"') {
      inQuotes = !inQuotes;
    } else if (ch === ',' && !inQuotes) {
      result.push(current);
      current = '';
    } else {
      current += ch;
    }
  }
  result.push(current);
  return result;
}

// -------------------------------------------------------
// MODULE: File Validation
// -------------------------------------------------------
function validateFile(filePath) {
  // Check if file exists
  if (!fs.existsSync(filePath)) {
    return { valid: false, error: 'Error: File does not exist.' };
  }

  // Check if file is readable
  try {
    fs.accessSync(filePath, fs.constants.R_OK);
  } catch {
    return { valid: false, error: 'Error: File is not readable.' };
  }

  // Check if file is a CSV
  if (path.extname(filePath).toLowerCase() !== '.csv') {
    return { valid: false, error: 'Error: File must be a CSV file (.csv).' };
  }

  // Check if CSV has correct format (read first line)
  try {
    const content = fs.readFileSync(filePath, 'utf8');
    const firstLine = content.split('\n')[0].toLowerCase();
    if (!firstLine.includes('total_sales') || !firstLine.includes('release_date')) {
      return { valid: false, error: 'Error: File is not a valid Video Game Sales CSV.' };
    }
  } catch (e) {
    return { valid: false, error: 'Error reading file: ' + e.message };
  }

  return { valid: true };
}

// -------------------------------------------------------
// MODULE: Load Dataset into DataRecord objects
// -------------------------------------------------------
function loadDataset(filePath) {
  const records = [];

  try {
    const content = fs.readFileSync(filePath, 'utf8');
    const lines = content.split('\n');

    const headers = parseCSVLine(lines[0]);
    const idx = {
      title:       headers.findIndex(h => h.trim().toLowerCase() === 'title'),
      console:     headers.findIndex(h => h.trim().toLowerCase() === 'console'),
      genre:       headers.findIndex(h => h.trim().toLowerCase() === 'genre'),
      publisher:   headers.findIndex(h => h.trim().toLowerCase() === 'publisher'),
      totalSales:  headers.findIndex(h => h.trim().toLowerCase() === 'total_sales'),
      releaseDate: headers.findIndex(h => h.trim().toLowerCase() === 'release_date'),
    };

    for (let i = 1; i < lines.length; i++) {
      const line = lines[i].trim();
      if (!line) continue;

      try {
        const cols = parseCSVLine(line);
        const salesStr    = cols[idx.totalSales]  ? cols[idx.totalSales].trim()  : '';
        const releaseDate = cols[idx.releaseDate] ? cols[idx.releaseDate].trim() : '';

        if (!salesStr || !releaseDate) continue;

        const sales = parseFloat(salesStr);
        if (isNaN(sales)) continue;

        records.push(new DataRecord(
          cols[idx.title]     ? cols[idx.title].trim()     : '',
          cols[idx.console]   ? cols[idx.console].trim()   : '',
          cols[idx.genre]     ? cols[idx.genre].trim()     : '',
          cols[idx.publisher] ? cols[idx.publisher].trim() : '',
          sales,
          releaseDate
        ));
      } catch (e) {
        // Skip invalid rows silently
      }
    }
  } catch (e) {
    console.log('Error loading dataset: ' + e.message);
  }

  return records;
}

// -------------------------------------------------------
// MODULE: Display Monthly Summary
// -------------------------------------------------------
function displayMonthlySummary(records) {
  const monthlySales = {};

  for (const record of records) {
    const month = record.getMonth();
    if (month < 1 || month > 12) continue;
    monthlySales[month] = (monthlySales[month] || 0) + record.totalSales;
  }

  const sortedMonths = Object.keys(monthlySales).map(Number).sort((a, b) => a - b);

  if (sortedMonths.length === 0) {
    console.log('No monthly data available.');
    return;
  }

  console.log('============================================');
  console.log('    MONTHLY SALES SUMMARY (in millions)    ');
  console.log('============================================');
  console.log('Month           Total Sales (M)');
  console.log('--------------------------------------------');

  let bestMonth = -1;
  let bestSales = -1;

  for (const month of sortedMonths) {
    const sales = monthlySales[month];
    const monthLabel = MONTH_NAMES[month].padEnd(15);
    const salesLabel = sales.toFixed(2).padStart(15);
    console.log(`${monthLabel} ${salesLabel}`);

    if (sales > bestSales) {
      bestSales = sales;
      bestMonth = month;
    }
  }

  console.log('============================================');
  console.log(`\n>>> Best-Performing Month : ${MONTH_NAMES[bestMonth]}`);
  console.log(`>>> Total Sales           : ${bestSales.toFixed(2)} million units`);
  console.log('============================================');
}

// -------------------------------------------------------
// MAIN: Entry point - prompt for file path, validate, run
// -------------------------------------------------------
const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

console.log('============================================');
console.log('       MONTHLY PERFORMANCE ANALYZER        ');
console.log('       Video Game Sales 2024 Dataset        ');
console.log('============================================\n');

function askFilePath() {
  rl.question('Enter dataset file path: ', function (inputPath) {
    const filePath = inputPath.trim();

    const validation = validateFile(filePath);
    if (!validation.valid) {
      console.log(validation.error + ' Please try again.\n');
      askFilePath();
    } else {
      console.log('File found and validated. Processing...\n');
      rl.close();

      const records = loadDataset(filePath);
      console.log(`Records loaded: ${records.length}\n`);

      if (records.length === 0) {
        console.log('No valid records found in the dataset.');
      } else {
        displayMonthlySummary(records);
      }
    }
  });
}

askFilePath();