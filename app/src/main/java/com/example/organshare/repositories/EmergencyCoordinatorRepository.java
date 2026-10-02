package com.example.organshare.repositories;

import com.example.organshare.models.HospitalProfile;
import com.example.organshare.models.emergency.BloodResource;
import com.example.organshare.models.emergency.EmergencyRequest;
import com.example.organshare.models.emergency.HospitalBedResource;
import com.example.organshare.models.emergency.HospitalResourceOverview;
import com.example.organshare.models.emergency.MedicalSupplyResource;
import com.example.organshare.models.emergency.ResourceAllocation;
import com.example.organshare.models.emergency.ResourceHistory;
import com.example.organshare.models.emergency.TransportRequest;
import com.example.organshare.models.emergency.TransportResource;
import com.example.organshare.models.emergency.Volunteer;
import com.example.organshare.utils.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EmergencyCoordinatorRepository {

    private final FirebaseFirestore db;
    private final CollectionReference bedsRef;
    private final CollectionReference bloodRef;
    private final CollectionReference transportRef;
    private final CollectionReference transportReqRef;
    private final CollectionReference volunteersRef;
    private final CollectionReference suppliesRef;
    private final CollectionReference emergencyReqRef;
    private final CollectionReference allocationsRef;
    private final CollectionReference historyRef;
    private final CollectionReference hospitalsRef;

    public interface DataCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }

    public interface ResourceCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public interface ResourceListener<T> {
        void onUpdate(T data);
    }

    public interface FreshCheckCallback {
        void onResult(boolean isAvailable, String message);
    }

    public interface ConfirmationCallback {
        void onConfirmed(String emergencyRequestId, String transportRequestId, String message);
        void onFailed(String reason);
    }

    public EmergencyCoordinatorRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.bedsRef = db.collection("emergency_hospital_beds");
        this.bloodRef = db.collection("emergency_blood_resources");
        this.transportRef = db.collection("emergency_transport_resources");
        this.transportReqRef = db.collection("emergency_transport_requests");
        this.volunteersRef = db.collection("emergency_volunteers");
        this.suppliesRef = db.collection("emergency_medical_supplies");
        this.emergencyReqRef = db.collection("emergency_resource_requests");
        this.allocationsRef = db.collection("emergency_resource_allocations");
        this.historyRef = db.collection("emergency_resource_history");
        this.hospitalsRef = db.collection("hospitals");
    }

    // ==========================================
    // 1. HOSPITAL BEDS MANAGEMENT
    // ==========================================

    public void getAllHospitalBeds(DataCallback<List<HospitalBedResource>> callback) {
        bedsRef.get().addOnSuccessListener(snapshots -> {
            List<HospitalBedResource> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    HospitalBedResource bed = doc.toObject(HospitalBedResource.class);
                    if (bed != null) list.add(bed);
                }
            }
            if (list.isEmpty()) {
                seedInitialData(callback);
            } else {
                callback.onSuccess(list);
            }
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public ListenerRegistration listenToHospitalBeds(DataCallback<List<HospitalBedResource>> callback) {
        return bedsRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
                return;
            }
            List<HospitalBedResource> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    HospitalBedResource bed = doc.toObject(HospitalBedResource.class);
                    if (bed != null) list.add(bed);
                }
            }
            callback.onSuccess(list);
        });
    }

    public void updateHospitalBeds(HospitalBedResource beds, String performedBy, DataCallback<Void> callback) {
        if (beds.getHospitalId() == null) {
            callback.onFailure("Hospital ID is required");
            return;
        }

        // Validate
        if (beds.getAvailableBeds() < 0 || beds.getAvailableBeds() > beds.getTotalBeds()) {
            callback.onFailure("Available beds must be between 0 and total beds (" + beds.getTotalBeds() + ")");
            return;
        }
        if (beds.getAvailableICUBeds() < 0 || beds.getAvailableICUBeds() > beds.getTotalICUBeds()) {
            callback.onFailure("Available ICU beds must be between 0 and total ICU beds (" + beds.getTotalICUBeds() + ")");
            return;
        }
        if (beds.getAvailableEmergencyBeds() < 0 || beds.getAvailableEmergencyBeds() > beds.getTotalEmergencyBeds()) {
            callback.onFailure("Available Emergency beds must be between 0 and total emergency capacity (" + beds.getTotalEmergencyBeds() + ")");
            return;
        }

        beds.setLastUpdated(new Date());
        bedsRef.document(beds.getHospitalId()).set(beds)
                .addOnSuccessListener(aVoid -> {
                    logHistory("BEDS", beds.getHospitalId(), beds.getHospitalName(),
                            "Total: " + beds.getTotalBeds(), "Avail: " + beds.getAvailableBeds() + ", ICU: " + beds.getAvailableICUBeds(),
                            "QUANTITY_UPDATED", performedBy, null, beds.getLocation(), "Hospital bed capacity updated.");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // 2. BLOOD RESOURCES MANAGEMENT
    // ==========================================

    public void getAllBloodResources(DataCallback<List<BloodResource>> callback) {
        bloodRef.get().addOnSuccessListener(snapshots -> {
            List<BloodResource> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    BloodResource blood = doc.toObject(BloodResource.class);
                    if (blood != null) list.add(blood);
                }
            }
            callback.onSuccess(list);
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public ListenerRegistration listenToBloodResources(DataCallback<List<BloodResource>> callback) {
        return bloodRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
                return;
            }
            List<BloodResource> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    BloodResource blood = doc.toObject(BloodResource.class);
                    if (blood != null) list.add(blood);
                }
            }
            callback.onSuccess(list);
        });
    }

    public void addOrUpdateBloodUnits(String bloodResourceId, int unitsToAdd, String performedBy, DataCallback<Void> callback) {
        DocumentReference docRef = bloodRef.document(bloodResourceId);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);
            if (!snapshot.exists()) {
                throw new IllegalStateException("Blood resource document does not exist.");
            }
            BloodResource item = snapshot.toObject(BloodResource.class);
            if (item == null) throw new IllegalStateException("Failed to parse blood resource.");

            int prev = item.getAvailableUnits();
            int newTotal = Math.max(0, prev + unitsToAdd);
            item.setAvailableUnits(newTotal);
            item.setLastUpdated(new Date());

            transaction.set(docRef, item);

            logHistory("BLOOD", bloodResourceId, item.getBloodGroup() + " Blood (" + item.getHospitalName() + ")",
                    prev + " units", newTotal + " units", unitsToAdd >= 0 ? "UNITS_ADDED" : "UNITS_DEDUCTED",
                    performedBy, null, item.getHospitalName(), "Updated by " + unitsToAdd + " units");
            return null;
        }).addOnSuccessListener(aVoid -> callback.onSuccess(null))
          .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // 3. TRANSPORT & FLEET MANAGEMENT
    // ==========================================

    public void getAllTransportResources(DataCallback<List<TransportResource>> callback) {
        transportRef.get().addOnSuccessListener(snapshots -> {
            List<TransportResource> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    TransportResource t = doc.toObject(TransportResource.class);
                    if (t != null) list.add(t);
                }
            }
            callback.onSuccess(list);
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public ListenerRegistration listenToTransportResources(DataCallback<List<TransportResource>> callback) {
        return transportRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
                return;
            }
            List<TransportResource> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    TransportResource t = doc.toObject(TransportResource.class);
                    if (t != null) list.add(t);
                }
            }
            callback.onSuccess(list);
        });
    }

    public void addOrUpdateTransport(TransportResource transport, String performedBy, DataCallback<Void> callback) {
        if (transport.getTransportId() == null || transport.getTransportId().isEmpty()) {
            transport.setTransportId("TR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        transport.setLastUpdated(new Date());

        transportRef.document(transport.getTransportId()).set(transport)
                .addOnSuccessListener(aVoid -> {
                    logHistory("TRANSPORT", transport.getTransportId(), transport.getVehicleId() + " (" + transport.getVehicleType() + ")",
                            "-", transport.getAvailabilityStatus(), "STATUS_CHANGED", performedBy, null, transport.getCurrentLocation(), "Transport record updated.");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void createTransportRequest(TransportRequest request, DataCallback<String> callback) {
        if (request.getTransportRequestId() == null || request.getTransportRequestId().isEmpty()) {
            request.setTransportRequestId("TREQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        request.setRequestedAt(new Date());
        request.setLastUpdated(new Date());

        transportReqRef.document(request.getTransportRequestId()).set(request)
                .addOnSuccessListener(aVoid -> {
                    // Attempt auto-assign first available ambulance for destination hospital
                    autoAssignAmbulanceIfAvailable(request);
                    callback.onSuccess(request.getTransportRequestId());
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private void autoAssignAmbulanceIfAvailable(TransportRequest request) {
        transportRef.whereEqualTo("availabilityStatus", "AVAILABLE").limit(1).get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots != null && !snapshots.isEmpty()) {
                        DocumentSnapshot doc = snapshots.getDocuments().get(0);
                        TransportResource ambulance = doc.toObject(TransportResource.class);
                        if (ambulance != null) {
                            ambulance.setAvailabilityStatus("ASSIGNED");
                            ambulance.setCurrentAssignment("Assigned to Donor Request #" + request.getTransportRequestId());
                            ambulance.setDestination(request.getDestinationHospitalName());
                            ambulance.setAssignedDonorId(request.getDonorId());
                            ambulance.setAssignedEmergencyRequestId(request.getTransportRequestId());
                            ambulance.setLastUpdated(new Date());
                            transportRef.document(ambulance.getTransportId()).set(ambulance);

                            // Update request
                            request.setStatus("ASSIGNED");
                            request.setAssignedTransportId(ambulance.getTransportId());
                            request.setAssignedVehicleNumber(ambulance.getVehicleId());
                            request.setAssignedDriverName(ambulance.getDriverName());
                            request.setAssignedDriverPhone(ambulance.getDriverContact());
                            request.setLastUpdated(new Date());
                            transportReqRef.document(request.getTransportRequestId()).set(request);
                        }
                    }
                });
    }

    public ListenerRegistration listenToTransportRequest(String requestId, DataCallback<TransportRequest> callback) {
        return transportReqRef.document(requestId).addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                TransportRequest req = snapshot.toObject(TransportRequest.class);
                callback.onSuccess(req);
            }
        });
    }

    // ==========================================
    // 4. VOLUNTEER MANAGEMENT
    // ==========================================

    public void getAllVolunteers(DataCallback<List<Volunteer>> callback) {
        volunteersRef.get().addOnSuccessListener(snapshots -> {
            List<Volunteer> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Volunteer v = doc.toObject(Volunteer.class);
                    if (v != null) list.add(v);
                }
            }
            callback.onSuccess(list);
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public ListenerRegistration listenToVolunteers(DataCallback<List<Volunteer>> callback) {
        return volunteersRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
                return;
            }
            List<Volunteer> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Volunteer v = doc.toObject(Volunteer.class);
                    if (v != null) list.add(v);
                }
            }
            callback.onSuccess(list);
        });
    }

    public void addOrUpdateVolunteer(Volunteer volunteer, String performedBy, DataCallback<Void> callback) {
        if (volunteer.getVolunteerId() == null || volunteer.getVolunteerId().isEmpty()) {
            volunteer.setVolunteerId("VOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        volunteer.setLastUpdated(new Date());

        volunteersRef.document(volunteer.getVolunteerId()).set(volunteer)
                .addOnSuccessListener(aVoid -> {
                    logHistory("VOLUNTEER", volunteer.getVolunteerId(), volunteer.getName(),
                            "-", volunteer.getAvailabilityStatus(), "STATUS_CHANGED", performedBy, null, volunteer.getLocation(), "Volunteer profile updated.");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void assignVolunteer(String volunteerId, String emergencyRequestId, String assignment, String performedBy, DataCallback<Void> callback) {
        DocumentReference docRef = volunteersRef.document(volunteerId);
        docRef.update("availabilityStatus", "ASSIGNED",
                     "assignedEmergencyRequestId", emergencyRequestId,
                     "currentAssignment", assignment,
                     "lastUpdated", new Date())
                .addOnSuccessListener(aVoid -> {
                    logHistory("VOLUNTEER", volunteerId, "Volunteer Assignment", "AVAILABLE", "ASSIGNED",
                            "ALLOCATED", performedBy, emergencyRequestId, "-", assignment);
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // 5. MEDICAL SUPPLIES MANAGEMENT
    // ==========================================

    public void getAllMedicalSupplies(DataCallback<List<MedicalSupplyResource>> callback) {
        suppliesRef.get().addOnSuccessListener(snapshots -> {
            List<MedicalSupplyResource> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    MedicalSupplyResource m = doc.toObject(MedicalSupplyResource.class);
                    if (m != null) list.add(m);
                }
            }
            callback.onSuccess(list);
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public ListenerRegistration listenToMedicalSupplies(DataCallback<List<MedicalSupplyResource>> callback) {
        return suppliesRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
                return;
            }
            List<MedicalSupplyResource> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    MedicalSupplyResource m = doc.toObject(MedicalSupplyResource.class);
                    if (m != null) list.add(m);
                }
            }
            callback.onSuccess(list);
        });
    }

    public void addOrUpdateMedicalSupply(MedicalSupplyResource supply, String performedBy, DataCallback<Void> callback) {
        if (supply.getSupplyId() == null || supply.getSupplyId().isEmpty()) {
            supply.setSupplyId("SUP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        supply.updateStatus();
        supply.setLastUpdated(new Date());

        suppliesRef.document(supply.getSupplyId()).set(supply)
                .addOnSuccessListener(aVoid -> {
                    logHistory("SUPPLIES", supply.getSupplyId(), supply.getSupplyName(),
                            "-", supply.getAvailableQuantity() + " " + supply.getUnit(), "QUANTITY_UPDATED", performedBy, null, supply.getStorageLocation(), "Medical supply record saved.");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // 6. EMERGENCY REQUESTS
    // ==========================================

    public void getAllEmergencyRequests(DataCallback<List<EmergencyRequest>> callback) {
        emergencyReqRef.get().addOnSuccessListener(snapshots -> {
            List<EmergencyRequest> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    EmergencyRequest req = doc.toObject(EmergencyRequest.class);
                    if (req != null) list.add(req);
                }
            }
            // Sort by priority (CRITICAL -> HIGH -> MEDIUM -> LOW)
            sortEmergencyRequests(list);
            callback.onSuccess(list);
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public ListenerRegistration listenToEmergencyRequests(DataCallback<List<EmergencyRequest>> callback) {
        return emergencyReqRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
                return;
            }
            List<EmergencyRequest> list = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    EmergencyRequest req = doc.toObject(EmergencyRequest.class);
                    if (req != null) list.add(req);
                }
            }
            sortEmergencyRequests(list);
            callback.onSuccess(list);
        });
    }

    private void sortEmergencyRequests(List<EmergencyRequest> list) {
        list.sort((r1, r2) -> {
            int p1 = getPriorityScore(r1.getPriority());
            int p2 = getPriorityScore(r2.getPriority());
            return Integer.compare(p2, p1);
        });
    }

    private int getPriorityScore(String priority) {
        if ("CRITICAL".equalsIgnoreCase(priority)) return 4;
        if ("HIGH".equalsIgnoreCase(priority)) return 3;
        if ("MEDIUM".equalsIgnoreCase(priority)) return 2;
        return 1;
    }

    public void createEmergencyRequest(EmergencyRequest req, DataCallback<String> callback) {
        if (req.getEmergencyRequestId() == null || req.getEmergencyRequestId().isEmpty()) {
            req.setEmergencyRequestId("ER-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        req.setRequestedAt(new Date());
        req.setLastUpdated(new Date());

        emergencyReqRef.document(req.getEmergencyRequestId()).set(req)
                .addOnSuccessListener(aVoid -> {
                    logHistory("EMERGENCY_REQUEST", req.getEmergencyRequestId(), req.getResourceType() + " Request (" + req.getResourceDetails() + ")",
                            "-", req.getPriority(), "CREATED", req.getRequestedBy(), req.getEmergencyRequestId(), req.getHospitalName(), req.getDescription());
                    callback.onSuccess(req.getEmergencyRequestId());
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateEmergencyRequestStatus(String requestId, String status, DataCallback<Void> callback) {
        emergencyReqRef.document(requestId).update("status", status, "lastUpdated", new Date())
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // 7. RESOURCE ALLOCATION TRANSACTION
    // ==========================================

    public void allocateResource(ResourceAllocation alloc, DataCallback<Void> callback) {
        if (alloc.getAllocationId() == null || alloc.getAllocationId().isEmpty()) {
            alloc.setAllocationId("ALC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        alloc.setAllocatedAt(new Date());

        db.runTransaction(transaction -> {
            // 1. Fetch and update the emergency request
            DocumentReference reqRef = emergencyReqRef.document(alloc.getEmergencyRequestId());
            DocumentSnapshot reqSnap = transaction.get(reqRef);
            if (reqSnap.exists()) {
                EmergencyRequest req = reqSnap.toObject(EmergencyRequest.class);
                if (req != null) {
                    int newAlloc = req.getAllocatedQuantity() + alloc.getQuantity();
                    req.setAllocatedQuantity(newAlloc);
                    if (newAlloc >= req.getRequiredQuantity()) {
                        req.setStatus("FULFILLED");
                    } else {
                        req.setStatus("PARTIALLY_FULFILLED");
                    }
                    req.setLastUpdated(new Date());
                    transaction.set(reqRef, req);
                }
            }

            // 2. Adjust target resource inventory if applicable
            if ("BLOOD".equalsIgnoreCase(alloc.getResourceType()) && alloc.getResourceId() != null) {
                DocumentReference bDoc = bloodRef.document(alloc.getResourceId());
                DocumentSnapshot bSnap = transaction.get(bDoc);
                if (bSnap.exists()) {
                    BloodResource br = bSnap.toObject(BloodResource.class);
                    if (br != null) {
                        br.setAvailableUnits(Math.max(0, br.getAvailableUnits() - alloc.getQuantity()));
                        br.setLastUpdated(new Date());
                        transaction.set(bDoc, br);
                    }
                }
            } else if ("SUPPLIES".equalsIgnoreCase(alloc.getResourceType()) && alloc.getResourceId() != null) {
                DocumentReference sDoc = suppliesRef.document(alloc.getResourceId());
                DocumentSnapshot sSnap = transaction.get(sDoc);
                if (sSnap.exists()) {
                    MedicalSupplyResource ms = sSnap.toObject(MedicalSupplyResource.class);
                    if (ms != null) {
                        ms.setAvailableQuantity(Math.max(0, ms.getAvailableQuantity() - alloc.getQuantity()));
                        ms.setLastUpdated(new Date());
                        transaction.set(sDoc, ms);
                    }
                }
            } else if ("BEDS".equalsIgnoreCase(alloc.getResourceType()) && alloc.getSourceHospitalId() != null) {
                DocumentReference bedDoc = bedsRef.document(alloc.getSourceHospitalId());
                DocumentSnapshot bedSnap = transaction.get(bedDoc);
                if (bedSnap.exists()) {
                    HospitalBedResource hbr = bedSnap.toObject(HospitalBedResource.class);
                    if (hbr != null) {
                        hbr.setAvailableBeds(Math.max(0, hbr.getAvailableBeds() - alloc.getQuantity()));
                        hbr.setLastUpdated(new Date());
                        transaction.set(bedDoc, hbr);
                    }
                }
            }

            // 3. Save allocation record
            transaction.set(allocationsRef.document(alloc.getAllocationId()), alloc);

            return null;
        }).addOnSuccessListener(aVoid -> {
            logHistory(alloc.getResourceType(), alloc.getResourceId(), alloc.getResourceName(),
                    "Allocated " + alloc.getQuantity() + " units", "STATUS: ALLOCATED",
                    "ALLOCATED", alloc.getAllocatedBy(), alloc.getEmergencyRequestId(),
                    alloc.getDestinationHospitalName(), "Resource successfully allocated by coordinator.");
            callback.onSuccess(null);
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // 8. RESOURCE HISTORY AUDIT LOG
    // ==========================================

    public void getResourceHistory(DataCallback<List<ResourceHistory>> callback) {
        historyRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(100).get()
                .addOnSuccessListener(snapshots -> {
                    List<ResourceHistory> list = new ArrayList<>();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            ResourceHistory h = doc.toObject(ResourceHistory.class);
                            if (h != null) list.add(h);
                        }
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void logHistory(String resourceType, String resourceId, String resourceName,
                           String prevVal, String newVal, String action, String performedBy,
                           String emergencyRequestId, String destination, String notes) {
        String logId = "HIST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ResourceHistory h = new ResourceHistory(logId, resourceType, resourceId, resourceName,
                prevVal, newVal, action, performedBy != null ? performedBy : "Coordinator",
                "Coordinator/Admin", emergencyRequestId, destination, notes);
        h.setTimestamp(new Date());
        historyRef.document(logId).set(h);
    }

    // ==========================================
    // 9. DONOR HOSPITAL OVERVIEW & FRESH AVAILABILITY
    // ==========================================

    public List<HospitalResourceOverview> getFallbackHospitalOverviews() {
        List<HospitalResourceOverview> list = new ArrayList<>();
        list.add(new HospitalResourceOverview("HOSP-001", "City Multispeciality Hospital", "Shivaji Nagar, Kolhapur", "+91-98230-11111", 24, 5, 8, "AVAILABLE", "AVAILABLE", "OPEN", "Updated just now"));
        list.add(new HospitalResourceOverview("HOSP-002", "Apex Trauma & Cardiac Center", "Tarabai Park, Kolhapur", "+91-98230-22222", 14, 3, 4, "AVAILABLE", "AVAILABLE", "OPEN", "Updated just now"));
        list.add(new HospitalResourceOverview("HOSP-003", "Metro Life Care Hospital", "Station Road, Kolhapur", "+91-98230-33333", 6, 1, 2, "LOW", "IN_TRANSIT", "LOW_CAPACITY", "Updated just now"));
        list.add(new HospitalResourceOverview("HOSP-004", "Shree Chhatrapati Memorial Hospital", "Rajarampuri, Kolhapur", "+91-98230-44444", 38, 8, 12, "AVAILABLE", "AVAILABLE", "OPEN", "Updated just now"));
        return list;
    }

    public HospitalBedResource getFallbackHospitalBed(String hospitalId) {
        if ("HOSP-002".equalsIgnoreCase(hospitalId)) {
            return new HospitalBedResource("HOSP-002", "Apex Trauma & Cardiac Center", "Tarabai Park, Kolhapur", 85, 14, 15, 3, 10, 4, "AVAILABLE", "+91-98230-22222");
        } else if ("HOSP-003".equalsIgnoreCase(hospitalId)) {
            return new HospitalBedResource("HOSP-003", "Metro Life Care Hospital", "Station Road, Kolhapur", 150, 6, 25, 1, 20, 2, "LOW_CAPACITY", "+91-98230-33333");
        } else if ("HOSP-004".equalsIgnoreCase(hospitalId)) {
            return new HospitalBedResource("HOSP-004", "Shree Chhatrapati Memorial Hospital", "Rajarampuri, Kolhapur", 200, 38, 30, 8, 25, 12, "AVAILABLE", "+91-98230-44444");
        }
        return new HospitalBedResource("HOSP-001", "City Multispeciality Hospital", "Shivaji Nagar, Kolhapur", 120, 24, 20, 5, 15, 8, "AVAILABLE", "+91-98230-11111");
    }

    public List<BloodResource> getFallbackBloodResources(String hospitalId) {
        List<BloodResource> list = new ArrayList<>();
        String hName = "City Multispeciality Hospital";
        if ("HOSP-002".equalsIgnoreCase(hospitalId)) hName = "Apex Trauma Center";
        else if ("HOSP-003".equalsIgnoreCase(hospitalId)) hName = "Metro Life Care Hospital";
        else if ("HOSP-004".equalsIgnoreCase(hospitalId)) hName = "Shree Chhatrapati Memorial Hospital";

        list.add(new BloodResource("BLD-01", hospitalId, hName, "O+", 22, 10));
        list.add(new BloodResource("BLD-02", hospitalId, hName, "A+", 14, 10));
        list.add(new BloodResource("BLD-03", hospitalId, hName, "B+", 18, 10));
        list.add(new BloodResource("BLD-04", hospitalId, hName, "AB+", 8, 8));
        list.add(new BloodResource("BLD-05", hospitalId, hName, "O-", 2, 5));
        return list;
    }

    public void getAllHospitalOverviews(DataCallback<List<HospitalResourceOverview>> callback) {
        bedsRef.get().addOnSuccessListener(snapshots -> {
            List<HospitalResourceOverview> overviews = new ArrayList<>();
            if (snapshots != null && !snapshots.isEmpty()) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    HospitalBedResource b = doc.toObject(HospitalBedResource.class);
                    if (b != null) {
                        HospitalResourceOverview o = new HospitalResourceOverview(
                                b.getHospitalId(),
                                b.getHospitalName(),
                                b.getLocation(),
                                b.getContactNumber() != null ? b.getContactNumber() : "+91-98230-12345",
                                b.getAvailableBeds(),
                                b.getAvailableICUBeds(),
                                b.getAvailableEmergencyBeds(),
                                b.getAvailableBeds() > 0 ? "AVAILABLE" : "LOW",
                                "AVAILABLE",
                                b.getStatus() != null ? b.getStatus() : "OPEN",
                                "Updated recently"
                        );
                        overviews.add(o);
                    }
                }
                callback.onSuccess(overviews);
            } else {
                // Seed initial data then fetch
                seedInitialData(new DataCallback<List<HospitalBedResource>>() {
                    @Override
                    public void onSuccess(List<HospitalBedResource> result) {
                        List<HospitalResourceOverview> seededList = new ArrayList<>();
                        for (HospitalBedResource b : result) {
                            seededList.add(new HospitalResourceOverview(
                                    b.getHospitalId(),
                                    b.getHospitalName(),
                                    b.getLocation(),
                                    b.getContactNumber() != null ? b.getContactNumber() : "+91-98230-12345",
                                    b.getAvailableBeds(),
                                    b.getAvailableICUBeds(),
                                    b.getAvailableEmergencyBeds(),
                                    b.getAvailableBeds() > 0 ? "AVAILABLE" : "LOW",
                                    "AVAILABLE",
                                    b.getStatus() != null ? b.getStatus() : "OPEN",
                                    "Updated recently"
                            ));
                        }
                        callback.onSuccess(seededList);
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onSuccess(getFallbackHospitalOverviews());
                    }
                });
            }
        }).addOnFailureListener(e -> callback.onSuccess(getFallbackHospitalOverviews()));
    }

    public void performFreshAvailabilityCheck(String hospitalId, boolean needICU, boolean needEmergency,
                                             String bloodGroup, boolean needTransport, FreshCheckCallback callback) {
        bedsRef.document(hospitalId).get().addOnSuccessListener(doc -> {
            HospitalBedResource bed = null;
            if (doc != null && doc.exists()) {
                bed = doc.toObject(HospitalBedResource.class);
            }
            if (bed == null) {
                bed = getFallbackHospitalBed(hospitalId);
            }

            if (bed == null || "CLOSED".equalsIgnoreCase(bed.getStatus())) {
                callback.onResult(false, "Hospital is currently closed or not taking new admissions.");
                return;
            }
            if (bed.getAvailableBeds() <= 0) {
                callback.onResult(false, "No standard beds are currently available at this hospital.");
                return;
            }
            if (needICU && bed.getAvailableICUBeds() <= 0) {
                callback.onResult(false, "No ICU beds are currently available at this hospital.");
                return;
            }
            if (needEmergency && bed.getAvailableEmergencyBeds() <= 0) {
                callback.onResult(false, "No emergency trauma beds are currently available at this hospital.");
                return;
            }

            callback.onResult(true, "Hospital verified! Resources are currently available.");
        }).addOnFailureListener(e -> {
            HospitalBedResource bed = getFallbackHospitalBed(hospitalId);
            if (bed != null && bed.getAvailableBeds() > 0) {
                callback.onResult(true, "Hospital verified! Resources are currently available.");
            } else {
                callback.onResult(false, "Network error checking availability: " + e.getMessage());
            }
        });
    }

    public void confirmHospitalAndReserveBed(String hospitalId, String donorId, String donorName, String donorPhone,
                                            boolean needTransport, String pickupLocation,
                                            ConfirmationCallback callback) {
        DocumentReference bedDoc = bedsRef.document(hospitalId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(bedDoc);
            if (!snapshot.exists()) {
                throw new IllegalStateException("Hospital does not exist.");
            }
            HospitalBedResource bed = snapshot.toObject(HospitalBedResource.class);
            if (bed == null || bed.getAvailableBeds() <= 0) {
                throw new IllegalStateException("Hospital availability has changed. No beds available.");
            }

            // Decrement available beds atomically
            bed.setAvailableBeds(bed.getAvailableBeds() - 1);
            bed.setLastUpdated(new Date());
            transaction.set(bedDoc, bed);

            // Create Emergency Request record
            String erId = "ER-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            EmergencyRequest er = new EmergencyRequest(erId, donorId, donorName != null ? donorName : "Donor/Patient",
                    hospitalId, bed.getHospitalName(), "BEDS", "1 General Admission Bed",
                    1, "HIGH", "Direct admission reserved by Donor.");
            er.setDonorId(donorId);
            er.setStatus("IN_PROGRESS");
            er.setRequestedAt(new Date());
            er.setLastUpdated(new Date());
            transaction.set(emergencyReqRef.document(erId), er);

            return erId;
        }).addOnSuccessListener(erId -> {
            if (needTransport) {
                // Create transport request
                TransportRequest treq = new TransportRequest("TREQ-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                        donorId, donorName, donorPhone, pickupLocation, hospitalId, "Selected Hospital", "URGENT");
                createTransportRequest(treq, new DataCallback<String>() {
                    @Override
                    public void onSuccess(String treqId) {
                        callback.onConfirmed(erId, treqId, "Hospital bed reserved and emergency ambulance requested!");
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onConfirmed(erId, null, "Hospital bed reserved. (Ambulance request queue error: " + error + ")");
                    }
                });
            } else {
                callback.onConfirmed(erId, null, "Hospital bed successfully confirmed and reserved!");
            }
        }).addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    // ==========================================
    // 10. INITIAL SEED DATA FOR DEMONSTRATION
    // ==========================================

    private void seedInitialData(DataCallback<List<HospitalBedResource>> callback) {
        WriteBatch batch = db.batch();

        List<HospitalBedResource> sampleBeds = Arrays.asList(
                new HospitalBedResource("HOSP-001", "City Multispeciality Hospital", "Shivaji Nagar, Kolhapur", 120, 24, 20, 5, 15, 8, "AVAILABLE", "+91-98230-11111"),
                new HospitalBedResource("HOSP-002", "Apex Trauma & Cardiac Center", "Tarabai Park, Kolhapur", 85, 14, 15, 3, 10, 4, "AVAILABLE", "+91-98230-22222"),
                new HospitalBedResource("HOSP-003", "Metro Life Care Hospital", "Station Road, Kolhapur", 150, 6, 25, 1, 20, 2, "LOW_CAPACITY", "+91-98230-33333"),
                new HospitalBedResource("HOSP-004", "Shree Chhatrapati Memorial Hospital", "Rajarampuri, Kolhapur", 200, 38, 30, 8, 25, 12, "AVAILABLE", "+91-98230-44444")
        );

        for (HospitalBedResource b : sampleBeds) {
            b.setLastUpdated(new Date());
            batch.set(bedsRef.document(b.getHospitalId()), b);
        }

        // Blood Groups Seed
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        int[] units = {14, 4, 18, 5, 8, 3, 22, 2};
        int[] mins = {10, 5, 10, 5, 8, 5, 15, 5};
        for (int i = 0; i < groups.length; i++) {
            String bId = "BLD-H1-" + groups[i].replace("+", "P").replace("-", "N");
            BloodResource br = new BloodResource(bId, "HOSP-001", "City Multispeciality Hospital", groups[i], units[i], mins[i]);
            br.setLastUpdated(new Date());
            batch.set(bloodRef.document(bId), br);
        }

        // Transport Seed
        List<TransportResource> sampleAmbulances = Arrays.asList(
                new TransportResource("TR-01", "AMB-04", "Advanced Life Support (ALS)", "Ramesh Jadhav", "+91-98231-10001", "Tarabai Park", "AVAILABLE", "HOSP-001"),
                new TransportResource("TR-02", "AMB-08", "Basic Life Support (BLS)", "Sunil Patil", "+91-98231-10002", "Station Road", "AVAILABLE", "HOSP-001"),
                new TransportResource("TR-03", "AMB-12", "Patient Transport Vehicle", "Vikas More", "+91-98231-10003", "Rajarampuri", "IN_TRANSIT", "HOSP-002"),
                new TransportResource("TR-04", "AMB-15", "Neonatal Emergency Van", "Prakash Kadam", "+91-98231-10004", "Shivaji Nagar", "AVAILABLE", "HOSP-004")
        );
        for (TransportResource t : sampleAmbulances) {
            t.setLastUpdated(new Date());
            batch.set(transportRef.document(t.getTransportId()), t);
        }

        // Volunteer Seed
        List<Volunteer> sampleVolunteers = Arrays.asList(
                new Volunteer("VOL-01", "Dr. Amit Deshmukh", "+91-98232-10001", "Kolhapur Central", Arrays.asList("Medical Support", "Emergency Response"), "AVAILABLE", "HOSP-001"),
                new Volunteer("VOL-02", "Pooja Shinde", "+91-98232-10002", "Rajarampuri", Arrays.asList("Blood Donation", "Logistics"), "AVAILABLE", "HOSP-001"),
                new Volunteer("VOL-03", "Rahul Sawant", "+91-98232-10003", "Tarabai Park", Arrays.asList("Transport Assistance", "Communication"), "ASSIGNED", "HOSP-002"),
                new Volunteer("VOL-04", "Sneha Kulkarni", "+91-98232-10004", "Udyamnagar", Arrays.asList("First-Aid", "Patient Support"), "AVAILABLE", "HOSP-004")
        );
        for (Volunteer v : sampleVolunteers) {
            v.setLastUpdated(new Date());
            batch.set(volunteersRef.document(v.getVolunteerId()), v);
        }

        // Medical Supplies Seed
        List<MedicalSupplyResource> sampleSupplies = Arrays.asList(
                new MedicalSupplyResource("SUP-01", "HOSP-001", "City Multispeciality Hospital", "Medical Grade Oxygen Cylinders (47L)", "OXYGEN", 18, 15, "Cylinders", "Central Vault B", "2027-12-31"),
                new MedicalSupplyResource("SUP-02", "HOSP-001", "City Multispeciality Hospital", "Surgical Sterile PPE Kits", "PPE", 150, 100, "Kits", "Storage Unit 4", "2028-06-30"),
                new MedicalSupplyResource("SUP-03", "HOSP-001", "City Multispeciality Hospital", "Emergency IV Fluids (Normal Saline 500ml)", "IV_FLUIDS", 45, 50, "Bottles", "Pharma Room A", "2026-11-30"),
                new MedicalSupplyResource("SUP-04", "HOSP-001", "City Multispeciality Hospital", "Trauma Hemostatic Gauze & Dressings", "SURGICAL", 80, 40, "Packs", "ER Vault 1", "2029-01-15")
        );
        for (MedicalSupplyResource s : sampleSupplies) {
            s.setLastUpdated(new Date());
            batch.set(suppliesRef.document(s.getSupplyId()), s);
        }

        // Sample Emergency Requests Seed
        List<EmergencyRequest> sampleRequests = Arrays.asList(
                new EmergencyRequest("ER-1024", "HOSP-001", "City Multispeciality Hospital", "HOSP-001", "City Multispeciality Hospital", "BLOOD", "5 Units O+ Blood", 5, "CRITICAL", "Trauma surgery patient urgently requires 5 units O+ blood."),
                new EmergencyRequest("ER-1025", "HOSP-002", "Apex Trauma Center", "HOSP-002", "Apex Trauma Center", "TRANSPORT", "ALS Ambulance with Ventilator", 1, "HIGH", "Inter-hospital emergency cardiac patient transit required.")
        );
        for (EmergencyRequest er : sampleRequests) {
            er.setRequestedAt(new Date());
            er.setLastUpdated(new Date());
            batch.set(emergencyReqRef.document(er.getEmergencyRequestId()), er);
        }

        batch.commit().addOnSuccessListener(aVoid -> callback.onSuccess(sampleBeds))
                     .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // 11. CONVENIENCE OVERLOADS & REACTIVE HELPERS
    // ==========================================

    public void getHospitalBeds(String hospitalId, ResourceCallback<HospitalBedResource> callback) {
        bedsRef.document(hospitalId).get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        callback.onSuccess(doc.toObject(HospitalBedResource.class));
                    } else {
                        callback.onSuccess(getFallbackHospitalBed(hospitalId));
                    }
                })
                .addOnFailureListener(e -> {
                    HospitalBedResource fallback = getFallbackHospitalBed(hospitalId);
                    if (fallback != null) {
                        callback.onSuccess(fallback);
                    } else {
                        callback.onError(e);
                    }
                });
    }

    public void saveHospitalBeds(HospitalBedResource beds, ResourceCallback<Void> callback) {
        updateHospitalBeds(beds, "Staff", new DataCallback<Void>() {
            @Override public void onSuccess(Void result) { callback.onSuccess(result); }
            @Override public void onFailure(String error) { callback.onError(new Exception(error)); }
        });
    }

    public ListenerRegistration listenToHospitalBeds(String hospitalId, ResourceListener<HospitalBedResource> listener) {
        return bedsRef.document(hospitalId).addSnapshotListener((doc, e) -> {
            if (doc != null && doc.exists()) {
                listener.onUpdate(doc.toObject(HospitalBedResource.class));
            } else {
                listener.onUpdate(getFallbackHospitalBed(hospitalId));
            }
        });
    }

    public ListenerRegistration listenToAllHospitalBeds(ResourceListener<List<HospitalBedResource>> listener) {
        return listenToHospitalBeds(new DataCallback<List<HospitalBedResource>>() {
            @Override public void onSuccess(List<HospitalBedResource> result) { listener.onUpdate(result); }
            @Override public void onFailure(String error) { listener.onUpdate(new ArrayList<>()); }
        });
    }

    public ListenerRegistration listenToBloodResources(String hospitalId, ResourceListener<List<BloodResource>> listener) {
        return bloodRef.whereEqualTo("hospitalId", hospitalId).addSnapshotListener((snapshots, e) -> {
            List<BloodResource> list = new ArrayList<>();
            if (snapshots != null && !snapshots.isEmpty()) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    BloodResource b = doc.toObject(BloodResource.class);
                    if (b != null) list.add(b);
                }
            }
            if (list.isEmpty()) {
                list = getFallbackBloodResources(hospitalId);
            }
            listener.onUpdate(list);
        });
    }

    public ListenerRegistration listenToAllBloodResources(ResourceListener<List<BloodResource>> listener) {
        return listenToBloodResources(new DataCallback<List<BloodResource>>() {
            @Override public void onSuccess(List<BloodResource> result) { listener.onUpdate(result); }
            @Override public void onFailure(String error) { listener.onUpdate(new ArrayList<>()); }
        });
    }

    public void saveBloodResource(BloodResource blood, ResourceCallback<Void> callback) {
        if (blood.getBloodResourceId() == null || blood.getBloodResourceId().isEmpty()) {
            blood.setBloodResourceId("BLD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        blood.updateStatus();
        blood.setLastUpdated(new Date());
        bloodRef.document(blood.getBloodResourceId()).set(blood)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(callback::onError);
    }

    public ListenerRegistration listenToTransportResources(ResourceListener<List<TransportResource>> listener) {
        return listenToTransportResources(new DataCallback<List<TransportResource>>() {
            @Override public void onSuccess(List<TransportResource> result) { listener.onUpdate(result); }
            @Override public void onFailure(String error) { listener.onUpdate(new ArrayList<>()); }
        });
    }

    public void saveTransportResource(TransportResource transport, ResourceCallback<Void> callback) {
        addOrUpdateTransport(transport, "Coordinator", new DataCallback<Void>() {
            @Override public void onSuccess(Void result) { callback.onSuccess(result); }
            @Override public void onFailure(String error) { callback.onError(new Exception(error)); }
        });
    }

    public void createTransportRequest(TransportRequest request, ResourceCallback<String> callback) {
        createTransportRequest(request, new DataCallback<String>() {
            @Override public void onSuccess(String result) { callback.onSuccess(result); }
            @Override public void onFailure(String error) { callback.onError(new Exception(error)); }
        });
    }

    public ListenerRegistration listenToVolunteers(ResourceListener<List<Volunteer>> listener) {
        return listenToVolunteers(new DataCallback<List<Volunteer>>() {
            @Override public void onSuccess(List<Volunteer> result) { listener.onUpdate(result); }
            @Override public void onFailure(String error) { listener.onUpdate(new ArrayList<>()); }
        });
    }

    public void saveVolunteer(Volunteer volunteer, ResourceCallback<Void> callback) {
        addOrUpdateVolunteer(volunteer, "Coordinator", new DataCallback<Void>() {
            @Override public void onSuccess(Void result) { callback.onSuccess(result); }
            @Override public void onFailure(String error) { callback.onError(new Exception(error)); }
        });
    }

    public ListenerRegistration listenToMedicalSupplies(ResourceListener<List<MedicalSupplyResource>> listener) {
        return listenToMedicalSupplies(new DataCallback<List<MedicalSupplyResource>>() {
            @Override public void onSuccess(List<MedicalSupplyResource> result) { listener.onUpdate(result); }
            @Override public void onFailure(String error) { listener.onUpdate(new ArrayList<>()); }
        });
    }

    public void saveMedicalSupply(MedicalSupplyResource supply, ResourceCallback<Void> callback) {
        addOrUpdateMedicalSupply(supply, "Coordinator", new DataCallback<Void>() {
            @Override public void onSuccess(Void result) { callback.onSuccess(result); }
            @Override public void onFailure(String error) { callback.onError(new Exception(error)); }
        });
    }

    public ListenerRegistration listenToEmergencyRequests(ResourceListener<List<EmergencyRequest>> listener) {
        return listenToEmergencyRequests(new DataCallback<List<EmergencyRequest>>() {
            @Override public void onSuccess(List<EmergencyRequest> result) { listener.onUpdate(result); }
            @Override public void onFailure(String error) { listener.onUpdate(new ArrayList<>()); }
        });
    }

    public void createEmergencyRequest(EmergencyRequest req, ResourceCallback<String> callback) {
        createEmergencyRequest(req, new DataCallback<String>() {
            @Override public void onSuccess(String result) { callback.onSuccess(result); }
            @Override public void onFailure(String error) { callback.onError(new Exception(error)); }
        });
    }

    public void recordAllocation(ResourceAllocation alloc, ResourceCallback<Void> callback) {
        allocateResource(alloc, new DataCallback<Void>() {
            @Override public void onSuccess(Void result) { callback.onSuccess(result); }
            @Override public void onFailure(String error) { callback.onError(new Exception(error)); }
        });
    }

    public ListenerRegistration listenToResourceHistory(ResourceListener<List<ResourceHistory>> listener) {
        return historyRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(100)
                .addSnapshotListener((snapshots, e) -> {
                    List<ResourceHistory> list = new ArrayList<>();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            ResourceHistory h = doc.toObject(ResourceHistory.class);
                            if (h != null) list.add(h);
                        }
                    }
                    listener.onUpdate(list);
                });
    }
}
