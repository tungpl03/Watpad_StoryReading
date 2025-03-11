package com.example.storywatpad.validate;

import android.util.Patterns;

public class Validate {
    public static boolean validateEmail(String email) {
        if(!email.isEmpty()&& Patterns.EMAIL_ADDRESS.matcher(email).matches()){
    return true;
}
        else {
            return false;
        }
    }
    public static boolean validatePassword(String password) {
        if(password.length()>=8 && password.length()<=24){
            return true;
        }
        else {
            return false;
        }
    }
}
