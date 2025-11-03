package fr.thefox580.collectEveryBlockDuo.utils;

public class Conversions {

    public static String secondsToFormattedTime(int seconds){

        int minutes = seconds / 60;
        int hours = seconds / 3600;
        seconds %= 60;

        String formatted = String.valueOf(seconds);
        if (seconds < 10){
            formatted = "0"+seconds;
        }
        formatted = minutes+":"+formatted;
        if (minutes < 10){
            formatted = "0"+formatted;
        }
        formatted = hours+":"+formatted;
        if (hours < 10){
            formatted = "0"+formatted;
        }

        return formatted;

    }

}
