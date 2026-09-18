# 📄 Project Statement: Smart Campus Facility & Lab Booking System (CFBS)

## 🛑 Problem Statement
Manual management of shared physical infrastructure in modern educational institutions—such as high-performance computing labs, grand auditoriums, and sports arenas—often leads to administrative inefficiencies. Common challenges include frequent double-bookings, the inability to enforce fair-usage time quotas, and unauthorized access to restricted facilities (e.g., students booking grand auditoriums without faculty endorsement). There is a critical need for an automated, rule-driven system to schedule, validate, and manage these resources seamlessly, without the massive overhead of complex enterprise software.

## 🎯 Scope of the Project
The scope of this project encompasses the development of a standalone, desktop-based Java application to manage campus facility reservations locally. 

**In-Scope:**
- A centralized catalog of campus facilities (Computing Labs, Auditoriums/Halls, Sports Courts).
- A reservation engine featuring mathematical collision detection.
- Role-Based Access Control (RBAC) to enforce differing business rules for students and faculty.
- A localized flat-file persistent storage system (`campus_data.txt`).
- A live analytics dashboard for tracking resource utilization.
- An automated self-diagnostic test harness.

**Out of Scope:**
- Online payment/fee processing.
- Live hardware/IoT integration (e.g., automated door locks).
- Cloud-based multi-node database synchronization (e.g., MySQL/AWS).

## 👥 Target Users
The system is designed for three primary categories of users within an educational institution:
1. **Students:** Who need to search for available computing labs or sports courts for project work or recreation, and book them within their allowed quota (e.g., maximum of 2 hours per session).
2. **Faculty Members:** Who require priority booking for larger facilities, such as seminar halls or grand auditoriums, for lectures and events, utilizing extended time quotas (e.g., up to 6 hours).
3. **Campus Administrators:** Who utilize the analytics dashboard to monitor facility utilization, track departmental activity, and manage the campus infrastructure efficiently.

## ✨ High-Level Features
- **Interactive Dark Mode GUI:** A modern, multi-tabbed Java Swing interface for seamless navigation through catalogs, personal bookings, and data analytics.
- **Role-Based Access Control (RBAC):** Distinct booking privileges and quota enforcements based on the user's role (Student vs. Faculty).
- **Smart Collision Detection:** Mathematical interval validation to instantly prevent scheduling conflicts and double-bookings on the same day.
- **Live Campus Analytics:** Dynamic statistical reporting that tracks facility utilization rates and departmental booking activities using the Java Stream API.
- **Persistent Flat-File Storage:** Lightweight, zero-dependency data persistence utilizing Java File I/O.
- **Automated Self-Diagnostics:** A built-in headless test suite that automatically validates the core logic (quotas, collisions, permissions) to ensure system integrity.
