package com.example.authproject.utilities;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FunctionalUtilities {
    public static String getDateFormat(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(date);
    }

    private static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public String getRandomImageName() {
        return "images/" + UUID.randomUUID();
    }

    public static String generateId(String type) {
        switch (type) {
            case "user":
                return "us" + getUUID();
            case "group":
                return "gr" + getUUID();
            default:
                return null;
        }
    }

}
