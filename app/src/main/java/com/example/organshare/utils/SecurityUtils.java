package com.example.organshare.utils;

public class SecurityUtils {

    /**
     * Masks sensitive phone numbers for public/hospital listings
     * e.g. +1 555-123-4567 -> +1 555-***-**67
     */
    public static String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 7) {
            return "Confidential";
        }
        int len = phone.length();
        return phone.substring(0, 4) + "****" + phone.substring(len - 2);
    }

    /**
     * Masks names for donor registry privacy
     * e.g. John Doe -> J*** D***
     */
    public static String maskName(String firstName, String lastName) {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            sb.append(firstName.charAt(0)).append("***");
        }
        if (lastName != null && !lastName.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(lastName.charAt(0)).append("***");
        }
        return sb.length() > 0 ? sb.toString() : "Anonymous Donor";
    }

    /**
     * Validates whether a user role is permitted to see full contact information.
     */
    public static boolean canAccessFullContact(String role) {
        return Constants.ROLE_ADMIN.equals(role) || Constants.ROLE_ORGAN_BANK.equals(role);
    }
}
