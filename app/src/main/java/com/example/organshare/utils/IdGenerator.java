package com.example.organshare.utils;

import java.util.Random;

public class IdGenerator {
    private static final Random random = new Random();

    public static String generateRequestId() {
        int num = 10000 + random.nextInt(90000);
        return "REQ-2026-" + num;
    }

    public static String generateOrderId() {
        int num = 10000 + random.nextInt(90000);
        return "ORD-2026-" + num;
    }

    public static String generateInventoryId() {
        int num = 1000 + random.nextInt(9000);
        return "INV-" + num;
    }

    public static String generateDeliveryId() {
        int num = 1000 + random.nextInt(9000);
        return "DEL-2026-" + num;
    }

    public static String generateDonorId() {
        int num = 1000 + random.nextInt(9000);
        return "DNR-" + num;
    }

    public static String generateCheckpointId() {
        int num = 100 + random.nextInt(900);
        return "CP-" + num;
    }

    public static String generateAllocationId() {
        int num = 10000 + random.nextInt(90000);
        return "ALC-2026-" + num;
    }
}
