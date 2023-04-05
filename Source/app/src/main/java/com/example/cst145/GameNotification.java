package com.example.cst145;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

/**
 * @author Tass CST145
 */
public class GameNotification extends Service
{

    public static final String CHANNEL_NUM = "1111";
    public static final CharSequence CHANNEL_NAME = "channel" + CHANNEL_NUM;
    public static final String TICKER_TEXT = "Don't leave yet!";
    public static final String CONTENT_TITLE = "Hey!";
    public static final String CONTENT_TEXT = "Come back for more square dodging action";
    public static final String ON_BIND_MSG = "Not yet implemented";

    public GameNotification()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException(ON_BIND_MSG);
    }

    /**
     * This class is pretty much the same as the one we created in class for the scheduled notification
     */
    public void onCreate()
    {
        NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification coolAndNotAnnoyingReminder;

        Intent notifIntent = new Intent(this, GameNotification.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + MainActivity.NOTIF_TIME, pendingIntent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel nChannel = new NotificationChannel(CHANNEL_NUM, CHANNEL_NAME, importance);
            notifyMgr.createNotificationChannel(nChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_NUM)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(CHANNEL_NUM)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setTicker(TICKER_TEXT)
                    .setContentTitle(CONTENT_TITLE)
                    .setContentText(CONTENT_TEXT)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent);
            coolAndNotAnnoyingReminder = builder.build();
        }
        else
        {
            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(TICKER_TEXT)
                    .setContentTitle(CONTENT_TITLE)
                    .setContentText(CONTENT_TEXT)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent);
            coolAndNotAnnoyingReminder = builder.build();
        }
        notifyMgr.notify(1, coolAndNotAnnoyingReminder);

    }
}