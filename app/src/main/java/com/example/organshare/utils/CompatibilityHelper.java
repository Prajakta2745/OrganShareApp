package com.example.organshare.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompatibilityHelper {

    private static final Map<String, List<String>> DONOR_TO_RECIPIENT_MAP = new HashMap<>();
    private static final Map<String, Integer> ORGAN_PRESERVATION_HOURS = new HashMap<>();

    static {
        // ABO & Rh Blood Group Compatibility rules (Donor -> List of compatible Recipients)
        DONOR_TO_RECIPIENT_MAP.put("O-", Arrays.asList("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")); // Universal Donor
        DONOR_TO_RECIPIENT_MAP.put("O+", Arrays.asList("O+", "A+", "B+", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("A-", Arrays.asList("A-", "A+", "AB-", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("A+", Arrays.asList("A+", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("B-", Arrays.asList("B-", "B+", "AB-", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("B+", Arrays.asList("B+", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("AB-", Arrays.asList("AB-", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("AB+", Collections.singletonList("AB+")); // Universal Recipient for receiving, only AB+ for donating

        // Typical Cold Ischemia Preservation Limits (Hours)
        ORGAN_PRESERVATION_HOURS.put("Heart", 4);
        ORGAN_PRESERVATION_HOURS.put("Lungs", 6);
        ORGAN_PRESERVATION_HOURS.put("Liver", 12);
        ORGAN_PRESERVATION_HOURS.put("Pancreas", 12);
        ORGAN_PRESERVATION_HOURS.put("Kidney", 24);
        ORGAN_PRESERVATION_HOURS.put("Small Intestine", 8);
        ORGAN_PRESERVATION_HOURS.put("Cornea", 168); // 7 days
        ORGAN_PRESERVATION_HOURS.put("Bone Marrow", 48);
    }

    /**
     * Checks if donor blood group can donate to patient/recipient blood group.
     */
    public static boolean isBloodCompatible(String donorBloodGroup, String recipientBloodGroup) {
        if (donorBloodGroup == null || recipientBloodGroup == null) return false;
        List<String> compatibleRecipients = DONOR_TO_RECIPIENT_MAP.get(donorBloodGroup.trim().toUpperCase());
        return compatibleRecipients != null && compatibleRecipients.contains(recipientBloodGroup.trim().toUpperCase());
    }

    /**
     * Returns standard cold ischemia preservation limit in hours for an organ.
     */
    public static int getStandardPreservationHours(String organType) {
        if (organType == null) return 24;
        Integer hours = ORGAN_PRESERVATION_HOURS.get(organType.trim());
        return hours != null ? hours : 24;
    }

    /**
     * Provides software-assisted compatibility summary string.
     * Note: Purely administrative matching aid.
     */
    public static String getCompatibilityStatusString(String organRequired, String recipientBloodGroup, String inventoryOrgan, String inventoryBloodGroup) {
        if (!organRequired.equalsIgnoreCase(inventoryOrgan)) {
            return "Incompatible Organ Type";
        }
        if (isBloodCompatible(inventoryBloodGroup, recipientBloodGroup)) {
            return "Software Compatible (ABO Matched)";
        } else {
            return "Incompatible Blood Group";
        }
    }
}
