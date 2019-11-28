package com.example.myterms.application;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class Date extends GregorianCalendar implements Parcelable {
    protected static final String TAG = "app: Date";
    
    private boolean lastDay = false;

    protected Date() {
    }
    protected Date(TimeZone zone) {
        super(zone);
    }
    protected Date(Locale locale) {
        super(locale);
    }
    protected Date(TimeZone zone, Locale locale) {
        super(zone, locale);
    }
    protected Date(int year, int month, int day) {
        super(year, month - 1, day);
    }
    protected Date(int year, int month, int day, int hour, int minute) {
        super(year, month - 1, day, hour, minute);
    }
    protected Date(int year, int month, int day, int hour, int minute, int second) {
        super(year, month - 1, day, hour, minute, second);
    }
    protected Date(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        super(year, month - 1, day, hour, minute, second);
        
        this.addMilliseconds(millisecond);
    }
    
    protected Date(Parcel in) {
        setFromLong(in.readLong());
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.getTimeInMillis());
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Date> CREATOR = new Creator<Date>() {
        @Override
        public Date createFromParcel(Parcel in) {
            return new Date(in);
        }
        
        @Override
        public Date[] newArray(int size) {
            return new Date[size];
        }
    };
    
    public static Date today() {
        GregorianCalendar date = new GregorianCalendar();
        int year = date.get(YEAR);
        int month = date.get(MONTH) + 1;
        int day = date.get(DAY_OF_MONTH);
        
        return of(year, month, day);
    }
    public static Date now() {
        GregorianCalendar date = new GregorianCalendar();
        int year = date.get(YEAR);
        int month = date.get(MONTH) + 1;
        int day = date.get(DAY_OF_MONTH);
        int hour = date.get(HOUR_OF_DAY);
        int minute = date.get(MINUTE);
        int second = date.get(SECOND);
        int millisecond = date.get(MILLISECOND);
        
        return of(year, month, day, hour, minute, second, millisecond);
    }
    public static Date of(int year, int month, int day) {
        return new Date(year, month, day);
    }
    public static Date of(int year, int month, int day, int hour, int minute) {
        return new Date(year, month, day, hour, minute);
    }
    public static Date of(int year, int month, int day, int hour, int minute, int second) {
        return new Date(year, month, day, hour, minute, second);
    }
    public static Date of(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        return new Date(year, month, day, hour, minute, second, millisecond);
    }
    public static Date of(GregorianCalendar date) {
        int year = date.get(YEAR);
        int month = date.get(MONTH) + 1;
        int day = date.get(DAY_OF_MONTH);
        
        return of(year, month, day);
    }
    public static Date parseSQL(String dateString) {
        String sqlPattern = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|1\\d|2\\d|3[0-1]) (0\\d|1\\d|2[0-3]):([0-5]\\d):([0-5]\\d):(\\d{3})$";
//        String sqlPattern = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|1\\d|2\\d|3[0-1])( (0\\d|1\\d|2[0-3]:[0-5]\\d)(:[0-5]\\d(:\\d{3,9})?)?)?$";
        if (dateString.matches(sqlPattern)) {
            String[] split = dateString.split(" ");
            String[] dateParts = split[0].split("-");
            String[] timeParts = split[1].split(":");
    
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);
            
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            int second = Integer.parseInt(timeParts[2]);
            int millisecond = Integer.parseInt(timeParts[3].substring(0, 3));
    
            return new Date(year, month, day, hour, minute, second, millisecond);
        }
        return null;
    }
    public static Date parseLong(long dateLong) {
        Calendar calendar = getInstance();
        calendar.setTimeInMillis(dateLong);
        
        int year = calendar.get(YEAR);
        int month = calendar.get(MONTH) + 1;
        int day = calendar.get(DAY_OF_MONTH);
        
        int hour = calendar.get(HOUR_OF_DAY);
        int minute = calendar.get(MINUTE);
        int second = calendar.get(SECOND);
        int millisecond = calendar.get(MILLISECOND);

        return new Date(year, month, day, hour, minute, second, millisecond);
    }

    public boolean onAfter(GregorianCalendar date) {
        return this.compareTo(date) >= 0;
    }
    public boolean onAfter(int year, int month, int day) {
        return onAfter(new GregorianCalendar(year, month, day));
    }

    public boolean isAfter(GregorianCalendar date) {
        return this.compareTo(date) > 0;
    }
    public boolean isAfter(int year, int month, int day) {
        return isAfter(new GregorianCalendar(year, month, day));
    }

    public boolean onBefore(GregorianCalendar date) {
        return this.compareTo(date) <= 0;
    }
    public boolean onBefore(int year, int month, int day) {
        return onBefore(new GregorianCalendar(year, month, day));
    }

    public boolean isBefore(GregorianCalendar date) {
        return this.compareTo(date) < 0;
    }
    public boolean isBefore(int year, int month, int day) {
        return isBefore(new GregorianCalendar(year, month, day));
    }

    public boolean isBetween(GregorianCalendar sDate, GregorianCalendar eDate) {
        return onAfter(sDate) && onBefore(eDate);
    }
    public boolean isBetween(int sYear, int sMonth, int sDay, int eYear, int eMonth, int eDay) {
        GregorianCalendar sDate = new GregorianCalendar(sYear, sMonth, sDay);
        GregorianCalendar eDate = new GregorianCalendar(eYear, eMonth, eDay);

        return isBetween(sDate, eDate);
    }

    public int getYear() {
        return get(YEAR);
    }
    public int getMonth() {
        return get(MONTH) + 1;
    }
    public int getDay() {
        return get(DAY_OF_MONTH);
    }
    public int getHour() {
        return get(HOUR_OF_DAY);
    }
    public int getMinte() {
        return get(MINUTE);
    }
    public int getSecond() {
        return get(SECOND);
    }
    public int getMillisecond() {
        return get(MILLISECOND);
    }
    public int getDayOfWeek() {
        return get(DAY_OF_WEEK);
    }

    public Date getFirstOfMonth() {
        return getFirstOfMonth(0);
    }
    public Date getFirstOfMonth(int n) {
        Date date = of(this);
    
        date.setDay(1);
        date.addMonths(n);

        return date;
    }

    public Date getEndOfMonth() {
        return getEndOfMonth(0);
    }
    public Date getEndOfMonth(int n) {
        Date date = of(this);
    
        date.setDay(1);
        date.add(MONTH, 1 + n);
        date.add(DAY_OF_MONTH, -1);
    
        return date;
    }

    public int getLastDayOfMonth() {
        return getLastDayOfMonth(0);
    }
    public int getLastDayOfMonth(int n) {
        Date date = of(this);
    
        date.setDay(1);
        date.add(MONTH, 1 + n);
        date.add(DAY_OF_MONTH, -1);
    
        return get(DAY_OF_MONTH);
    }
    
    public Date getStartOfDay() {
        return of(this).setTime(0, 0, 0, 0);
    }
    public Date getEndOfDay() {
        return of(this).setTime(23, 59, 59, 999);
    }
    
    public long millisUntil(GregorianCalendar other) {
        return this.getTimeInMillis() - other.getTimeInMillis();
    }
    public double daysUntil(GregorianCalendar other) {
        long mpd = 24 * 60 * 60 * 1000;
        long millis = millisUntil(other);
        return millis / mpd;
    }
    public int daysUntilx(GregorianCalendar other) {
        int compared = -compareTo(other);
        if (compared == 0)
            return 0;

        int days = 0;

        Date t = of((compared > 0) ? this : other);
        Date o = of((compared < 0) ? this : other);

        Integer[] dim = new Integer[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        int tY = t.getYear();
        int oY = o.getYear();
        if (tY == oY) {
            if (t.getMonth() == o.getMonth()) {
                days = o.getDay() - t.getDay();
            } else {
                days += t.getLastDayOfMonth() - t.getDay();

                for(int i = t.getMonth(); i < o.getMonth() - 1; i++) {
                    if (i == 2 && isLeapYear(tY))
                        days += 1;

                    days += dim[i];
                }

                days += o.getDay();
            }
        } else {
            days += t.getLastDayOfMonth() - t.getDay();

            for(int i = t.getMonth(); i < 12; i++) {
                if (i == 2 && isLeapYear(tY))
                    days += 1;

                days += dim[i];
            }

            for(int i = tY + 1; i < oY; i++) {
                days += (isLeapYear(i) ? 366 : 365);
            }

            for(int i = 0; i < o.getMonth() - 1; i++) {
                if (i == 2 && isLeapYear(oY))
                    days += 1;

                days += dim[i];
            }

            days += o.getDay();
        }

        return days * compared;
    }
    
    public Date setDate(int year, int month, int day) {
        set(YEAR, year);
        set(MONTH, month - 1);
        set(DAY_OF_MONTH, day);
        return this;
    }
    public Date setYear(int year) {
        set(YEAR, year);
        return this;
    }
    public Date setMonth(int month) {
        set(MONTH, month - 1);
        return this;
    }
    public Date setDay(int day) {
        set(DAY_OF_MONTH, day);
        return this;
    }
    public Date setTime(int hour, int minute, int second, int millisecond) {
        set(HOUR_OF_DAY, hour);
        set(MINUTE, minute);
        set(SECOND, second);
        set(MILLISECOND, millisecond);
        return this;
    }
    public Date setTime(int hour, int minute, int second) {
        set(HOUR_OF_DAY, hour);
        set(MINUTE, minute);
        set(SECOND, second);
        return this;
    }
    public Date setTime(int hour, int minute) {
        set(HOUR_OF_DAY, hour);
        set(MINUTE, minute);
        return this;
    }
    public Date setHour(int hour) {
        set(HOUR_OF_DAY, hour);
        return this;
    }
    public Date setMinute(int minute) {
        set(MINUTE, minute - 1);
        return this;
    }
    public Date setSecond(int second) {
        set(SECOND, second);
        return this;
    }
    public Date setMillisecond(int millisecond) {
        set(MILLISECOND, millisecond);
        return this;
    }
    public Date setFromOther(GregorianCalendar other) {
        set(YEAR, other.get(YEAR));
        set(MONTH, other.get(MONTH));
        set(DAY_OF_MONTH, other.get(DAY_OF_MONTH));
        set(HOUR_OF_DAY, other.get(HOUR_OF_DAY));
        set(MINUTE, other.get(MINUTE));
        set(SECOND, other.get(SECOND));
        set(MILLISECOND, other.get(MILLISECOND));
        
        return this;
    }
    public Date setFromLong(long dateLong) {
        return setFromOther(Date.parseLong(dateLong));
    }
    
    public Date addYears(int n) {
        this.add(YEAR, n);
        return this;
    }
    public Date addMonths(int n) {
        boolean lastDay = getDay() == getLastDayOfMonth();
        
        this.add(MONTH, n);
        
        if (lastDay) setDay(getLastDayOfMonth());
        
        return this;
    }
    public Date addWeeks(int n) {
        this.add(DAY_OF_MONTH, n * 7);
        return this;
    }
    public Date addDays(int n) {
        this.add(DAY_OF_MONTH, n);
        return this;
    }
    public Date addHours(int n) {
        this.add(HOUR_OF_DAY, n);
        return this;
    }
    public Date addMinutes(int n) {
        this.add(MINUTE, n);
        return this;
    }
    public Date addSeconds(int n) {
        this.add(SECOND, n);
        return this;
    }
    public Date addMilliseconds(int n) {
        this.add(MILLISECOND, n);
        return this;
    }
    public Date addMilliseconds(long n) {
        return addMilliseconds((int) n);
    }
    
    public boolean isCurrentYear() {
        return getYear() == Date.today().getYear();
    }
    
    @Override
    public String toString() {
        return toSQL();
    }
    
    public String getFormatted(String format) {
        String str = String.format(format, this);
        
        return str;
    }
    public String getDateDisplay() {
        return getFormatted("%tY-%<tm-%<td");
    }
    public String getLongDateDisplay() {
        return getFormatted("%tB %<td, %<tY");
    }
    public String getShortDateDisplay() {
        return getFormatted("%tb %<td" + (isCurrentYear() ? "" : ", %<tY"));
    }
    public String getTimeDisplay() {
        return getFormatted("%tH:%<tM:%<tS");
    }
    public String toSQL() {
        String output = getFormatted("%tY-%<tm-%<td %<tH:%<tM:%<tS:%<tN");
        return output.substring(0, 23);
    }
    public long toLong() {
        return getTimeInMillis();
    }
    
    public static String getDateRangeDisplay(Date startDate, Date endDate) {
        String sDate = startDate.getFormatted("%tb %<te" + ((startDate.getYear() != endDate.getYear()) ? ", %<tY" : ""));
        String eDate = endDate.getFormatted("%tb %<te, %<tY");

        return sDate + " - " + eDate;
    }
}
