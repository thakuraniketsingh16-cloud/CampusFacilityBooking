# Problem Statement: Smart Campus Facility & Lab Booking System

## Course Details
* **Course Title:** Programming in Java
* **Academic Term:** Flipped Course Project Evaluation
* **Submission Platform:** VITyarthi

---

## 1. Problem Description
Across a modern university campus, shared academic facilities—including high-performance computing labs, IoT and embedded hardware laboratories, grand conference auditoriums, and sports courts—face continuous reservation conflicts, double-bookings, and lack of visibility.

Traditional manual registers or ad-hoc email requests suffer from:
1. **Slot Overlap & Collision:** Multiple student groups or faculties reserving the same room for identical or overlapping time intervals.
2. **Resource Monopolization:** Students over-reserving computing laboratories for excessive durations without policy limits.
3. **Unauthorized Bookings:** Students booking high-capacity auditoriums without administrative or faculty advisor endorsement.
4. **Disjoint Records:** Inability to track facility schedules, utilization rates, and department-wise reservations in real time.

---

## 2. Project Objectives
The objective of this project is to engineer a lightweight, console-driven Java application utilizing Object-Oriented Programming (OOP) paradigms:

1. **Object-Oriented Architecture:**
   - **Abstraction:** Abstract base classes (`Facility`, `User`) defining contracts.
   - **Inheritance:** Concrete subclasses (`Lab`, `Hall`, `SportsCourt` extending `Facility`; `Student`, `FacultyMember` extending `User`).
   - **Polymorphism:** Dynamic method dispatch on `getMaxBookingHours()` and facility details.
   - **Encapsulation:** Private data members with validated accessors and time interval overlap checks.
2. **Conflict & Overlap Resolution:**
   - Strict interval overlap detection logic `[start1 < end2 && end1 > start2]` preventing double-booking.
3. **Role-Based Booking Quotas:**
   - Students constrained to a maximum of 2 hours per session.
   - Faculty permitted up to 6 hours for research and seminars.
   - Restrict direct grand auditorium reservations to faculty users.
4. **Persistent Data Storage:**
   - Flat file database (`campus_data.txt`) using structured pipe-delimited records.
5. **Evaluator-Friendly Execution:**
   - Command-line flags (`--test`, `--demo`) for automated evaluation pipelines.

---

## 3. Scope & Deliverables
* **`Main.java`**: Self-contained Java source file containing all class definitions and CLI controller.
* **`screenshots/`**: Folder for capturing and displaying interface execution screens.
* **`Project Report - Smart Campus Facility & Lab Booking System.pdf`**: Comprehensive academic report with UML diagrams.
* **`README.md`**: Complete setup and execution guide.
