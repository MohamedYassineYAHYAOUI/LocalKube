package fr.umlv.LocalKube.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * the application timer, calculates the elapsed time of the application
 */
class AppTimer {
    private long launchTime;
    private long stopTime;
    private long elapsedTime;

    AppTimer(long startTime){
        this.launchTime = startTime;
        elapsedTime = 0;
        stopTime = 0;
    }

    private String millisToMinSec(long timeInMillis){

        var seconds = timeInMillis/1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return minutes+"m"+seconds+"s";
    }
    /**
     * the elapsed time of the application that follows the format ..m..s
     * @return elapsed time
     */
    String elapsedTime(){
        if(stopTime ==0){
            return millisToMinSec(System.currentTimeMillis() - launchTime );
        }
        return millisToMinSec(elapsedTime);
    }

}
