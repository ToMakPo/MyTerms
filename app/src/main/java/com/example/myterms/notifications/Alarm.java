package com.example.myterms.notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.myterms.application.Date;
import com.example.myterms.course.CourseViewActivity;
import com.example.myterms.term.TermViewActivity;

import java.util.HashMap;

public class Alarm extends BroadcastReceiver {
    private static final String TAG = "app: Alarm";
    public static final HashMap<Integer, Date> ALARMS = new HashMap<>();
    
    public static final Channel TERM_START_CHANNEL = Channel.create(
            "term_start_channel",
            "Term Start Channel",
            "Notifies the user when the term is about to start.",
            TermViewActivity.class,
            NotificationManager.IMPORTANCE_DEFAULT);
    public static final Channel TERM_END_CHANNEL = Channel.create(
            "term_end_channel",
            "Term End Channel",
            "Notifies the user when the term is about to end.",
            TermViewActivity.class,
            NotificationManager.IMPORTANCE_DEFAULT);
    public static final Channel COURSE_START_CHANNEL = Channel.create(
            "course_start_channel",
            "Course Start Channel",
            "Notifies the user when the course is about to start.",
            CourseViewActivity.class,
            NotificationManager.IMPORTANCE_DEFAULT);
    public static final Channel COURSE_END_CHANNEL = Channel.create(
            "course_end_channel",
            "Course End Channel",
            "Notifies the user when the course is about to end.",
            CourseViewActivity.class,
            NotificationManager.IMPORTANCE_DEFAULT);
    public static final Channel ASSESSMENT_COMPLETE_CHANNEL = Channel.create(
            "assessment_complete_channel",
            "Assessment Complete Channel",
            "Notifies the user when the assessment is getting close to the expected completion date.",
            CourseViewActivity.class,
            NotificationManager.IMPORTANCE_DEFAULT);
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle == null) throw new NullPointerException("Could not get the bundle.");
        
        Channel channel = bundle.getParcelable("channel");
        if (channel == null) throw new NullPointerException("Could not get the channel.");
        
        String title = bundle.getString("title");
        if (title == null) title = channel.NAME;
        
        String message = bundle.getString("message");
        if (message == null) message = "";
        
        int code = bundle.getInt("code", 0);
        
        channel.show(context, title, message, code, bundle);
    }
    
    public void setAlarm(Context context, Channel channel, String title, String message, Date trigger, int callerID) {
        Bundle bundle = new Bundle();
        setAlarm(context, channel, title, message, trigger, callerID, bundle);
    }
    public void setAlarm(Context context, Channel channel, String title, String message, Date trigger, int callerID, Bundle bundle) {
        int code = getCode(channel, callerID);
        
        if (ALARMS.containsKey(code))
            cancelAlarm(context, channel, code);
        
        bundle.putParcelable("channel", channel);
        bundle.putString("title", title);
        bundle.putString("message", message);
        bundle.putInt("code", code);
        
        Intent intent = new Intent(context, Alarm.class);
        intent.putExtra("bundle", bundle);
        
        PendingIntent sender = PendingIntent
                .getBroadcast(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger.getTimeInMillis(), sender);
            ALARMS.put(code, trigger);
        }
    }
    public void cancelAlarm(Context context, Channel channel, int callerID) {
        cancelAlarm(context, getCode(channel, callerID));
    }
    public void cancelAlarm(Context context, int code) {
        if (ALARMS.containsKey(code)) {
            Intent intent = new Intent(context, Alarm.class);
    
//            Log.d(TAG, "   cancel:  " + ALARMS.get(code).toSQL() + " - code: " + code);
    
            PendingIntent sender = PendingIntent
                    .getBroadcast(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(sender);
                ALARMS.remove(code);
            }
        }
//        else {Log.d(TAG, "   cancel:  NO ALARM SET            - code: " + code);}
    }
    public int getCode(Channel channel,  int callerID) {
        return (channel.BUFFER / Channel.BUFFER_MULTIPLIER != callerID / Channel.BUFFER_MULTIPLIER)
                ?  callerID + channel.BUFFER : callerID;
    }
}
