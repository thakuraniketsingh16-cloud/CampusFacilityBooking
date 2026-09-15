# Smart Campus Facility & Lab Booking System

A modular, zero-dependency Java terminal application for managing, scheduling, and reserving university campus facilities, laboratories, seminar halls, and sports grounds with automatic conflict detection, role-based quota rules, and persistent single-file database storage.

Developed for **Programming in Java** course evaluation.

---

## 1. Project Overview & Problem Statement

In an academic campus environment, shared resources—such as high-performance computing labs, specialized hardware research facilities, seminar auditoriums, and sports courts—face frequent scheduling conflicts, double bookings, and lack of visibility.

This project provides a robust, command-line driven management and reservation system featuring:
- **Role-Based Access**: Specialized rules for Students (2-hour session limit, auditorium restrictions) and Faculty (extended 6-hour slots, direct auditorium reservations).
- **Automated Conflict Resolution**: Rigorous interval-overlap algorithm preventing simultaneous or overlapping bookings for any facility.
- **Single-File Text Database**: Persistent storage in `data/campus_data.txt` with formatted pipe-delimited records (`FAC|...`, `USR|...`, `BKG|...`).
- **Interactive & Headless CLI Modes**: Full interactive console menu for manual operations, alongside non-interactive CLI flags (`--demo`, `--test`) designed for automated grading scripts.
- **Pure Java Standard Library**: Zero external `.jar` dependencies. Compiles and executes cleanly out-of-the-box on Java 17, 21, and 26.

---

## 2. Environment Setup & Prerequisites

- **Java Development Kit (JDK)**: JDK 17 or higher (tested and verified on Java SE 26.0.1).
- **Operating System**: Windows, Linux, or macOS.
- **External Dependencies**: **None** (Built exclusively using standard `java.lang`, `java.util`, `java.io`, `java.time`).

### Verify Java Installation
Open your terminal and verify your JDK version:
```bash
java -version
javac -version
```

---

## 3. Step-by-Step Compilation & Execution Instructions

### A. Windows (Command Prompt / CMD)
1. **Compile**:
   ```cmd
   compile.bat
   ```
   *(Or manual command: `javac -d bin src\campus\models\*.java src\campus\exceptions\*.java src\campus\storage\*.java src\campus\service\*.java src\campus\*.java`)*

2. **Run Interactive Menu**:
   ```cmd
   run.bat
   ```
   *(Or manual command: `java -cp bin campus.Main`)*

3. **Run Automated Test Suite (Grading Validation)**:
   ```cmd
   run.bat --test
   ```

4. **Run Headless Demonstration**:
   ```cmd
   run.bat --demo
   ```

---

### B. Windows (PowerShell)
1. **Compile**:
   ```powershell
   .\compile.ps1
   ```
2. **Run Interactive Menu**:
   ```powershell
   .\run.ps1
   ```
3. **Run Automated Test Suite**:
   ```powershell
   .\run.ps1 --test
   ```

---

### C. Linux / macOS (Terminal / Bash)
1. Grant execute permissions (first time only):
   ```bash
   chmod +x compile.sh run.sh
   ```
2. **Compile**:
   ```bash
   ./compile.sh
   ```
3. **Run Interactive Menu**:
   ```bash
   ./run.sh
   ```
4. **Run Automated Test Suite**:
   ```bash
   ./run.sh --test
   ```
5. **Run Headless Demo**:
   ```bash
   ./run.sh --demo
   ```

---

## 4. Command Line Flags (Automated Evaluator Friendly)

The application supports command line arguments so automated grading pipelines can evaluate it without hanging on console input:

| Flag | Description |
| :--- | :--- |
| `(no args)` | Launches the interactive terminal menu with user switching and live reservation creation. |
| `--test` | Runs the 6-point self-test suite (data loading, booking creation, double-booking rejection, duration limit checks, permissions, cancellation & rebooking) and exits with status 0. |
| `--demo` | Executes an end-to-end non-interactive demonstration and outputs an analytics summary. |
| `--help` / `-h` | Prints available command line arguments. |

---

## 5. Project Architecture & Package Structure

```
CampusFacilityBooking/
├── README.md                      # Evaluator setup and execution guide
├── PROJECT_REPORT.md              # Academic submission report
├── compile.bat / compile.ps1      # Windows build scripts
├── run.bat / run.ps1              # Windows launch scripts
├── compile.sh / run.sh            # Linux/macOS build & launch scripts
├── .gitignore                     # Git ignore rules for compiled classes
├── data/
│   └── campus_data.txt            # Single-file text database
└── src/
    └── campus/
        ├── Main.java              # Application entry point & CLI handler
        ├── models/
        │   ├── Facility.java      # Base abstract facility
        │   ├── Lab.java           # Computer/IoT lab subclass
        │   ├── Hall.java          # Auditorium/seminar hall subclass
        │   ├── SportsCourt.java   # Badminton/tennis court subclass
        │   ├── User.java          # Base abstract user
        │   ├── Student.java       # Student subclass with 2hr limit
        │   ├── Faculty.java       # Faculty subclass with priority
        │   └── Booking.java       # Booking record with time interval logic
        ├── exceptions/
        │   ├── BookingException.java         # Base domain exception
        │   ├── SlotUnavailableException.java # Double-booking conflict exception
        │   └── InvalidInputException.java    # Bad input/format exception
        ├── storage/
        │   └── DataFileManager.java          # Single-file database I/O manager
        └── service/
            ├── BookingService.java           # Core business logic & analytics
            └── TestRunner.java               # Automated test suite
```

---

## 6. Key OOP Concepts Implemented

1. **Abstraction**:
   - `Facility` and `User` are defined as abstract classes specifying essential contract methods (`getSpecificDetails()`, `getMaxBookingHours()`, `canBookAuditoriumDirectly()`) while hiding low-level implementation.
2. **Inheritance**:
   - `Lab`, `Hall`, and `SportsCourt` extend `Facility`.
   - `Student` and `Faculty` extend `User`.
3. **Polymorphism**:
   - Dynamic method dispatch on `getMaxBookingHours()` enforces distinct quota limits (Students = 2 hrs, Faculty = 6 hrs).
   - Overridden `getSpecificDetails()` yields specialized specifications depending on whether the entity is a computer lab, seminar hall, or sports court.
4. **Encapsulation**:
   - Private/protected instance fields accessible strictly through validated getter and setter methods.
5. **Exception Handling**:
   - Custom checked exception hierarchy (`BookingException`, `SlotUnavailableException`, `InvalidInputException`) prevents unhandled runtime crashes and provides meaningful user guidance.
6. **Persistence & File I/O**:
   - Uses `BufferedReader` and `BufferedWriter` with try-with-resources to read and write the single structured file `data/campus_data.txt`.

---

## 7. Automated Test Suite Results

Running `run.bat --test` outputs:
```
========================================================
     RUNNING AUTOMATED SYSTEM & CONFLICT TEST SUITE     
========================================================
[TEST 1] Verifying Data File Loading ................... PASS (Facilities: 6, Users: 5)
[TEST 2] Testing Standard Booking Creation ............. PASS (Created ID: B105)
[TEST 3] Testing Slot Conflict Detection .............. PASS (Correctly prevented double-booking)
[TEST 4] Testing Student Max Duration Limit ............ PASS (Quota enforced: Selected duration exceeds allowed limit of 2 hrs for Student)
[TEST 5] Testing Auditorium Booking Restriction ........ PASS (Restriction enforced: Students cannot directly book grand auditoriums)
[TEST 6] Testing Cancellation & Slot Freeing ........... PASS (Slot freed upon cancellation and re-booked successfully)
========================================================
Test Summary: 6 / 6 Tests Passed (Score: 100.0%)
========================================================
```