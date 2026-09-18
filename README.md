# Smart Campus Facility & Lab Booking System (CFBS)

A modular, console-driven Java application for scheduling, managing, and reserving university academic facilities, computing laboratories, seminar halls, and sports courts with mathematical interval collision avoidance, role-based duration quotas, and persistent flat-file storage.

Developed for **Programming in Java** flipped course evaluation on the **VITyarthi** platform.

---

## 📸 Screenshots

| Facilities Catalog | Reservation & Conflict Check |
| :---: | :---: |
| ![Facilities](screenshots/dashboard.png) | ![Bookings](screenshots/fees.png) |

| User Session & Menu | Diagnostic Test Suite |
| :---: | :---: |
| ![Menu](screenshots/login.png) | ![Tests](screenshots/students.png) |


---

## 1. Project Title & Overview
* **Project Title:** Smart Campus Facility & Lab Booking System
* **Course:** Programming in Java
* **Platform:** VITyarthi
* **Author:** Aniket Singh (`thakuraniketsingh16-cloud`)

### Project Overview
Modern universities feature valuable shared infrastructure—including GPU-accelerated computing labs, embedded systems laboratories, high-capacity seminar halls, and sports courts. Managing these resources across diverse user groups (Students and Faculty) without a centralized booking engine leads to double-booking collisions, students monopolizing computer labs, and unauthorized bookings.

This project delivers a 100% terminal-executable, zero-dependency Java application providing automated interval collision checks, polymorphic duration quotas, and persistent file storage.

---

## 2. Key Features

* **Three Major Functional Modules:**
  1. *Facility Catalog & Schedule Management*: Real-time schedule lookup and hardware specifications.
  2. *Reservation Engine with Conflict Resolution*: Algorithmic collision detection `[start1 < end2 && end1 > start2]` preventing double-booking.
  3. *Campus Analytics & Utilization Reporting*: Aggregates booking frequencies per facility and activity by department.
* **Role-Based Fair Usage Policies:**
  * *Students*: Restricted to a 2-hour maximum per booking session; grand auditoriums restricted.
  * *Faculty*: Extended 6-hour reservations with direct auditorium booking access.
* **Slot Cancellation & Freeing**: Immediately returns cancelled slots to the availability pool.
* **Single-File Persistent Database**: Flat-file database (`campus_data.txt`) using pipe-delimited records.
* **Automated Evaluator Testing**: Built-in 6-point self-diagnostic harness running non-interactively via `--test`.

---

## 3. Technologies & Tools Used
* **Programming Language:** Java SE (Standard Edition) 17+ (Tested & verified on Java 26.0.1)
* **Libraries:** Standard Java SE Library (`java.lang`, `java.util`, `java.io`, `java.time`) — **Zero External JAR Dependencies**
* **Version Control:** Git & GitHub
* **Storage:** Flat file database (`campus_data.txt`)

---

## 4. Steps to Install & Run the Project

### Prerequisites
* JDK 17 or higher installed (`java -version` and `javac -version`).

### Step 1: Clone the Repository
```bash
git clone https://github.com/thakuraniketsingh16-cloud/CampusFacilityBooking.git
cd CampusFacilityBooking
```

### Step 2: Compile (Single-File)
```bash
javac Main.java
```

### Step 3: Run Interactive Console UI
```bash
java Main
```

---

## 5. Instructions for Testing

The system provides an automated non-interactive test runner designed specifically for grading pipelines:

### Run Automated Test Suite
```bash
java Main --test
```

### Expected Output:
```
========================================================
     RUNNING AUTOMATED SYSTEM & CONFLICT TEST SUITE     
========================================================
[TEST 1] Verifying Facility & User Catalog Loading .... PASS (Facilities: 6, Users: 5)
[TEST 2] Testing Standard Booking Creation ............. PASS (Created ID: B107)
[TEST 3] Testing Slot Conflict & Overlap Prevention ... PASS (Prevented collision: Slot already reserved)
[TEST 4] Testing Student Max Duration Limit (2 hrs) .... PASS (Quota enforced: Requested duration exceeds allowed limit)
[TEST 5] Testing Grand Auditorium Booking Restriction . PASS (Restriction enforced: Students cannot directly book grand auditoriums)
[TEST 6] Testing Cancellation & Slot Freeing ........... PASS (Slot freed upon cancellation and re-booked)
========================================================
Test Summary: 6 / 6 Tests Passed (Score: 100.0%)
========================================================
```

### Run Non-Interactive Demonstration Mode
```bash
java Main --demo
```

---

## 6. Repository File Layout
```
├── screenshots/                                                      # UI capture screenshots
│   ├── dashboard.png
│   ├── fees.png
│   ├── login.png
│   └── students.png
├── Main.java                                                         # Complete self-contained Java source code
├── Project Report - Smart Campus Facility & Lab Booking System.pdf   # Official 15-section PDF report
├── Project Report - Smart Campus Facility & Lab Booking System.md    # Markdown formatted report
├── Project Report - Smart Campus Facility & Lab Booking System.html  # Printable HTML report
├── README.md                                                         # Setup, execution guide and overview
├── statement.md                                                      # Problem statement specification
└── campus_data.txt                                                   # Single-file persistent text database
```
