package top.yangyuscript.knowledge.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static String getTime(String formatStr){
        LocalDateTime ldt = LocalDateTime.now();
        return ldt.format(DateTimeFormatter.ofPattern(formatStr));
    }

    public static String getNowTime(){
        return getTime("yyyy-MM-dd HH:mm:ss");
    }


    public static void main(String[] args) {
        System.out.println(getTime("yyyy-MM-dd HH:mm:ss"));
    }
}
