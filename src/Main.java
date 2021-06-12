import ChatRoom.RoleTag;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
//        Date da= new Date();
//        System.out.println(da);
//        System.out.println(java.time.LocalDateTime.now());
//        Instant i = Instant.now();
//        System.out.println(i);
//        System.out.println( new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) );
//        Date d = new Date();
//        System.out.println( (d.getTime() / 1000 / 60 / 60) % 24 + ":" + (d.getTime() / 1000 / 60) % 60 + ":" + (d.getTime() / 1000) % 60 );
//        while (d.getTime())
//        long start = this.getSystemTime();
        System.out.println(System.currentTimeMillis());
       long start = System.currentTimeMillis();
       long end = start+10*1000;
       while (System.currentTimeMillis()<end)
       {
           System.out.println("if niggers werent slave, they were probably dead");
           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }

    }
}
