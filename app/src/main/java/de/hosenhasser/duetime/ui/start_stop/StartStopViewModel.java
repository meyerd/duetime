package de.hosenhasser.duetime.ui.start_stop;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hosenhasser.duetime.ContractionRunningService;
import de.hosenhasser.duetime.R;
import de.hosenhasser.duetime.content.Contraction;
import de.hosenhasser.duetime.content.ContractionDao;
import de.hosenhasser.duetime.content.ContractionDatabase;

public class StartStopViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> mToggled;
    private MutableLiveData<String> mCaption;
    private final SharedPreferences sharedPreferences;

    public final static String TOGGLED_KEY = "startStopToggled";
    public final static String CURRENT_START_TIME_KEY = "startStopCurrentStartTime";

    public StartStopViewModel(Application application) {
        super(application);

        final Context context = getApplication().getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        mToggled = new MutableLiveData<>();
        mToggled.setValue(sharedPreferences.getBoolean(TOGGLED_KEY, false));

        mCaption = new MutableLiveData<>();
        mCaption.setValue(context.getString(R.string.start));

        final Handler h = new Handler();
        final int delay = 1000;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean inner_toggled = sharedPreferences.getBoolean(TOGGLED_KEY, false);
                if (inner_toggled) {
                    long currentStartTimeLong = sharedPreferences.getLong(CURRENT_START_TIME_KEY, -1);
                    if (currentStartTimeLong > 0) {
                        final Date currentStartTime = new Date();
                        currentStartTime.setTime(currentStartTimeLong);
                        final Date currentEndTime = new Date();
                        long interval = (currentEndTime.getTime() - currentStartTime.getTime()) / 1000;
                        long interval_minutes = interval / 60;
                        long interval_seconds = interval - interval_minutes * 60;

                        mCaption.setValue(String.format(Locale.getDefault(), "%d:%d (%ds)", interval_minutes, interval_seconds, interval));
                    }
                } else {
                    mCaption.setValue(context.getString(R.string.start));
                }
                h.postDelayed(this, delay);
            }
        }, delay);
    }

    public void triggerToggle(boolean toggled) {
        if(toggled) {
            ContractionRunningService.startActionStart(getApplication().getApplicationContext());
            mToggled.setValue(true);
        } else {
            ContractionRunningService.startActionStop(getApplication().getApplicationContext());
            mToggled.setValue(false);
        }
    }

    public LiveData<Boolean> getToggled() {
        return mToggled;
    }

    public LiveData<String> getCaption() { return mCaption; }
}