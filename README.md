# 🫀 OrganShare

### Organ Donation & Emergency Resource Coordination Android Application

OrganShare is an Android application designed to support organ-donation coordination and emergency medical-resource management by connecting donors, hospitals, and emergency resources in one platform.

The application includes donor and hospital workflows, hospital-bed availability, emergency resource management, transportation coordination, blood resources, volunteers, medical supplies, and emergency requests.

---

## 📱 App Screenshots

Here are some screenshots of the OrganShare Android application.

👉 [View all application screenshots](./screenshots/app)

👉 [View development/code screenshots](./screenshots/developer)

---

## ✨ Key Features

### 🧑 Donor Module

- Donor registration and authentication
- Donor profile management
- Organ donation information and coordination
- View available hospitals
- View hospital bed availability
- View hospital details
- Request emergency transportation

### 🏥 Hospital Module

- Hospital registration and authentication
- Hospital profile management
- Live emergency bed management
- Blood resource management
- Transport resource management
- Volunteer management
- Medical-supply management
- Emergency request management
- Resource allocation and tracking

### 🚑 Emergency Resource Coordination

- Hospital bed availability
- ICU and emergency-bed tracking
- Blood-resource management
- Emergency transportation
- Volunteer coordination
- Medical-supply tracking
- Emergency resource requests
- Resource allocation and history

---

## 👥 User Roles

| Role                     | Main Responsibilities                                                         |
| ------------------------ | ----------------------------------------------------------------------------- |
| 🧑 Donor                 | Manage profile, view hospitals, check availability and request transportation |
| 🏥 Hospital              | Manage beds, blood, supplies, transport and volunteers                        |
| 🚑 Emergency Coordinator | Coordinate emergency resources and requests                                   |
| 👨‍💼 Admin             | Platform and resource management                                              |

---

## 🔄 How OrganShare Works

```text
                         🫀 OrganShare
                              │
              ┌───────────────┴───────────────┐
              │                               │
           🧑 Donor                       🏥 Hospital
              │                               │
       ┌──────┼──────┐              ┌─────────┼─────────┐
       │      │      │              │    │     │    │   │
    Hospitals Beds  Transport     Beds Blood Transport Volunteers
       │                             │
       │                             ├── Supplies
       │                             └── Emergency Requests
       │
       └──────────────► Emergency Coordination
                                │
                                ▼
                         Resource Allocation
```

---

## 🛠️ Technology Stack

### Android

- Java
- Android SDK
- XML
- Android Studio
- Gradle

### Backend & Database

- Firebase Authentication
- Cloud Firestore

### Development

- Git
- GitHub

---

## 🔥 Firebase Integration

OrganShare uses Firebase for authentication and cloud data management.

### Firebase Authentication

Used for user registration and login.

### Cloud Firestore

Used to store application and emergency-resource data.

Key collections include:

```text
emergency_hospital_beds
emergency_blood_resources
emergency_transport_resources
emergency_transport_requests
emergency_volunteers
emergency_medical_supplies
emergency_resource_requests
emergency_resource_allocations
emergency_resource_history
hospitals
donors
users
```

---

## 📸 Screenshots

The `screenshots` folder contains the screenshots extracted from the OrganShare application showcase document.

### Application Screens

The `screenshots/app` folder contains the first 30 application screenshots.

### Development Screens

The `screenshots/developer` folder contains the remaining development/code screenshots.

This keeps the main README clean while still making the complete project visual documentation available in the repository.

---

## 📦 Download APK

Download the latest Android APK from GitHub Releases:

👉 **[Download the latest OrganShare APK](../../releases/latest)**

---

## 🚀 Installation

### For Users

1. Open the latest GitHub Release.
2. Download the APK.
3. Transfer it to an Android device if necessary.
4. Allow installation from unknown sources when Android asks for permission.
5. Install and open OrganShare.

### For Developers

Clone the repository:

```bash
git clone https://github.com/Prajakta2745/OrganShareApp.git
```

Open the project in Android Studio, configure the required Firebase project, sync Gradle, and run the application on an Android device or emulator.

---

## 🎯 Project Objectives

OrganShare aims to:

- Simplify donor-hospital coordination
- Improve visibility of hospital emergency resources
- Centralize emergency resource information
- Support transportation and volunteer coordination
- Improve emergency resource management
- Help users make faster decisions during emergency situations

---

## 🔮 Future Enhancements

- 📍 GPS-based hospital discovery
- 🔔 Push notifications
- 🚑 Live emergency transport tracking
- 🏥 Hospital verification workflow
- 📊 Advanced analytics dashboard
- 🌐 Multi-language support
- 🤖 Intelligent hospital/resource matching

---

## 👩‍💻 Developer

### Prajakta Patil

Computer Science Engineering (AI & ML)

- GitHub: [Prajakta2745](https://github.com/Prajakta2745)
- Project Repository: [OrganShareApp](https://github.com/Prajakta2745/OrganShareApp)

---

## ⭐ Project Status

**Current Release:** `v1.1.0`

**Platform:** Android

**Project Type:** Academic / Final Year Project

---

⭐ If you find this project useful, consider starring the repository.
