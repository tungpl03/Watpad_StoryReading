package com.example.storywatpad.support;

import java.util.Random;

public class GenPassword {
    public static String genpassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = rand.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }
}
