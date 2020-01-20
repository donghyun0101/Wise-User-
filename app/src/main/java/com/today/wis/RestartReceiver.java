package com.today.wis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartReceiver extends BroadcastReceiver
{

    static public final String ACTION_RESTART_SERVICE = "wise.restart";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(ACTION_RESTART_SERVICE))
        {
            Intent i = new Intent(context, StartService.class);
            context.startService(i);
        }
    }
}