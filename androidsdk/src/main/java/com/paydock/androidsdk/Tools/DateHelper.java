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

    public static String convertCardIOFormat(Integer monthInt, Integer yearInt) {
        try {
            if (monthInt == 0 || yearInt == 0)
                return null;
            String monthString = String.valueOf(monthInt);
            String yearString = String.valueOf(yearInt);
            if (monthString.length() == 1)
                monthString = "0" + monthString;
            else if (monthString.length() > 2)
                return null;
            if (yearString.length() == 4)
                yearString = yearString.substring(2,4);
            else
                return null;
            return monthString + yearString;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }

    }

}
