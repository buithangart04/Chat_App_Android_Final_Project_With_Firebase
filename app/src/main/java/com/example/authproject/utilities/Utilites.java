package com.example.authproject.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilites {
    public static String getDateFormat (Date date ){
        return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(date);
    }
}
