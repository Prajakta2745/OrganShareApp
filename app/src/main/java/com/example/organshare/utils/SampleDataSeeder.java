package com.example.organshare.utils;

import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.Checkpoint;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.models.DonorProfile;
import com.example.organshare.models.HospitalProfile;
import com.example.organshare.models.OrderModel;
import com.example.organshare.models.OrganBankProfile;
import com.example.organshare.models.OrganInventory;
import com.example.organshare.models.OrganRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SampleDataSeeder {

    public interface SeedCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public static void seedDemoData(SeedCallback callback) {
        FirebaseFirestore db = FirestoreHelper.getFirestore();
        WriteBatch batch = db.batch();

        // 1. Seed Organ Bank
        OrganBankProfile bank = new OrganBankProfile();
        bank.setBankId("BANK-001");
        bank.setUserId("bank_demo_user");
        bank.setBankName("National Organ Preservation & Distribution Bank");
        bank.setLicenseNumber("NOTTO-LIC-2024-88");
        bank.setAddress("742 Healthcare Boulevard, Metro City");
        bank.setCity("Metro City");
        bank.setState("Central");
        bank.setLatitude(40.7128);
        bank.setLongitude(-74.0060);
        bank.setContactPhone("+1-555-400-0199");
        bank.setEmergencyHotline("+1-555-400-9999");
        bank.setEmail("coordinator@organbank.org");
        bank.setVerified(true);
        batch.set(db.collection(FirestoreCollections.ORGAN_BANKS).document(bank.getBankId()), bank);

        // 2. Seed Hospital
        HospitalProfile hospital = new HospitalProfile();
        hospital.setHospitalId("HOSP-001");
        hospital.setUserId("hospital_demo_user");
        hospital.setHospitalName("St. Jude Memorial Transplant Center");
        hospital.setLicenseNumber("MED-TX-99021");
        hospital.setAddress("100 Hospital Avenue, North Wing");
        hospital.setCity("Metro City");
        hospital.setState("Central");
        hospital.setLatitude(40.7580);
        hospital.setLongitude(-73.9855);
        hospital.setContactNumber("+1-555-300-8800");
        hospital.setTransplantCoordinatorPhone("+1-555-300-8811");
        hospital.setEmail("transplant@stjude-hospital.org");
        hospital.setVerified(true);
        batch.set(db.collection(FirestoreCollections.HOSPITALS).document(hospital.getHospitalId()), hospital);

        // 3. Seed Donors
        DonorProfile donor1 = new DonorProfile();
        donor1.setDonorId("DNR-1001");
        donor1.setUserId("donor1_user");
        donor1.setFirstName("Alexander");
        donor1.setLastName("Wright");
        donor1.setGender("Male");
        donor1.setDob("1992-04-15");
        donor1.setBloodGroup("O+");
        donor1.setPhone("+1-555-010-4422");
        donor1.setEmail("alex.wright@example.com");
        donor1.setCity("Metro City");
        donor1.setState("Central");
        donor1.setGeneralLocation("Downtown District");
        donor1.setOrgansWillingToDonate(Arrays.asList("Kidney", "Cornea", "Liver"));
        donor1.setDonationStatus("PLEDGED");
        donor1.setConsentGiven(true);
        donor1.setVerified(true);
        batch.set(db.collection(FirestoreCollections.DONORS).document(donor1.getDonorId()), donor1);

        DonorProfile donor2 = new DonorProfile();
        donor2.setDonorId("DNR-1002");
        donor2.setUserId("donor2_user");
        donor2.setFirstName("Elena");
        donor2.setLastName("Rostova");
        donor2.setGender("Female");
        donor2.setDob("1996-08-22");
        donor2.setBloodGroup("A-");
        donor2.setPhone("+1-555-010-8877");
        donor2.setEmail("elena.r@example.com");
        donor2.setCity("Metro City");
        donor2.setState("Central");
        donor2.setGeneralLocation("Uptown Medical Suburb");
        donor2.setOrgansWillingToDonate(Arrays.asList("Heart", "Lungs", "Kidney", "Pancreas"));
        donor2.setDonationStatus("PLEDGED");
        donor2.setConsentGiven(true);
        donor2.setVerified(true);
        batch.set(db.collection(FirestoreCollections.DONORS).document(donor2.getDonorId()), donor2);

        // 4. Seed Organ Inventory
        OrganInventory inv1 = new OrganInventory();
        inv1.setInventoryId("INV-5001");
        inv1.setOrganType("Kidney");
        inv1.setBloodGroup("O+");
        inv1.setAvailabilityStatus(Constants.INV_AVAILABLE);
        inv1.setCollectionDate(DateTimeUtils.getCurrentDate());
        inv1.setExpiryDate(DateTimeUtils.getCurrentDate());
        inv1.setPreservationLimitHours(24);
        inv1.setDonorRefId("DNR-1001");
        inv1.setDonorMaskedName("A*** W***");
        inv1.setOrganBankId("BANK-001");
        inv1.setOrganBankName("National Organ Bank");
        inv1.setStorageLocation("Cryo Vault Bay 3");
        inv1.setStorageTemperature("4°C Hypothermic Perfusion");
        inv1.setVerified(true);
        batch.set(db.collection(FirestoreCollections.INVENTORY).document(inv1.getInventoryId()), inv1);

        OrganInventory inv2 = new OrganInventory();
        inv2.setInventoryId("INV-5002");
        inv2.setOrganType("Liver");
        inv2.setBloodGroup("A-");
        inv2.setAvailabilityStatus(Constants.INV_AVAILABLE);
        inv2.setCollectionDate(DateTimeUtils.getCurrentDate());
        inv2.setExpiryDate(DateTimeUtils.getCurrentDate());
        inv2.setPreservationLimitHours(12);
        inv2.setDonorRefId("DNR-1002");
        inv2.setDonorMaskedName("E*** R***");
        inv2.setOrganBankId("BANK-001");
        inv2.setOrganBankName("National Organ Bank");
        inv2.setStorageLocation("Preservation Suite A");
        inv2.setStorageTemperature("4°C UW Solution");
        inv2.setVerified(true);
        batch.set(db.collection(FirestoreCollections.INVENTORY).document(inv2.getInventoryId()), inv2);

        // 5. Seed Organ Request
        OrganRequest req1 = new OrganRequest();
        req1.setRequestId("REQ-2026-10101");
        req1.setHospitalId("HOSP-001");
        req1.setHospitalName("St. Jude Memorial Transplant Center");
        req1.setHospitalAddress("100 Hospital Avenue, North Wing");
        req1.setHospitalContact("+1-555-300-8800");
        req1.setOrganRequired("Kidney");
        req1.setBloodGroup("O+");
        req1.setQuantity(1);
        req1.setEmergencyLevel(Constants.EMERGENCY_URGENT);
        req1.setPatientRefId("PAT-TX-8821");
        req1.setRequiredDate(DateTimeUtils.getCurrentDate());
        req1.setRequiredTime("18:00");
        req1.setAdditionalNotes("ESRD stage 5 patient scheduled for priority grafting.");
        req1.setStatus(Constants.STATUS_PENDING);
        req1.setCompatibilitySummary("Software Compatible (ABO Matched)");
        batch.set(db.collection(FirestoreCollections.ORGAN_REQUESTS).document(req1.getRequestId()), req1);

        // 6. Seed In-Transit Delivery
        DeliveryModel delivery = new DeliveryModel();
        delivery.setDeliveryId("DEL-2026-9001");
        delivery.setOrderId("ORD-2026-5501");
        delivery.setRequestId("REQ-2026-10100");
        delivery.setOrganType("Kidney");
        delivery.setBloodGroup("O+");
        delivery.setDeliveryPersonId("DEL-USR-01");
        delivery.setDeliveryPersonName("Marcus Vance");
        delivery.setDeliveryPersonPhone("+1-555-700-1122");
        delivery.setPickupAddress("742 Healthcare Boulevard, Metro City");
        delivery.setPickupLatitude(40.7128);
        delivery.setPickupLongitude(-74.0060);
        delivery.setDestinationHospitalName("St. Jude Memorial Transplant Center");
        delivery.setDestinationAddress("100 Hospital Avenue, North Wing");
        delivery.setDestinationLatitude(40.7580);
        delivery.setDestinationLongitude(-73.9855);
        delivery.setCoordinatorContact("+1-555-400-9999");
        delivery.setCurrentStatus(Constants.DEL_IN_TRANSIT);
        delivery.setLastKnownLatitude(40.7300);
        delivery.setLastKnownLongitude(-73.9950);
        delivery.setLastUpdatedTime(DateTimeUtils.getCurrentDateTime());
        delivery.setEstimatedArrival("25 mins");

        List<Checkpoint> checkpoints = new ArrayList<>();
        checkpoints.add(new Checkpoint("CP-01", delivery.getDeliveryId(), "Organ Bank Dispatch Bay", 40.7128, -74.0060, DateTimeUtils.getCurrentTime(), "PICKED_UP", "Cold storage temp verified at 3.8°C"));
        checkpoints.add(new Checkpoint("CP-02", delivery.getDeliveryId(), "Express Corridor Mile 4", 40.7300, -73.9950, DateTimeUtils.getCurrentTime(), "IN_TRANSIT", "Green corridor clearance active"));
        delivery.setCheckpoints(checkpoints);

        batch.set(db.collection(FirestoreCollections.DELIVERIES).document(delivery.getDeliveryId()), delivery);

        batch.commit()
                .addOnSuccessListener(aVoid -> callback.onSuccess("Sample demo data seeded successfully!"))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
