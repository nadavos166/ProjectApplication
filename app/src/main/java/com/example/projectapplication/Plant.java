package com.example.projectapplication;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class Plant
{
    String key;
    String name;
    String place;
    String time;
    long date;
    int wateramount;
    String imageurl;
    public long getDate()
    {
        return date;
    }
    public void setDate(long date)
    {
        this.date = date;
    }
    public Plant(String key, String name, String place, long date, String time, int wateramount, String imageurl)
    {
        this.key = key;
        this.name = name;
        this.place = place;
        this.time = time;
        this.date = date;
        this.wateramount = wateramount;
        this.imageurl=imageurl;
    }
    public Plant( String name, String place, long date, String time, int wateramount,String imageurl)
    {
        this.name = name;
        this.place = place;
        this.time = time;
        this.wateramount = wateramount;
        this.date = date;
        this.imageurl=imageurl;
    }
    public String getImageUrl()
    {
        return imageurl;
    }
    public void setImageUrl(String imageurl)
    {
        this.imageurl = imageurl;
    }
    public Plant()
    {

    }
    public void setKey(String key)
    {
        this.key = key;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setPlace(String place)
    {
        this.place = place;
    }
    public void setTime(String time)
    {
        this.time = time;
    }
    public void setWateramount(int wateramount)
    {
        this.wateramount = wateramount;
    }
    public String getKey()
    {
        return key;
    }
    public String getName()
    {
        return name;
    }
    public String getPlace()
    {
        return place;
    }
    public String getTime()
    {
        return time;
    }
    public int getWateramount()
    {
        return wateramount;
    }
    public Calendar parseDateToCalendar()
    {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance();
            if (date != null)
            {
                calendar.clear();
                calendar.setTimeInMillis(this.date);
                calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
                calendar.set(Calendar.MINUTE, date.getMinutes());
            }
            return calendar;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
