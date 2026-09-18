# statement.md — Project Statement & Scope Specification

## Project Title
**Smart Campus Facility & Lab Booking System (CFBS)**

## Course Information
* **Course Title:** Programming in Java
* **Evaluation Scheme:** Flipped Course Evaluation (Build Your Own Project)
* **Platform:** VITyarthi

---

## 1. Problem Statement
In higher educational institutions such as university campuses, shared physical resources—including high-performance computing centers, embedded systems and IoT hardware laboratories, seminar halls, conference auditoriums, and sports courts—face frequent reservation conflicts, resource monopolization, and coordination bottlenecks. 

Traditional paper registers or ad-hoc emails fail to provide real-time schedule visibility, leading to overlapping slot reservations, students hogging lab workstations for excessive hours, and unauthorized bookings of high-capacity auditoriums without faculty approval. A centralized, rule-enforced console system is required to automate reservation validation, enforce role-based duration quotas, and maintain an immutable historical record of all bookings.

---

## 2. Scope of the Project
The **Smart Campus Facility & Lab Booking System** is an authentic, console-based Java application built purely with standard Java SE (JDK 17+ / Java 26) with zero external library dependencies. 

The scope includes:
* Modeling multiple categories of physical facilities with distinct capacity, hardware specs, and location attributes.
* Managing user roles (Students and Faculty) with dynamic polymorphic session constraints.
* Enforcing algorithmic time-slot conflict detection `[start1 < end2 && end1 > start2]` across all booking requests.
* Persisting all system state across executions in a structured, single flat-file database (`campus_data.txt`).
* Supporting dual execution workflows: interactive console menus for users and non-interactive command-line test flags (`--test`, `--demo`) for automated evaluation environments.

---

## 3. Target Users
1. **Students**:
   * Browse available laboratories, seminar rooms, and sports courts.
   * Reserve facilities for academic projects, study groups, and extracurricular clubs.
   * Subject to fair-usage policy: maximum 2 hours per booking session; restricted from booking large auditoriums directly.
2. **Faculty Members & Researchers**:
   * Reserve specialized hardware laboratories and seminar halls for lectures, research sessions, and conferences.
   * Extended booking privileges: up to 6 hours per reservation; authorized to directly reserve grand auditoriums.
3. **Campus Administrators / Wardens / Evaluators**:
   * Inspect real-time facility availability schedules.
   * View campus-wide analytics, booking frequencies, and department activity metrics.
   * Execute built-in diagnostic test suites for automated grading and verification.

---

## 4. High-Level Features
* **Facility Catalog & Schedule Explorer**: Real-time browsing of computing labs, auditoriums, and sports courts with complete hardware/amenity specifications and date-filtered confirmed bookings.
* **Intelligent Collision-Free Reservation Engine**: Mathematical interval-overlap conflict detection rejecting overlapping reservations for the same room.
* **Role-Based Quota & Authorization Enforcement**: Polymorphic dispatch applying differentiated session limits (2 hrs vs 6 hrs) and access permissions.
* **Booking Cancellation & Slot De-allocation**: Secure cancellation by booking owners or faculty administrators that immediately frees the slot for subsequent reservations.
* **Department & Facility Analytics**: Live statistical aggregation showing total bookings, active reservations, facility utilization counts, and department engagement.
* **Automated Self-Diagnostic Test Suite**: Built-in 6-point automated verification engine (`--test`) validating data parsing, conflict rejection, quota rules, and rebooking with zero keyboard interaction.
