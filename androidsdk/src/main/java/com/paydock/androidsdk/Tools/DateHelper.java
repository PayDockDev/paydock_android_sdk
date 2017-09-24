package com.paydock.androidsdk.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    public static boolean isFuture(String monthString, String yearString) {

        Calendar calendarCurrent = Calendar.getInstance();

        Calendar calendarExpiry = Calendar.getInstance();
        calendarExpiry.clear();
        try
        {
            DateFormat formatter = new SimpleDateFormat("MMyy", Locale.getDefault());
            if ((Integer.valueOf(monthString) < 1) || (Integer.valueOf(monthString) > 12)){
                return false;
            }
            Date date = formatter.parse(monthString + yearString);
            calendarExpiry.setTime(date);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        return calendarExpiry.after(calendarCurrent);
    }

    public static String convertCardIOFormat(int monthInt, int yearInt) {
        try {
            String monthString = String.valueOf(monthInt);
            String yearString = String.valueOf(yearInt);
            if (monthString.length() == 1)
                monthString = "0" + monthString;
            if (yearString.length() == 4)
                yearString = yearString.substring(2,4);
            return monthString + yearString;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return "0101";
        }

    }

}
