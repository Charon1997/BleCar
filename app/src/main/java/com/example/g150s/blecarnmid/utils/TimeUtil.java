package com.example.g150s.blecarnmid.utils;

/**
 * Created by G150S on 2017/3/14.
 */

public class TimeUtil {

    public static String MilltoMinute(int mills)
    {
        int minute = mills/60;
        int mill = mills % 60;

        StringBuilder minuteString = new StringBuilder();
        minuteString.append(Integer.toString(minute));
        if (minuteString.length() == 1)
            minuteString.insert(0,"0");

        StringBuilder millString = new StringBuilder();
        millString.append(Integer.toString(mill));
        if (millString.length() == 1)
          millString.insert(0,"0");

        String end = minuteString + ":" + millString;
        return end;
    }
}
