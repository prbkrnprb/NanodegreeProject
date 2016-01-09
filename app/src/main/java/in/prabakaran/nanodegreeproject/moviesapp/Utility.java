package in.prabakaran.nanodegreeproject.moviesapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Prabakaran on 09-01-2016.
 */
public class Utility {
    public static int formatDateToYear(String inpDate){
        String patern = "EEE MMM d hh:mm:ss zzz yyyy";
        SimpleDateFormat format = new SimpleDateFormat(patern);
        try {
            Date date = format.parse(inpDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String formatRating(Double rate){
        return rate + "/10";
    }
}
