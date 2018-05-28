package com.pau.enrech.adminapp;


import java.util.Calendar;
import java.util.GregorianCalendar;

public class Statistics {

   public Long date;
   public String nomApKiller;
   public String nomApVictim;
   private static Calendar calendarStatics2 = new GregorianCalendar();

   public  String getDayString(){
       calendarStatics2.setTimeInMillis(this.date);
       int dayM = calendarStatics2.get(Calendar.DAY_OF_MONTH);
       int dayW = calendarStatics2.get(Calendar.DAY_OF_WEEK);
       String dayS = null;
       switch (dayW){
           case Calendar.SUNDAY: dayS = "Domingo";
               break;
           case Calendar.MONDAY: dayS = "Lunes";
           break;
           case Calendar.TUESDAY: dayS = "Martes";
           break;
           case Calendar.WEDNESDAY: dayS = "Miércoles";
           break;
           case Calendar.THURSDAY: dayS = "Jueves";
           break;
           case Calendar.FRIDAY: dayS = "Viernes";
           break;
           case Calendar.SATURDAY: dayS = "Sábado";
           break;
       }

       return  String.format("%s, %d",dayS,dayM);
   }

    public String getHourString(){
        calendarStatics2.setTimeInMillis(this.date);
        int hour = calendarStatics2.get(Calendar.HOUR);
        int min = calendarStatics2.get(Calendar.MINUTE);

        return String.format("%02d:%02d",hour,min);

    }
    public String getStaticsMessageString(){
       return String.format("%s ha eliminado a %s",this.nomApKiller,this.nomApVictim);
    }



   public Statistics(){}
    public Statistics(Long date, String nomApKiller, String nomApVictim) {
        this.date = date;
        this.nomApKiller = nomApKiller;
        this.nomApVictim = nomApVictim;
    }

}
