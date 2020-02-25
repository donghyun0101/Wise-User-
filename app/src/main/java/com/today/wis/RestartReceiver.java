package com.today.wis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class RestartReceiver extends BroadcastReceiver
{

    static public final String ACTION_RESTART_SERVICE = "wise.restart";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //2020-02-25 변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Intent in = new Intent(context, StartService.class);
            context.startForegroundService(in);
        }
        else
        {
            Intent in = new Intent(context, StartService.class);
            context.startService(in);
        }
    }
}