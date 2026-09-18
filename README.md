# 🏫 Smart Campus Facility & Lab Booking System (CFBS)

## 📖 Overview

The Smart Campus Facility & Lab Booking System is a desktop application developed using Java Swing to simplify physical infrastructure administration on college and university campuses. It automates the reservation of high-tech computing labs, grand auditoriums, and sports arenas through a single, easy-to-use dashboard. 

The application is developed as a single Java source file (`Main.java`) and provides a modern, dark-themed graphical user interface using Java Swing. It enforces role-based rules, ensures fair usage by preventing double-booking overlaps, automatically seeds demo data, and saves all records into a lightweight flat-file database without requiring external servers.

---

## ✨ Features

- Modern Dark Mode GUI
- Role-Based Access Control (Student vs. Faculty)
- Facility Catalog Management
- Automatic Collision Detection
- Quota Enforcement (2-hour limit for Students, 6 for Faculty)
- My Bookings Management
- One-Click Cancellation System
- Live Campus Analytics
- Dynamic Facility Utilization Tracking
- Automated Background Data Saving
- Test Harness for Automated Diagnostics
- Flat-File Persistent Storage

---

## 🛠 Technologies / Tools Used

- Java 8+
- Java Swing (GUI)
- Java AWT
- Java Collections Framework
- Java Stream API
- Java File I/O

---

## 🚀 Steps to Install & Run the Project

1. Ensure you have the Java Development Kit (JDK) installed on your system.
2. Download or copy the `Main.java` file to a local directory on your computer.
3. Open a terminal or command prompt in that specific directory.
4. Compile the source code by running the command: `javac Main.java`
5. Launch the application by running the command: `java Main`

*Note: Upon the first launch, the system will automatically generate a `campus_data.txt` file containing pre-seeded demo users, facilities, and bookings.*

---

## 🧪 Instructions for Testing

- **GUI Testing:** Run `java Main`, select a facility from the "New Booking" tab, and try booking overlapping times to see the system automatically reject the conflict. Switch user profiles from the top right to test role-based hour limits.
- **Automated Tests:** Run the command `java Main --test` to launch the built-in diagnostic suite, which actively verifies quotas, collision logic, and permissions without launching the UI.
- **Headless Demo:** Run the command `java Main --demo` to execute a rapid text-based demonstration of the system engine directly in the console.

---

## 📸 Screenshots

| Campus Analytics | Booking Window |
|---|---|
| ![Campus Analytics](screenshots/Campus%20Analytics.png)) | ![Booking Window](screenshots/Booking%20window.png) |
