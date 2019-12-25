package de.hosenhasser.duetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ContractionRunningService.ACTION_STOP.equals(action)) {
                ContractionRunningService.startActionStop(context);
            }
        }
    }
}
