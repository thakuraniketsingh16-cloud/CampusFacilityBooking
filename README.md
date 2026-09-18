# Smart Hostel Management System (SHMS)

A robust, console-driven Java application for managing university hostel rooms, bed allocations, student records, and fee payment tracking with automated capacity enforcement and persistent flat-file storage.

Developed for **Programming in Java** course evaluation.

---

## 📸 Screenshots

| Login & Authentication | Warden Admin Dashboard |
| :---: | :---: |
| ![Login](screenshots/login.png) | ![Dashboard](screenshots/dashboard.png) |

| Student Management & Profiles | Fee Management & Receipts |
| :---: | :---: |
| ![Students](screenshots/students.png) | ![Fees](screenshots/fees.png) |

---

## ⚡ Quick Start & Execution

### Prerequisites
* **Java Development Kit (JDK)**: JDK 17 or higher (Tested & verified on Java 26).
* **Dependencies**: **Zero** external libraries or build tools required.

### 1. Compile
From the repository root:
```bash
javac Main.java
```

### 2. Run Interactive Console UI
```bash
java Main
```
*(Default Admin Credentials: Username `admin`, Password `admin123`)*

### 3. Run Automated Evaluator Test Suite
```bash
java Main --test
```

### 4. Run Non-Interactive Demo
```bash
java Main --demo
```

---

## 📁 Repository Structure
```
├── screenshots/
│   ├── dashboard.png                      # Warden dashboard and room occupancy
│   ├── fees.png                           # Fee defaulters and payment receipt
│   ├── login.png                          # Console login and authentication
│   └── students.png                       # Student list and profile lookup
├── Main.java                              # Complete self-contained Java source code
├── Project Report - Smart Hostel Management System.md   # Full university project report
├── Project Report - Smart Hostel Management System.html # Printable formatted report
├── README.md                              # Setup, execution guide and overview
└── statement.md                           # Formal course problem statement
```

---

## 🧩 OOP Concepts Implemented

1. **Abstraction**:
   - `Room` (abstract) and `Person` (abstract) define contract methods (`calculateTotalFee(months)`, `getRole()`, `getAmenities()`).
2. **Inheritance**:
   - `StandardRoom` and `DeluxeACRoom` inherit from base class `Room`.
   - `Student` inherits from base class `Person`.
3. **Polymorphism**:
   - Dynamic method dispatch on `calculateTotalFee(int months)` computes custom tariffs (Standard rooms incur flat utility charges; Deluxe AC rooms calculate electricity & AC maintenance surcharges).
4. **Encapsulation**:
   - All critical state variables (capacity, occupied beds, rent, student dues) are private, accessible solely through validated getters/setters.
5. **Custom Exceptions**:
   - `HostelException`, `RoomFullException`, and `InvalidDataException` protect against over-allocation and invalid financial transactions.
6. **Data Persistence**:
   - Reads and writes structured records from `hostel_data.txt`.

---

## 🧪 Test Suite Results (`java Main --test`)

```
========================================================
    RUNNING AUTOMATED UNIT & CONFLICT TEST HARNESS     
========================================================
[TEST 1] Verifying Room & Student Catalog Loading ...... PASS (Rooms: 6, Students: 5)
[TEST 2] Testing Polymorphic Fee Calculation ........... PASS (Standard: Rs.48000.0, Deluxe AC: Rs.105000.0)
[TEST 3] Testing Room Allocation & Bed Counter ......... PASS (Bed assigned in R103)
[TEST 4] Testing Capacity Limit & RoomFullException ... PASS (Prevented over-allocation)
[TEST 5] Testing Fee Payment & Overpay Protection ..... PASS (Payment recorded, dues updated)
[TEST 6] Testing Vacate With Dues Check ................ PASS (Blocked vacate with dues)
========================================================
Test Summary: 6 / 6 Tests Passed (Score: 100.0%)
========================================================
```
