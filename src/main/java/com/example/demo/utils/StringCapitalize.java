package com.example.demo.utils;

public class StringCapitalize {
    
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}