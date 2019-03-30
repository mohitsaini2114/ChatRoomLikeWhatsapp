package com.example.vaibhav.login_inclass6;


import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.ocpsoft.prettytime.PrettyTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Comparator;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public class MessageModel implements Serializable, Comparator<MessageModel> {
    private String DATEFORMAT = "yyyy-MM-dd HH:mm:ss" ;

    String user_fname;
    String user_lname;
    String user_id;
    String id;
    String message;
    String created_at;


    public String getTimeString() {
        /*DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime o1Date = formatter.parseDateTime(this.created_at );*/

        SimpleDateFormat format = new SimpleDateFormat(DATEFORMAT,Locale.UK);

        Date created_Date;
        try {

            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            created_Date = format.parse(this.created_at);

            PrettyTime prettyTime = new PrettyTime(Locale.UK);
            this.timeString = prettyTime.format(created_Date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*if(Days.daysBetween(o1Date,new DateTime()).getDays() > 0)
        {
            this.timeString = Days.daysBetween(o1Date,new DateTime()).getDays()+" days from Now";
        }
        else if(Hours.hoursBetween(o1Date,new DateTime()).getHours() > 0)
        {
            this.timeString = Hours.hoursBetween(o1Date,new DateTime()).getHours()+" hours from Now";
        }
        else {
            if(Minutes.minutesBetween(o1Date,new DateTime()).getMinutes() > 0)
            this.timeString = Minutes.minutesBetween(o1Date,new DateTime()).getMinutes()+" minutes from Now";
            else
                this.timeString ="Just Now";
        }*/
        return timeString;
    }

    String timeString;

    @Override
    public int compare(MessageModel o1, MessageModel o2) {
        try {

        DateFormat format = new SimpleDateFormat(DATEFORMAT);
        Date o1Date;
        Date o2Date;

        o1Date = format.parse(o1.created_at);
        o2Date = format.parse(o2.created_at);

            return o1Date.compareTo(o2Date);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
