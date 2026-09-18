# Problem Statement: Smart Hostel Management System (SHMS)

## Course Details
* **Course Title:** Programming in Java
* **Academic Term:** Flipped Course Project Evaluation
* **Submission Platform:** VITyarthi

---

## 1. Problem Description
University residential campus hostels manage thousands of students, varying room categories (Standard Non-AC, Deluxe AC), fluctuating occupancy rates, and complex semester fee structures (room rent, utility charges, electrical tariffs, and mess billing).

Traditional manual ledger or spreadsheet-based tracking suffers from:
1. **Double-Allocation & Overcrowding:** Rooms assigned beyond their maximum bed capacity.
2. **Billing Inconsistencies:** Difficulty calculating polymorphic fee components across diverse room categories.
3. **Fee Defaulter Tracking:** Inability to readily track outstanding dues prior to room vacation or semester checkouts.
4. **Disjoint Record Keeping:** Lack of persistent, human-readable records connecting room inventories, student allocations, and transaction receipts.

---

## 2. Project Objectives
The objective of this project is to develop a lightweight, 100% terminal-executable, zero-dependency Java application demonstrating core Object-Oriented Programming (OOP) principles:

1. **Object-Oriented Architecture:**
   - **Abstraction:** Abstract base classes (`Room`, `Person`) defining interface contracts.
   - **Inheritance:** Concrete subclasses (`StandardRoom`, `DeluxeACRoom` extending `Room`; `Student` extending `Person`).
   - **Polymorphism:** Dynamic method dispatch for `calculateTotalFee(months)` and room detail resolution.
   - **Encapsulation:** Private data members protected via validated accessors and state modifiers.
2. **Capacity & Bed Allocation Safety:**
   - Enforce capacity limits with custom checked exceptions (`RoomFullException`, `InvalidDataException`).
3. **Fee Calculation & Payment Tracking:**
   - Calculate semester fees based on room category and utility surcharges.
   - Record payments, issue transaction receipts, and track defaulters.
4. **Persistent Data Storage:**
   - Flat file database (`hostel_data.txt`) using structured pipe-delimited records.
5. **Evaluator-Friendly Execution:**
   - Command-line flags (`--test`, `--demo`) for automated assessment pipelines.

---

## 3. Scope & Deliverables
* **`Main.java`**: Self-contained Java source file containing all class definitions and CLI controller.
* **`screenshots/`**: Terminal output captures demonstrating Login, Admin Dashboard, Student Management, and Fee Records.
* **`Project Report - Smart Hostel Management System.md`**: Academic report with UML architecture and test suite results.
* **`README.md`**: Clear setup and execution guide for manual and automated evaluation.
