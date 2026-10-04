# 🫀 OrganShare

### Organ Donation & Emergency Resource Coordination Android Application

OrganShare is an Android application designed to support organ-donation coordination and emergency medical-resource management through a centralized platform.

The application connects five main user roles:

- 🧑 Donor
- 🏥 Hospital
- 🏦 Organ Bank / Blood Bank
- 🚚 Delivery Person
- 👨‍💼 Admin

The system supports organ donation coordination, hospital-bed availability, blood resources, emergency transportation, volunteers, medical supplies, emergency requests, resource allocation, and delivery coordination.

---

## 👥 User Roles

| Role | Main Responsibilities |
|------|------------------------|
| 🧑 Donor | Manage donor profile, provide donation information, view hospitals, check hospital availability and request transportation |
| 🏥 Hospital | Manage hospital information, beds, blood resources, transport, volunteers, medical supplies and emergency requests |
| 🏦 Organ Bank / Blood Bank | Coordinate organ and blood-related information and support donation and hospital coordination |
| 🚚 Delivery Person | Handle assigned transportation requests, coordinate pickup and delivery, and update delivery status |
| 👨‍💼 Admin | Manage and monitor users, hospitals, resources and overall platform activities |

---

# ✨ Key Features

## 🧑 Donor Module

- Donor registration and authentication
- Donor profile management
- Organ donation information
- View available hospitals
- View hospital bed availability
- View hospital details
- Check available emergency resources
- Select a suitable hospital
- Request emergency transportation
- Provide emergency and medical information

---

## 🏥 Hospital Module

- Hospital registration and authentication
- Hospital profile management
- Live emergency bed management
- ICU and emergency-bed management
- Blood resource management
- Transport resource management
- Volunteer management
- Medical-supply management
- Emergency request management
- Resource allocation
- Emergency resource tracking

### Hospital Emergency Resources

Hospitals can manage:

- 🛏️ Hospital Beds
- 🩸 Blood Resources
- 🚑 Transport Resources
- 👥 Volunteers
- 💊 Medical Supplies
- 🚨 Emergency Requests
- 📦 Resource Allocations

---

## 🏦 Organ Bank / Blood Bank Module

The Organ Bank / Blood Bank page supports organ and blood-related coordination within the system.

Main activities include:

- Organ donation coordination
- Organ availability information
- Blood-related information
- Coordination with hospitals
- Coordination with donors
- Support for organ transportation
- Support for emergency requirements

---

## 🚚 Delivery Person Module

The Delivery Person page supports transportation and delivery operations.

The Delivery Person can:

- View assigned transportation requests
- View pickup information
- View destination information
- Coordinate with hospitals and organ banks
- Handle emergency transportation
- Update transportation status
- Confirm delivery completion
- Support time-sensitive organ transportation

The Delivery Person implementation and related database information are demonstrated in the development screenshots.

---

## 👨‍💼 Admin Module

The Admin page provides overall system management and monitoring.

Admin functionality includes:

- User management
- Role management
- Hospital management
- Monitoring system activities
- Resource monitoring
- Emergency activity monitoring
- Overall platform management

---

# 🔄 How OrganShare Works

```text
                         🫀 OrganShare
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
       🧑 Donor           🏥 Hospital       🏦 Organ/Blood Bank
          │                   │                   │
          │                   │                   │
          │             Emergency Resources      │
          │                   │                   │
          │          ┌────────┼────────┐          │
          │          │        │        │          │
          │        Beds     Blood   Transport     │
          │          │        │        │          │
          │          ├── Volunteers ───┤          │
          │          └── Supplies ─────┘          │
          │                   │                   │
          └─────────────┬─────┴───────────────────┘
                        │
                        ▼
                Emergency Coordination
                        │
                        ▼
                 🚚 Delivery Person
                        │
                        ▼
                Organ / Resource Delivery
                        │
                        ▼
                 Destination Hospital
                        │
                        ▼
                    👨‍💼 Admin
              System Monitoring & Management
````

---

# 🚑 Emergency Transportation Workflow

```text
🧑 Donor
   │
   ▼
View Available Hospitals
   │
   ▼
Check Hospital Availability
   │
   ▼
Select Hospital
   │
   ▼
Create Transport Request
   │
   ▼
Transportation Coordination
   │
   ▼
🚚 Delivery Person
   │
   ▼
Pickup Organ / Medical Resource
   │
   ▼
Transport to Destination
   │
   ▼
🏥 Destination Hospital
   │
   ▼
Update Delivery Status
```

---

# 🛏️ Emergency Resource Management

OrganShare provides hospitals with centralized emergency resource management.

### Hospital Beds

Hospitals can manage:

* Total beds
* Available beds
* Occupied beds
* ICU beds
* Emergency beds
* Bed availability status
* Last updated information

### Blood Resources

The system supports management of available blood resources for emergency requirements.

### Transport Resources

Hospitals can manage transportation resources required for emergency medical operations.

### Volunteers

Hospitals can manage available volunteers and their assignments.

### Medical Supplies

Hospitals can manage emergency medical supplies and their availability.

### Emergency Requests

Authorized users can create and manage emergency resource requests.

### Resource Allocation

Emergency resources can be allocated and tracked according to requirements.

---

# 🛠️ Technology Stack

## Android

* Java
* Android SDK
* XML
* Android Studio
* Gradle

## Backend & Database

* Firebase Authentication
* Cloud Firestore

## Development & Monitoring

* Git
* GitHub
* Sentry

---

# 🔥 Firebase Integration

OrganShare uses Firebase for authentication and cloud data management.

## Firebase Authentication

Used for:

* User registration
* User login
* Authentication
* Role-based access

## Cloud Firestore

Cloud Firestore is used to store application and emergency-resource data.

### Main Collections

```text
hospitals
donors
users

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

These collections support hospital resources, transportation, emergency requests, volunteers, medical supplies, resource allocation and related coordination.

---

# 🔐 Authentication & Role-Based Access

OrganShare provides role-based access for its five main user types:

```text
DONOR
HOSPITAL
ORGAN_BANK
DELIVERY
ADMIN
```

Each role receives access to the functionality associated with its page and responsibilities.

Cloud Firestore security rules are used to control access to application data.

---

# 📸 Screenshots

The project contains application and development screenshots extracted from the OrganShare application showcase document.

## 📱 Application Screenshots

The `screenshots/app` folder contains the available Android application UI screenshots.

👉 [View all application screenshots](./screenshots/app)

## 💻 Development Screenshots

The `screenshots/developer` folder contains development and implementation screenshots.

These screenshots demonstrate areas such as:

* Android application development
* Firebase Authentication
* Cloud Firestore
* Database collections
* Hospital resource management
* Emergency resource management
* Transportation resources
* Delivery Person implementation
* Database structures
* Project development

👉 [View all development screenshots](./screenshots/developer)

> The Delivery Person role is included in the project, and its implementation/database information is demonstrated in the development screenshots. A separate Delivery Person UI screenshot is not currently included in the application screenshots.

---

# 📦 Download APK

Download the latest Android APK from GitHub Releases:

👉 **[Download the latest OrganShare APK](../../releases/latest)**

---

# 🚀 Installation

## For Users

1. Open the latest GitHub Release.
2. Download the APK.
3. Transfer it to an Android device if necessary.
4. Allow installation from unknown sources when Android asks for permission.
5. Install and open OrganShare.

## For Developers

Clone the repository:

```bash
git clone https://github.com/Prajakta2745/OrganShareApp.git
```

Open the project in Android Studio, configure the required Firebase project, sync Gradle, and run the application on an Android device or emulator.

---

# 🎯 Project Objectives

OrganShare aims to:

* Simplify donor-hospital coordination
* Support organ donation coordination
* Improve visibility of hospital emergency resources
* Centralize emergency resource information
* Support organ bank and blood coordination
* Support delivery-person transportation operations
* Improve emergency resource management
* Coordinate hospital beds, blood, volunteers and medical supplies
* Support emergency transportation
* Help users make faster decisions during emergency situations

---

# 🔮 Future Enhancements

* 📍 GPS-based hospital discovery
* 🔔 Push notifications
* 🚑 Live emergency transport tracking
* 🏥 Hospital verification workflow
* 📊 Advanced analytics dashboard
* 🌐 Multi-language support
* 🤖 Intelligent hospital/resource matching
* 📱 Dedicated delivery tracking
* 🔔 Real-time delivery status notifications

---

# 👩‍💻 Developer

### Prajakta Patil

Computer Science Engineering (AI & ML)

Bharati Vidyapeeth's College of Engineering, Kolhapur

* GitHub: [Prajakta2745](https://github.com/Prajakta2745)
* Project Repository: [OrganShareApp](https://github.com/Prajakta2745/OrganShareApp)

---

# ⭐ Project Status

**Current Release:** `v1.1.0`

**Platform:** Android

**Project Type:** Academic / Final Year Project

---

⭐ If you find this project useful, consider starring the repository.

```
```
