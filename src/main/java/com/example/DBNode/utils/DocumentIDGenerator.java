package com.example.DBNode.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class DocumentIDGenerator {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final MessageDigest SHA_256;

    static {
        try {
            SHA_256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to initialize SHA-256 MessageDigest", e);
        }
    }

    public static String getUniqueID() {
        byte[] uuidBytes = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
        byte[] hash = SHA_256.digest(uuidBytes);
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
