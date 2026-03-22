/*
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

public class DeterminantSolver {

    // --------------------------------------------------------
    // Entry point — declares the assigned matrix and triggers
    // the matrix display followed by the full determinant solve.
    // --------------------------------------------------------
    public static void main(String[] args) {

        // Hardcoded assigned matrix for Bautista, Rinamae Czel Siana M.
        int[][] matrix = {
            {4, 3, 2},
            {1, 5, 3},
            {2, 1, 4}
        };

        // Show the header banner and the matrix, then solve
        printMatrix(matrix);
        solveDeterminant(matrix);
    }

    // --------------------------------------------------------
    // Prints the title banner, student name, and the 3x3
    // matrix in the required bordered grid format.
    // --------------------------------------------------------
    public static void printMatrix(int[][] M) {
        System.out.println("===================================================");
        System.out.println("  3x3 MATRIX DETERMINANT SOLVER");
        System.out.println("  Student: Rinamae Czel Siana M. Bautista");
        System.out.println("  Assigned Matrix:");
        System.out.println("===================================================");
        for (int[] row : M) {
            System.out.printf("  | %2d  %2d  %2d  |%n", row[0], row[1], row[2]);
        }
        System.out.println("===================================================");
        System.out.println();
    }

    // --------------------------------------------------------
    // Accepts the four elements of a 2x2 sub-matrix and returns
    // its determinant using the formula: (a*d) - (b*c).
    // --------------------------------------------------------
    public static int computeMinor(int a, int b, int c, int d) {
        return (a * d) - (b * c);
    }

    // --------------------------------------------------------
    // Carries out cofactor expansion along row 1, printing each
    // 2x2 minor, each signed cofactor term, the expansion sum,
    // and the final determinant. Detects singular matrices too.
    // --------------------------------------------------------
    public static void solveDeterminant(int[][] M) {

        System.out.println("Expanding along Row 1 (cofactor expansion):");
        System.out.println();

        // Pivot elements from the first row
        int m00 = M[0][0], m01 = M[0][1], m02 = M[0][2];

        // ---- Minor M11: sub-matrix formed by removing row 1 and col 1 ----
        int minor11 = computeMinor(M[1][1], M[1][2], M[2][1], M[2][2]);
        System.out.printf("  Step 1 \u2014 Minor M\u2081\u2081: det([%d,%d],[%d,%d]) = (%d\u00d7%d) - (%d\u00d7%d) = %d - %d = %d%n",
            M[1][1], M[1][2], M[2][1], M[2][2],
            M[1][1], M[2][2], M[1][2], M[2][1],
            M[1][1]*M[2][2], M[1][2]*M[2][1], minor11);

        // ---- Minor M12: sub-matrix formed by removing row 1 and col 2 ----
        int minor12 = computeMinor(M[1][0], M[1][2], M[2][0], M[2][2]);
        System.out.printf("  Step 2 \u2014 Minor M\u2081\u2082: det([%d,%d],[%d,%d]) = (%d\u00d7%d) - (%d\u00d7%d) = %d - %d = %d%n",
            M[1][0], M[1][2], M[2][0], M[2][2],
            M[1][0], M[2][2], M[1][2], M[2][0],
            M[1][0]*M[2][2], M[1][2]*M[2][0], minor12);

        // ---- Minor M13: sub-matrix formed by removing row 1 and col 3 ----
        int minor13 = computeMinor(M[1][0], M[1][1], M[2][0], M[2][1]);
        System.out.printf("  Step 3 \u2014 Minor M\u2081\u2083: det([%d,%d],[%d,%d]) = (%d\u00d7%d) - (%d\u00d7%d) = %d - %d = %d%n",
            M[1][0], M[1][1], M[2][0], M[2][1],
            M[1][0], M[2][1], M[1][1], M[2][0],
            M[1][0]*M[2][1], M[1][1]*M[2][0], minor13);

        System.out.println();

        // ---- Compute each signed cofactor term ----
        int term1 =  m00 * minor11;   // positive sign for column 1
        int term2 = -m01 * minor12;   // negative sign for column 2
        int term3 =  m02 * minor13;   // positive sign for column 3

        System.out.printf("  Cofactor C\u2081\u2081 = (+1) \u00d7 %d \u00d7 %2d = %4d%n", m00, minor11, term1);
        System.out.printf("  Cofactor C\u2081\u2082 = (-1) \u00d7 %d \u00d7 %2d = %4d%n", m01, minor12, term2);
        System.out.printf("  Cofactor C\u2081\u2083 = (+1) \u00d7 %d \u00d7 %2d = %4d%n", m02, minor13, term3);

        System.out.println();

        // Apply the full first-row cofactor expansion formula
        int det = M[0][0] * (M[1][1]*M[2][2] - M[1][2]*M[2][1])
                - M[0][1] * (M[1][0]*M[2][2] - M[1][2]*M[2][0])
                + M[0][2] * (M[1][0]*M[2][1] - M[1][1]*M[2][0]);

        // Print the expansion sum before showing the final result
        System.out.printf("  det(M) = %d + (%d) + %d%n", term1, term2, term3);
        System.out.println();
        System.out.println("===================================================");
        System.out.printf("  \u2713  DETERMINANT = %d%n", det);
        System.out.println("===================================================");

        // A determinant of zero means the matrix cannot be inverted
        if (det == 0) {
            System.out.println("  The matrix is SINGULAR \u2014 it has no inverse.");
            System.out.println("===================================================");
        }
    }
}