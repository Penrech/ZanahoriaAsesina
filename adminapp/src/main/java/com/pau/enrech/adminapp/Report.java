package com.pau.enrech.adminapp;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Report {

    public Long date;
    public String nomApReporter;
    public String nomApReported;
    public String message;
    public String ReporterId;
    public String ReportedId;
    private static Calendar calendarStatics = new GregorianCalendar();

    public String getDayString(){
        calendarStatics.setTimeInMillis(this.date);
        int dayM = calendarStatics.get(Calendar.DAY_OF_MONTH);
        int dayW = calendarStatics.get(Calendar.DAY_OF_WEEK);
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
        calendarStatics.setTimeInMillis(this.date);
        int hour = calendarStatics.get(Calendar.HOUR);
        int min = calendarStatics.get(Calendar.MINUTE);

        return String.format("%02d:%02d",hour,min);

    }
    public String getReportMessageString(){
        return String.format("%s ha reportado a %s",this.nomApReporter,this.nomApReported);
    }



    public Report(){}
    public Report(Long date, String nomApReporter, String nomApReported, String message, String reporterId,String reportedId) {
        this.date = date;
        this.nomApReporter = nomApReporter;
        this.nomApReported = nomApReported;
        this.message = message;
        this.ReporterId= reporterId;
        this.ReportedId = reportedId;
    }

}