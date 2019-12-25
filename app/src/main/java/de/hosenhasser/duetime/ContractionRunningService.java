package de.hosenhasser.duetime;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;

import de.hosenhasser.duetime.content.Contraction;
import de.hosenhasser.duetime.content.ContractionDao;
import de.hosenhasser.duetime.content.ContractionDatabase;
import de.hosenhasser.duetime.content.ContractionsContentProvider;
import de.hosenhasser.duetime.ui.start_stop.StartStopViewModel;

public class ContractionRunningService extends IntentService  {
    public static final String ACTION_START = "de.hosenhasser.duetime.action.START";
    public static final String ACTION_STOP = "de.hosenhasser.duetime.action.STOP";
    public static final String ACTION_UPDATE_NOTIFICATION = "de.hosenhasser.duetime.action.UPDATE_NOTIFICATION";
    public static final String ACTION_DELETE_ALL = "de.hosenhasser.duetime.action.DELETE_ALL";

    private static final String NOTIFICATION_CHANNEL_NAME = "DueTime";
    private static final String NOTIFICATION_CHANNEL_ID = "de.hosenhasser.duetime.channel.RUNNING";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Running DueTime";
    private static final int NOTIFICATION_RUNNING_ID = 1;

    public final static String TOGGLED_KEY = "startStopToggled";
    public final static String CURRENT_START_TIME_KEY = "startStopCurrentStartTime";

    private NotificationCompat.Builder notificationBuilder;

    public ContractionRunningService() {
        super("ContractionRunningService");
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
//            NotificationManagerC notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void startActionStart(Context context) {
        Intent intent = new Intent(context, ContractionRunningService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void startActionStop(Context context) {
        Intent intent = new Intent(context, ContractionRunningService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }

    public static void startActionDeleteAll(Context context) {
        Intent intent = new Intent(context, ContractionRunningService.class);
        intent.setAction(ACTION_DELETE_ALL);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                handleActionStart();
            } else if (ACTION_STOP.equals(action)) {
                handleActionStop();
            } else if (ACTION_DELETE_ALL.equals(action)) {
                handleActionDeleteAll();
            } else if (ACTION_UPDATE_NOTIFICATION.equals(action)) {
                handleUpdateNotification();
            }
        }
    }

    private void handleToggle(boolean toggled) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        createNotificationChannel();
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
        if (toggled) {
            editor.putBoolean(TOGGLED_KEY, true);
            editor.putLong(CURRENT_START_TIME_KEY, new Date().getTime());

            Intent stopIntent = new Intent(this, NotificationReceiver.class);
            stopIntent.setAction(ACTION_STOP);
            PendingIntent stopPendingIntent =
                    PendingIntent.getBroadcast(this, 0, stopIntent, 0);


            notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notificationBuilder.setSmallIcon(R.drawable.ic_graphic_eq_black_24dp)
                    .setContentTitle(this.getString(R.string.notification_running_title))
                    .setContentText(String.format(this.getString(R.string.notification_running_text), ""))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOnlyAlertOnce(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .addAction(R.drawable.ic_stop_black_24dp, getString(R.string.stop), stopPendingIntent)
                    .setAutoCancel(true);
            mNotificationManager.notify(NOTIFICATION_RUNNING_ID, notificationBuilder.build());
        } else {
            boolean inner_toggled = sharedPreferences.getBoolean(TOGGLED_KEY, false);
            if (inner_toggled) {
                long currentStartTimeLong = sharedPreferences.getLong(CURRENT_START_TIME_KEY, -1);
                if (currentStartTimeLong > 0) {
                    final Date currentStartTime = new Date();
                    currentStartTime.setTime(currentStartTimeLong);
                    final Date currentEndTime = new Date();
                    ContractionDao contractionDao = ContractionDatabase.getInstance(
                            this).contractionDao();
                    final Contraction lastContraction = contractionDao.selectNewest();
                    long interval = -1;
                    if(lastContraction != null) {
                        interval = (currentStartTime.getTime() - lastContraction.end.getTime()) / 1000;
                    }
                    Contraction newContraction = new Contraction();
                    newContraction.end = currentEndTime;
                    newContraction.start = currentStartTime;
                    newContraction.interval = interval;
                    contractionDao.insert(newContraction);
                    this.getContentResolver().notifyChange(ContractionsContentProvider.URI_CONTRACTIONS, null);
                }
                editor.putBoolean(TOGGLED_KEY, false);
                editor.putLong(CURRENT_START_TIME_KEY, -1);
                mNotificationManager.cancel(NOTIFICATION_RUNNING_ID);
            }
        }
        editor.commit();
    }

    private void handleActionStart() {
        handleToggle(true);
    }

    private void handleActionStop() {
        handleToggle(false);
    }

    private void handleActionDeleteAll() {
        ContractionDao contractionDao = ContractionDatabase.getInstance(this).contractionDao();
        contractionDao.deleteAll();
        this.getContentResolver().notifyChange(ContractionsContentProvider.URI_CONTRACTIONS, null);
    }

    private void handleUpdateNotification() {
        if (notificationBuilder != null) {

        }
    }
}