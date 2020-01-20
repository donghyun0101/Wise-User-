package com.today.wis;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class WiseBroadcastReceiver extends BroadcastReceiver
{
    @Override

    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
        {
            Log.e("onReceive", "SCREEN_ON");
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
            try
            {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e)
            {
                e.printStackTrace();
            }
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {
            Log.e("onReceive", "SCREEN_OFF");
        }
        else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Log.e("onReceive", "BOOT_COMPLETED");
        }
    }
}