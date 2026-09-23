package com.example.organshare.utils;

public class Constants {
    // User Roles
    public static final String ROLE_DONOR = "DONOR";
    public static final String ROLE_HOSPITAL = "HOSPITAL";
    public static final String ROLE_ORGAN_BANK = "ORGAN_BANK";
    public static final String ROLE_DELIVERY = "DELIVERY";
    public static final String ROLE_ADMIN = "ADMIN";

    // Request Statuses
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_UNDER_REVIEW = "UNDER_REVIEW";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_ALLOCATED = "ALLOCATED";
    public static final String STATUS_IN_TRANSIT = "IN_TRANSIT";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // Inventory Statuses
    public static final String INV_AVAILABLE = "AVAILABLE";
    public static final String INV_RESERVED = "RESERVED";
    public static final String INV_ALLOCATED = "ALLOCATED";
    public static final String INV_IN_TRANSIT = "IN_TRANSIT";
    public static final String INV_DELIVERED = "DELIVERED";
    public static final String INV_UNAVAILABLE = "UNAVAILABLE";

    // Delivery Statuses
    public static final String DEL_ASSIGNED = "ASSIGNED";
    public static final String DEL_PICKUP_STARTED = "PICKUP_STARTED";
    public static final String DEL_PICKED_UP = "PICKED_UP";
    public static final String DEL_IN_TRANSIT = "IN_TRANSIT";
    public static final String DEL_CHECKPOINT_1 = "CHECKPOINT_1";
    public static final String DEL_CHECKPOINT_2 = "CHECKPOINT_2";
    public static final String DEL_NEAR_DESTINATION = "NEAR_DESTINATION";
    public static final String DEL_DELIVERED = "DELIVERED";
    public static final String DEL_COMPLETED = "COMPLETED";
    public static final String DEL_DELAYED = "DELAYED";

    // Emergency Levels
    public static final String EMERGENCY_CRITICAL = "EMERGENCY";
    public static final String EMERGENCY_URGENT = "URGENT";
    public static final String EMERGENCY_NORMAL = "NORMAL";

    // Organs List
    public static final String[] ORGAN_TYPES = {
        "Kidney",
        "Liver",
        "Heart",
        "Lungs",
        "Pancreas",
        "Cornea",
        "Bone Marrow",
        "Small Intestine"
    };

    // Blood Groups
    public static final String[] BLOOD_GROUPS = {
        "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    };

    // Shared Prefs
    public static final String PREF_NAME = "OrganShareSession";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_ROLE = "user_role";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
}
