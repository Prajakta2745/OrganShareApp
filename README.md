# OrganShare - Android Organ Donation & Allocation Management System

**OrganShare** is an enterprise-grade, secure, and modern Android application written in **Java + XML Layouts** for organ donation registration, hospital organ requisition, organ bank inventory vaults, double-allocation safe matching, courier delivery tracking, and administrative compliance auditing.

---

## 🏛️ Architecture Overview

The system strictly follows clean N-Tier architecture:
- **Presentation Layer**: Material 3 XML Layouts, Activities, Fragments, and RecyclerView Adapters.
- **Domain Layer**: 12 Java Model POJOs with Firestore auto-mapping (`UserModel`, `DonorProfile`, `HospitalProfile`, `OrganBankProfile`, `DeliveryPersonnelProfile`, `OrganInventory`, `OrganRequest`, `Allocation`, `OrderModel`, `DeliveryModel`, `Checkpoint`, `AppNotification`, `AuditLog`).
- **Data & Repository Layer**: Dedicated Repositories communicating with Firebase Auth, Cloud Firestore, Firebase Storage, and Cloud Messaging.
- **Concurrency & Integrity**: `FirestoreTransactions` guarantees race-condition safe allocation using atomic transactions.

---

## 👥 Supported Roles & Workflows

1. **Donor**:
   - Register pledge with blood group, contact, city, and organs willing to donate.
   - Pledged status badges and privacy-preserving data protection.
   - Access to educational transplant and cold ischemia guidelines.

2. **Hospital**:
   - Create organ requisitions with priority (`EMERGENCY`, `URGENT`, `NORMAL`) generating standardized `REQ-2026-XXXXX` IDs.
   - Search registered donors with masked privacy protection (e.g. `A*** W***` with contact brokered via coordinator).
   - Track active deliveries with real-time Google Maps route and checkpoint monitoring.

3. **Organ Bank / Coordinator**:
   - Maintain organ inventory vaults with cold ischemia time counters and preservation solutions (`INV-XXXXX`).
   - Software-assisted compatibility check aid with mandatory ethical disclaimer.
   - Atomic allocation workflow creating confirmed orders (`ORD-2026-XXXXX`) and assigning rapid transit medical couriers (`DEL-2026-XXXXX`).

4. **Delivery Personnel**:
   - Dedicated courier transit dashboard.
   - Update checkpoint milestones (`PICKUP_STARTED` → `PICKED_UP` → `IN_TRANSIT` → `CHECKPOINTS` → `NEAR_DESTINATION` → `DELIVERED`).
   - One-touch delay reporting broadcasting alerts to hospitals and coordinators.

5. **Administrator**:
   - Overview metrics (Total Donors, Verified Hospitals, Pending Requests, Active Inventory).
   - Institution and user verification (Approve / Suspend).
   - Immutable audit trail of all transactions and lifecycle events.

---

## 🚀 Step-by-Step Setup Guide

### 1. Firebase Setup
1. Open the [Firebase Console](https://console.firebase.google.com/).
2. Create a new project named `OrganShare`.
3. Add an Android App with package name `com.example.organshare`.
4. Download `google-services.json` and place it inside the `app/` folder:
   ```
   OrganShareApp/app/google-services.json
   ```
5. Enable **Email/Password Authentication** in `Authentication -> Sign-in method`.
6. Enable **Cloud Firestore** in production or test mode.
7. Paste the contents of `firestore.rules` into the Firebase Console Rules tab and publish.

### 2. Google Maps Setup
1. Obtain an API Key from the [Google Cloud Console](https://console.cloud.google.com/) with **Maps SDK for Android** enabled.
2. In `app/src/main/res/values/strings.xml`, replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your API key:
   ```xml
   <string name="google_maps_key">AIzaSyYourActualMapsKey</string>
   ```

### 3. Open and Run in Android Studio
1. Open Android Studio -> **Open an Existing Project** -> Select `C:\Users\Dell\.gemini\antigravity\brain\32fe9b2a-e968-418c-9fcd-7e27d34d6d9e\OrganShareApp`.
2. Let Gradle sync.
3. Run on an Android Emulator or physical device (Android 7.0+ / API 24+).
4. On the Login screen, tap **"⚡ Populate Realistic Academic Demo Data"** to instantly seed sample donors, hospitals, inventory, requests, and deliveries for quick testing!

---

## ⚖️ Medical and Ethical Safeguard Notice
This application serves strictly as an administrative and logistical coordination tool. All matching summaries provided are software-level aids. Final medical eligibility, HLA tissue cross-matching, and allocation decisions must be independently confirmed and authorized by certified medical professionals and statutory organ donation authorities.
