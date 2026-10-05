# 🫀 OrganShare

### Organ Donation & Emergency Resource Coordination Android Application

OrganShare is an Android application that helps coordinate **organ donation and emergency medical resources** by connecting donors, hospitals, organ/blood banks, delivery persons, and admins on one platform.

---

## ✨ Key Features

- Organ donation coordination
- Hospital and bed availability
- Blood resource management
- Emergency transportation
- Volunteer management
- Medical supply management
- Emergency requests
- Resource allocation
- Delivery coordination
- Role-based access

---

## 👥 User Roles

### 🧑 Donor
Donors can register, manage their profile, provide organ donation information, view available hospitals and bed availability, and request emergency transportation.

### 🏥 Hospital
Hospitals can manage beds, ICU/emergency resources, blood, transportation, volunteers, medical supplies, and emergency requests.

### 🏦 Organ Bank / Blood Bank
Manages organ and blood-related information and coordinates with donors and hospitals for donation and emergency requirements.

### 🚚 Delivery Person
Handles assigned transportation requests, views pickup and destination details, coordinates deliveries, and updates transportation status.

### 👨‍💼 Admin
Manages users, hospitals, resources, and overall platform activities.

---

## 🔄 System Workflow

```text
🧑 Donor
   ↓
🏥 Hospital ←→ 🏦 Organ/Blood Bank
   ↓
Emergency Resources
   ↓
🚚 Delivery Person
   ↓
Organ / Resource Delivery
   ↓
Destination Hospital

👨‍💼 Admin → Overall System Management
````

---

## 🛠️ Technology Stack

* **Java**
* **Android SDK**
* **XML**
* **Android Studio**
* **Firebase Authentication**
* **Cloud Firestore**
* **Gradle**
* **Git & GitHub**
* **Sentry**

---

## 🔥 Database

OrganShare uses **Cloud Firestore** for storing application and emergency-resource data.

Main collections:

```text
users
donors
hospitals
emergency_hospital_beds
emergency_blood_resources
emergency_transport_resources
emergency_transport_requests
emergency_volunteers
emergency_medical_supplies
emergency_resource_requests
emergency_resource_allocations
emergency_resource_history
```

---

## 📸 Screenshots

### 📱 Application Screenshots

👉 [View Application Screenshots](./screenshots/app)

### 💻 Development Screenshots

Development screenshots include Firebase/Firestore database structures, implementation details, resource management, and Delivery Person implementation.

👉 [View Development Screenshots](./screenshots/developer)

---

## 📦 Download APK

👉 [Download Latest OrganShare APK](../../releases/latest)

---

## 🚀 Installation

```bash
git clone https://github.com/Prajakta2745/OrganShareApp.git
```

Open the project in **Android Studio**, configure Firebase, sync Gradle, and run the application.

---

## 🎯 Project Objective

OrganShare aims to simplify **organ donation coordination and emergency medical-resource management** by providing a centralized platform for donors, hospitals, organ/blood banks, delivery persons, and administrators.

---

## 👩‍💻 Developer

### Prajakta Patil

**Computer Science Engineering (AI & ML)**
**Bharati Vidyapeeth's College of Engineering, Kolhapur**

* [GitHub Profile](https://github.com/Prajakta2745)
* [OrganShare Repository](https://github.com/Prajakta2745/OrganShareApp)

---

## ⭐ Project Status

**Release:** `v1.1.0`
**Platform:** Android
**Type:** Academic / Final Year Project

⭐ If you find this project useful, consider starring the repository.

```

This is the version I recommend for your GitHub: **short, professional, and focused on what the project actually does.**
```
