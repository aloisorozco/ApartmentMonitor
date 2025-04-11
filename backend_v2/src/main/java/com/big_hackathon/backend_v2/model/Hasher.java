package com.big_hackathon.backend_v2.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import lombok.SneakyThrows;

// "Hasher" object to hlep with hashing - we use it in both userDAO and apartmentDAO
public class Hasher {

    @SneakyThrows
    public static String hashData(String data){
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String hexString = hashByte2Hex(digest.digest(data.getBytes(StandardCharsets.UTF_8)));
        return hexString;
    }

    // translating byte hash to hex 
    private static String hashByte2Hex(byte[] hash){
        
        // size of 2 * len of hash since every byte becomes a two char hex value
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0'); // ex: hex = a, we add 0 in front to keep the hex num two char long
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
