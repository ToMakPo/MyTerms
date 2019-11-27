package com.example.myterms.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.core.app.NotificationCompat;

import com.example.myterms.R;
import com.example.myterms.application.App;
import com.example.myterms.application.Date;

import static com.example.myterms.notifications.Alarm.ALARMS;

public class Channel implements Parcelable {
    private static final String TAG = "app: Channel";
    
    private static final NotificationManager MANAGER = App.MAIN.getSystemService(NotificationManager.class);
    
    final String ID;
    final String NAME;
    final String DESCRIPTION;
    final int IMPORTANCE;
    final int BUFFER;
    private final Intent INTENT;
    
    static final int BUFFER_MULTIPLIER = 10000;
    static int buffer = 0;
    
    private Channel(String id, String name, String description, Class<?> activityClass, int importance) {
        this.ID = id;
        this.NAME = name;
        this.DESCRIPTION = description;
        this.IMPORTANCE = importance;
        BUFFER = ++buffer * BUFFER_MULTIPLIER;
        
        INTENT = new Intent(App.MAIN, activityClass);
        INTENT.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        if (MANAGER != null) {
            NotificationChannel termStartChannel = new NotificationChannel(ID, NAME, IMPORTANCE);
            termStartChannel.setDescription(DESCRIPTION);
            MANAGER.createNotificationChannel(termStartChannel);
        }
    }
    
    protected Channel(Parcel in) {
        ID = in.readString();
        NAME = in.readString();
        DESCRIPTION = in.readString();
        IMPORTANCE = in.readInt();
        BUFFER = in.readInt();
        INTENT = in.readParcelable(Intent.class.getClassLoader());
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(NAME);
        dest.writeString(DESCRIPTION);
        dest.writeInt(IMPORTANCE);
        dest.writeInt(BUFFER);
        dest.writeParcelable(INTENT, flags);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }
        
        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
    
    public static Channel create(String id, String name, String description, Class<?> activityClass) {
        return new Channel(id, name, description, activityClass, NotificationManager.IMPORTANCE_DEFAULT);
    }
    public static Channel create(String id, String name, String description, Class<?> activityClass, int importance) {
        return new Channel(id, name, description, activityClass, importance);
    }
    
    public void show(Context context, String title, String message, int callerID) {
        show(context, title, message, callerID, new Bundle());
    }
    public void show(Context context, String title, String message, int callerID, Bundle bundle) {
        int code = getCode(callerID);
        ALARMS.remove(code);
        
        INTENT.putExtras(bundle);
        
        Notification notification = new NotificationCompat.Builder(context, ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();

        MANAGER.notify(callerID, notification);
    }
    public int getCode(int callerID) {
        return (BUFFER / BUFFER_MULTIPLIER != callerID / BUFFER_MULTIPLIER)
                ?  callerID + BUFFER : callerID;
    }
    
    public void setAlarm(Context context, String title, String message, Date trigger, int callerID) {
        new Alarm().setAlarm(context, this, title, message, trigger, callerID);
    }
    public void setAlarm(Context context, String title, String message, Date trigger, int callerID, Bundle bundle) {
        new Alarm().setAlarm(context, this, title, message, trigger, callerID, bundle);
    }
    public void cancelAlarm(Context context, int callerID) {
        new Alarm().cancelAlarm(context, getCode(callerID));
    }
}