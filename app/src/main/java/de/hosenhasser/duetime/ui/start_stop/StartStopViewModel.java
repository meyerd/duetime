package de.hosenhasser.duetime.ui.start_stop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StartStopViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public StartStopViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is start/stop fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}