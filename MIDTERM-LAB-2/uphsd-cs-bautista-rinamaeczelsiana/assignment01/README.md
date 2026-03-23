# Programming Assignment 1 — 3×3 Matrix Determinant Solver

## Student Information

- **Full Name:** Rinamae Czel Siana M. Bautista
- **Student ID:** 252060651
- **Section:** 9302-AY225
- **Course:** BSIT-GD 1 Game Development, UPHSD Molino Campus
- **Assignment:** Programming Assignment 1 — 3×3 Matrix Determinant Solver
- **Date:** March 18, 2026

---

## Assigned Matrix

My assigned matrix (Bautista, Rinamae Czel Siana M.):

```
| 4  3  2 |
| 1  5  3 |
| 2  1  4 |
```

---

## How to Run the Java Program

**Step 1 — Compile the program:**

```bash
javac DeterminantSolver.java
```

**Step 2 — Run the compiled program:**

```bash
java DeterminantSolver
```

> Make sure you are inside the project folder when running these commands.

---

## How to Run the JavaScript Program

**Run with Node.js:**

```bash
node determinant_solver.js
```

> Requires Node.js to be installed. No additional packages needed.

---

## Sample Output

Both programs produce the same result. Below is the sample output:

```
===================================================
  3x3 MATRIX DETERMINANT SOLVER
  Student: Rinamae Czel Siana M. Bautista
  Assigned Matrix:
===================================================
  |  4   3   2  |
  |  1   5   3  |
  |  2   1   4  |
===================================================

Expanding along Row 1 (cofactor expansion):

  Step 1 — Minor M₁₁: det([5,3],[1,4]) = (5×4) - (3×1) = 20 - 3 = 17
  Step 2 — Minor M₁₂: det([1,3],[2,4]) = (1×4) - (3×2) = 4 - 6 = -2
  Step 3 — Minor M₁₃: det([1,5],[2,1]) = (1×1) - (5×2) = 1 - 10 = -9

  Cofactor C₁₁ = (+1) × 4 × 17 =   68
  Cofactor C₁₂ = (-1) × 3 × -2 =    6
  Cofactor C₁₃ = (+1) × 2 × -9 =  -18

  det(M) = 68 + (6) + -18

===================================================
  ✓  DETERMINANT = 56
===================================================
```

---

## Final Determinant Value

**det(M) = 56**

Since the determinant is not zero, the matrix is **non-singular** and has an inverse.

---

## Files in This Repository

| File | Description |
|------|-------------|
| `DeterminantSolver.java` | Java solution — computes the determinant with step-by-step console output |
| `determinant_solver.js` | JavaScript (Node.js) solution — identical logic and output |
| `README.md` | This documentation file |