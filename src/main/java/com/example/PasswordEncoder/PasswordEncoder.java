package com.example.PasswordEncoder;

import java.util.Base64;

public class PasswordEncoder {
    public static String encodePassword(String password) {
        byte[] encodedBytes = Base64.getEncoder().encode(password.getBytes());
        return new String(encodedBytes);
    }
}