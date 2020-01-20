package com.today.wis;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

public class StartService extends Service
{
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate()
    {
        super.onCreate();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new WiseBroadcastReceiver();
        registerReceiver(mReceiver, filter);

        registerRestartAlarm(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent == null)
        {

            startForeground(1, new Notification());
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        registerRestartAlarm(false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

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
}