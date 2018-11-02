package com.htdata.crawl.core.manager;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
@Component
public class TimeFormatAdjustManager {

    private final Pattern pattern1 = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d");
    private final Pattern pattern2 = Pattern.compile("\\d\\d\\d\\d-\\d-\\d\\d");
    private final Pattern pattern3 = Pattern.compile("\\d\\d\\d\\d-\\d-\\d");


    public String getAdjustTime(String time){
        if(pattern1.matcher(time).matches()){
            String adjustTime = time.substring(0,time.length()-1)+"0"+time.substring(time.length()-1);
            return adjustTime;
        }
        if(pattern2.matcher(time).matches()){
            String adjustTime=time.substring(0,time.length()-4)+"0"+time.substring(time.length()-4);
            return adjustTime;
        }
        if(pattern3.matcher(time).matches()){
            String adjustTime = time.substring(0,time.length()-3)+"0"+time.charAt(5)+"-0"+time.substring(time.length()-1);
            return adjustTime;
        }
        return time;
    }

    public static void main(String[] args) {
        System.out.println(new TimeFormatAdjustManager().getAdjustTime("2018-9-2"));
    }
}
