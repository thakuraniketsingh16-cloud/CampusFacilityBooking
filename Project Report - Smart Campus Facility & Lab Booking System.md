# Project Report: Smart Campus Facility & Lab Booking System

---

## 1. Cover Page
* **Course Title:** Programming in Java
* **Evaluation Scheme:** Flipped Course Evaluation (Build Your Own Project)
* **Platform:** VITyarthi Portal
* **Project Title:** Smart Campus Facility & Lab Booking System
* **Student Name:** Aniket Singh
* **GitHub Username:** `thakuraniketsingh16-cloud`
* **Repository URL:** `https://github.com/thakuraniketsingh16-cloud/CampusFacilityBooking`
* **Submission Date:** September 2026
* **Implementation:** Pure Java SE 17+ (Single-File Architecture `Main.java`)

---

## 2. Introduction
In higher educational institutions, academic infrastructure is heavily shared across multiple faculties, departments, research groups, and student clubs. Facilities such as high-performance computing clusters, hardware prototyping laboratories, seminar auditoriums, and sports grounds experience intense booking competition. 

The **Smart Campus Facility & Lab Booking System** is a console-driven software solution engineered entirely in standard Java. The system incorporates Object-Oriented Programming (OOP) paradigms to model polymorphic facilities and user personas, enforces algorithmic time-interval collision detection, implements role-based duration quotas, and persists state in a structured flat file database (`campus_data.txt`).

---

## 3. Problem Statement
Manual booking using paper logbooks or informal messaging introduces serious operational flaws:
1. **Simultaneous Booking Collisions:** Multiple groups arrive at the same facility having received overlapping approvals.
2. **Resource Monopolization:** Students reserve specialized computer laboratories for full days without duration limits.
3. **Unauthorized Bookings:** High-capacity auditoriums require institutional approvals, yet lack access barriers.
4. **Disjoint Scheduling Records:** Lack of persistent data causes scheduling disputes and prevents utilization analysis.

---

## 4. Functional Requirements
The system contains three major functional modules:

### Module 1: Facility Catalog & Schedule Management
* Maintains polymorphic representations of campus facilities: `Lab`, `Hall`, `SportsCourt`.
* Displays capacity, location, hardware specs, and air-conditioning status.
* Queries date-filtered schedules displaying active confirmed bookings chronologically.

### Module 2: Reservation & Collision Resolution Engine
* Validates user existence, operating hours (07:00 to 22:00), and timestamp sanity.
* Evaluates mathematical interval overlap: `start1 < end2 && end1 > start2`.
* Enforces role quotas: Students max 2 hours; Faculty max 6 hours.
* Restricts direct grand auditorium reservations to faculty users.
* Supports booking cancellation, instantly freeing the slot.

### Module 3: Campus Analytics & Reporting
* Summarizes total facilities, registered users, confirmed vs. cancelled bookings.
* Computes booking frequency per facility using Java Stream API.
* Groups and displays campus-wide activity by academic department.

---

## 5. Non-Functional Requirements
1. **Performance:** Sub-millisecond conflict checks and $O(1)$ in-memory lookups using `LinkedHashMap`.
2. **Reliability:** Deterministic validation and atomic flat-file updates guaranteeing persistence.
3. **Usability:** Formatted console tables, clear prompts, and dual interactive/headless CLI flags.
4. **Maintainability:** Strict adherence to OOP principles with single-responsibility class modularity.
5. **Security & Authorization:** Role-based permissions preventing unauthorized student bookings.
6. **Error Handling Strategy:** Custom checked exception hierarchy preventing application crashes.

---

## 6. System Architecture
The application adheres to a 3-Tier Layered Console Architecture in pure Java:
* **Presentation Tier (`Main.java`):** Console I/O, user session state, menus, and headless CLI flags (`--test`, `--demo`).
* **Business Logic Tier (`CampusDatabase`):** Validates intervals, enforces quotas, resolves collisions, and computes metrics.
* **Data Access Tier (`campus_data.txt`):** Persistent flat-file serialization and deserialization via buffered character streams.

---

## 7. Design Diagrams

### 7.1 Use Case Diagram
* **Actors:** Student, Faculty, Administrator.
* **Use Cases:** Browse Facilities, Check Schedule, Make Reservation, Cancel Booking, View Analytics, Run Test Suite.

### 7.2 Workflow Diagram
1. User selects facility & submits desired date and time interval.
2. System checks facility maintenance status.
3. System verifies duration against user role quota (Student = 2 hrs, Faculty = 6 hrs).
4. System executes interval overlap algorithm against all existing bookings.
5. If collision occurs, `SlotUnavailableException` is thrown; otherwise, record is persisted to `campus_data.txt`.

### 7.3 Sequence Diagram
* `User` &rarr; `Main.java` &rarr; `CampusDatabase.createBooking()` &rarr; `Booking.overlapsWith()` &rarr; `File Persistence` &rarr; `Success Message`.

### 7.4 Class / Component Diagram
* `Facility` (Abstract) &rarr; `Lab`, `Hall`, `SportsCourt`.
* `User` (Abstract) &rarr; `Student`, `FacultyMember`.
* `Booking` entity referencing `Facility` and `User`.
* `CampusDatabase` managing registries and persistence.

### 7.5 Database & Storage Design (ER Diagram)
* **USER** `(PK userId, name, role, email, department, regNo/empId)` (1 to N with BOOKING).
* **FACILITY** `(PK id, name, type, capacity, location, extraSpecs)` (1 to N with BOOKING).
* **BOOKING** `(PK bookingId, FK userId, FK facilityId, date, startTime, endTime, status, purpose)`.

---

## 8. Design Decisions & Rationale
1. **Single-File `Main.java` Architecture:** Eliminates package declaration and classpath mismatch issues when evaluating across Windows, Linux, and macOS terminal environments.
2. **Zero Third-Party Dependencies:** Guarantees instant compilation on standard JDK 17+ without requiring Maven, Gradle, or external `.jar` downloads.
3. **Flat Text Database (`campus_data.txt`):** Human-readable pipe-delimited format allows evaluators to inspect records in any text editor without installing database engines.
4. **Mathematical Interval Overlap Algorithm:** Evaluates `start1 < end2 && end1 > start2` to prevent overlaps while cleanly permitting adjacent, contiguous bookings.

---

## 9. Implementation Details
* **Dynamic Polymorphism:** `getMaxBookingHours()` and `getSpecificDetails()` dynamically dispatched at runtime.
* **Encapsulation:** Private/protected fields accessed strictly via validated methods.
* **Exception Hierarchy:** `CampusBookingException`, `SlotUnavailableException`, `InvalidInputException` separate business failures from unexpected crashes.

---

## 10. Screenshots / Results
* **Main Menu & Login:** Clean interactive console UI with active user tracking.
* **Facilities Catalog:** Tabular display showing IDs, types, capacities, locations, and hardware specifications.
* **Reservation Engine:** Confirmation displaying reference ID, reserved times, and status.
* **Conflict Prevention:** Warning message reporting conflicting reservation ID and time window.
* **Analytics Report:** Breakdown of bookings per facility and activity per academic department.

---

## 11. Testing Approach
An automated self-diagnostic test harness (`java Main --test`) executes 6 comprehensive test scenarios:

| Test # | Test Case Description | Expected Result | Actual Result | Verdict |
| :--- | :--- | :--- | :--- | :--- |
| 1 | Catalog Loading | Parse entities from data file | 6 facilities, 5 users loaded | **PASS** |
| 2 | Booking Creation | Create valid reservation | Booking B107 created (CONFIRMED) | **PASS** |
| 3 | Collision Check | Block double-booking same room | `SlotUnavailableException` caught | **PASS** |
| 4 | Student Quota | Reject 4-hour booking by student | `InvalidInputException` caught (limit 2 hrs) | **PASS** |
| 5 | Auditorium Restriction | Block student from booking hall directly | `CampusBookingException` caught | **PASS** |
| 6 | Slot Re-allocation | Free slot on cancel & allow rebook | Slot re-booked successfully | **PASS** |

**Overall Score:** 6 / 6 Tests Passed (100.0%)

---

## 12. Challenges Faced
* **Interval Edge Cases:** Ensuring back-to-back contiguous bookings (e.g. 10:00-12:00 and 12:00-14:00) do not trigger false-positive collisions.
* **Test Idempotency:** Ensuring automated tests remain 100% repeatable across multiple test runs without corrupting persistent data.
* **Scanner Buffer Management:** Preventing skipped prompts when reading numbers followed by full lines.

---

## 13. Learnings & Key Takeaways
* Effective application of dynamic polymorphism in enforcing business rules.
* Structuring custom checked exceptions to provide actionable error feedback in terminal applications.
* Designing automated diagnostic CLI modes for automated grading pipelines.

---

## 14. Future Enhancements
* REST API & Web Dashboard using Spring Boot and React.
* Relational database migration to PostgreSQL with JPA/Hibernate.
* QR-code entry pass generation for laboratory physical access.
* Automated SMS and email confirmation dispatches.

---

## 15. References
1. Oracle Corporation. *Java Platform, Standard Edition Documentation (JDK 17/21/26)*.
2. Cay S. Horstmann. *Core Java Volume I — Fundamentals (12th Edition)*, Prentice Hall, 2022.
3. Erich Gamma et al. *Design Patterns: Elements of Reusable Object-Oriented Software*, Addison-Wesley, 1994.
4. VITyarthi Course Management. *Programming in Java Flipped Course Guidelines & Evaluation Rubric*, 2026.
