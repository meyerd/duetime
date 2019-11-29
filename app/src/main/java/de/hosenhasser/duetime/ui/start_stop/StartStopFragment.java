package de.hosenhasser.duetime.ui.start_stop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import de.hosenhasser.duetime.R;

public class StartStopFragment extends Fragment {

    private StartStopViewModel startStopViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        startStopViewModel =
                ViewModelProviders.of(this).get(StartStopViewModel.class);
        View root = inflater.inflate(R.layout.fragment_start_stop, container, false);
        final TextView textView = root.findViewById(R.id.text_start_stop);
        startStopViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}