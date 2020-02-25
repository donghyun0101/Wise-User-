package com.today.wis;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class StartService extends Service
{
    private BroadcastReceiver mReceiver;
    public static Intent serviceIntent = null; //2020-02-25 추가

    @Override
    public void onCreate()
    {
        super.onCreate();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new WiseBroadcastReceiver();
        registerReceiver(mReceiver, filter);

        //2020-02-25 주석처리
        //registerRestartAlarm(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //2020-02-25 추가및 변경
        serviceIntent = intent;
        serviceNotification();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //2020-02-25 추가
        serviceIntent = null;
        setAlarmTimer();
        unregisterReceiver(mReceiver);

        //2020-02-25 주석처리
        //registerRestartAlarm(false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    /*
    //2020-02-25 주석처리
    public void registerRestartAlarm(boolean isOn){
        Intent intent = new Intent(StartService.this, WiseBroadcastReceiver.class);
        intent.setAction(RestartReceiver.ACTION_RESTART_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        if(isOn){
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, 60000, sender);
            Log.e("확인", "am");
        }else{
            am.cancel(sender);
        }
    }
     */

    //2020-02-25 추가
    private void serviceNotification()
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26)
        {
            String Channel_id = "wise_channel";
            NotificationChannel channel = new NotificationChannel(Channel_id, "wise Channel", NotificationManager.IMPORTANCE_LOW);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, Channel_id);
        }
        else
        {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.drawable.ic_launcher_background)
                //.setContent(remoteViews)
                .setContentTitle("오늘의 칭찬")
                .setContentText("백그라운드 실행중")
                .setContentIntent(pendingIntent);
        startForeground(1, builder.build());
    }

    //2020-02-25 추가
    protected void setAlarmTimer()
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, RestartReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }
}