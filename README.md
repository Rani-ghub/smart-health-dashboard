# 🩺 Smart Health Dashboard

A Java Swing-based desktop application for tracking, filtering, and visualizing personal health data. Users can log blood pressure and heart rate readings, filter records by date and username, export data to CSV, and view interactive charts grouped by user.

---

## ✨ Features

- Add health data with automatic timestamping
- Filter records by date range and username
- View all users' data or focus on individual profiles
- Export health records to CSV format
- Visualize trends with user-wise heart rate and blood pressure charts
- Clean, responsive UI built with Swing and JDatePicker

---

## 🛠 Tech Stack

- Java (Swing)
- JFreeChart (for chart visualization)
- JDatePicker (for date selection)
- MySQL (via JDBC)
- Maven (for dependency management)

---

## 📦 Maven Configuration


<groupId>com.RaniAutomation</groupId>
<artifactId>smart-health-dashboard</artifactId>
<version>1.0.0</version>

---

## ✨ Prerequisites

## MySQL Database Setup
1. Create a database named smart_health:
#sql querry
CREATE DATABASE smart_health;

#2. Table: users
#Stores registered users.
#sql querry
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

#3. Table: health_data
#Stores health records linked to usernames.
#sql querry
CREATE TABLE health_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    blood_pressure VARCHAR(10) NOT NULL,
    heart_rate INT NOT NULL,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

#4. JDBC Configuration
#Update your DBManager.java with your MySQL credentials:

java
public static String db_UserName = "your_mysql_username";
public static String db_Password = "your_mysql_password";
public static String port = "your_port";

## 🚀 Getting Started
#1. Clone the Repository
#git bash
git clone https://github.com/your-username/smart-health-dashboard.git

#2. Import into Your IDE
#Open the project in IntelliJ, Eclipse, or VS Code as a Maven project.

---
## ✨ Run the Application
Launch Main.java to start the dashboard.

## ✨ Screenshots
#1.Login

<img width="221" height="107" alt="image" src="https://github.com/user-attachments/assets/787b2c28-363d-417e-9e53-4a21d76a0381" />




---



#2.Dashboard--Add data, Filter data, Export to CSV, Show Chart

<img width="958" height="332" alt="image" src="https://github.com/user-attachments/assets/6cbdc9fa-3917-4736-b67a-5ea3cf5ac58f" />



---



#3.Chart View

<img width="959" height="506" alt="image" src="https://github.com/user-attachments/assets/61d1866a-a30a-4534-b844-864e5f66e7ff" />


---

