/**
 * ============================================================
 * Student  : Rinamae Czel Siana M. Bautista
 * Student ID: 252060651
 * Course   : 9302
 * Assignment: Determinant via Cofactor Expansion (3×3 Matrix)
 * Date     : March 18, 2026
 * Description: Computes the determinant of a hardcoded 3×3
 *              matrix using cofactor expansion along the first
 *              row, printing every intermediate step clearly.
 * ============================================================
 */

"use strict";

// --------------------------------------------------------
// Prints the title banner, student name, and the 3x3
// matrix in the required bordered grid format.
// --------------------------------------------------------
const printMatrix = (M) => {
    console.log("===================================================");
    console.log("  3x3 MATRIX DETERMINANT SOLVER");
    console.log("  Student: Rinamae Czel Siana M. Bautista");
    console.log("  Assigned Matrix:");
    console.log("===================================================");
    M.forEach(row => {
        const [a, b, c] = row.map(n => String(n).padStart(2));
        console.log(`  | ${a}  ${b}  ${c}  |`);
    });
    console.log("===================================================");
    console.log();
};

// --------------------------------------------------------
// Accepts the four elements of a 2x2 sub-matrix and returns
// its determinant using the formula: (a*d) - (b*c).
// --------------------------------------------------------
const computeMinor = (a, b, c, d) => (a * d) - (b * c);

// --------------------------------------------------------
// Carries out cofactor expansion along row 1, printing each
// 2x2 minor, each signed cofactor term, the expansion sum,
// and the final determinant. Detects singular matrices too.
// --------------------------------------------------------
const solveDeterminant = (M) => {

    console.log("Expanding along Row 1 (cofactor expansion):");
    console.log();

    // Pivot elements from the first row
    const [m00, m01, m02] = M[0];

    // ---- Minor M11: sub-matrix formed by removing row 1 and col 1 ----
    const minor11 = computeMinor(M[1][1], M[1][2], M[2][1], M[2][2]);
    console.log(
        `  Step 1 \u2014 Minor M\u2081\u2081: det([${M[1][1]},${M[1][2]}],[${M[2][1]},${M[2][2]}])` +
        ` = (${M[1][1]}\u00d7${M[2][2]}) - (${M[1][2]}\u00d7${M[2][1]})` +
        ` = ${M[1][1]*M[2][2]} - ${M[1][2]*M[2][1]} = ${minor11}`
    );

    // ---- Minor M12: sub-matrix formed by removing row 1 and col 2 ----
    const minor12 = computeMinor(M[1][0], M[1][2], M[2][0], M[2][2]);
    console.log(
        `  Step 2 \u2014 Minor M\u2081\u2082: det([${M[1][0]},${M[1][2]}],[${M[2][0]},${M[2][2]}])` +
        ` = (${M[1][0]}\u00d7${M[2][2]}) - (${M[1][2]}\u00d7${M[2][0]})` +
        ` = ${M[1][0]*M[2][2]} - ${M[1][2]*M[2][0]} = ${minor12}`
    );

    // ---- Minor M13: sub-matrix formed by removing row 1 and col 3 ----
    const minor13 = computeMinor(M[1][0], M[1][1], M[2][0], M[2][1]);
    console.log(
        `  Step 3 \u2014 Minor M\u2081\u2083: det([${M[1][0]},${M[1][1]}],[${M[2][0]},${M[2][1]}])` +
        ` = (${M[1][0]}\u00d7${M[2][1]}) - (${M[1][1]}\u00d7${M[2][0]})` +
        ` = ${M[1][0]*M[2][1]} - ${M[1][1]*M[2][0]} = ${minor13}`
    );

    console.log();

    // ---- Compute each signed cofactor term ----
    const term1 =  m00 * minor11;  // positive sign for column 1
    const term2 = -m01 * minor12;  // negative sign for column 2
    const term3 =  m02 * minor13;  // positive sign for column 3

    console.log(`  Cofactor C\u2081\u2081 = (+1) \u00d7 ${m00} \u00d7 ${String(minor11).padStart(2)} = ${String(term1).padStart(4)}`);
    console.log(`  Cofactor C\u2081\u2082 = (-1) \u00d7 ${m01} \u00d7 ${String(minor12).padStart(2)} = ${String(term2).padStart(4)}`);
    console.log(`  Cofactor C\u2081\u2083 = (+1) \u00d7 ${m02} \u00d7 ${String(minor13).padStart(2)} = ${String(term3).padStart(4)}`);

    console.log();

    // Apply the full first-row cofactor expansion formula
    const det = M[0][0] * (M[1][1]*M[2][2] - M[1][2]*M[2][1])
              - M[0][1] * (M[1][0]*M[2][2] - M[1][2]*M[2][0])
              + M[0][2] * (M[1][0]*M[2][1] - M[1][1]*M[2][0]);

    // Print the expansion sum before showing the final result
    console.log(`  det(M) = ${term1} + (${term2}) + ${term3}`);
    console.log();
    console.log("===================================================");
    console.log(`  \u2713  DETERMINANT = ${det}`);
    console.log("===================================================");

    // A determinant of zero means the matrix cannot be inverted
    if (det === 0) {
        console.log("  The matrix is SINGULAR \u2014 it has no inverse.");
        console.log("===================================================");
    }
};

// --------------------------------------------------------
// Hardcoded assigned matrix — Bautista, Rinamae Czel Siana M.
// --------------------------------------------------------
const matrix = [
    [4, 3, 2],
    [1, 5, 3],
    [2, 1, 4]
];

// Run both display functions in sequence
printMatrix(matrix);
solveDeterminant(matrix);