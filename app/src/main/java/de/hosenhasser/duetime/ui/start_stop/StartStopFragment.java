package de.hosenhasser.duetime.ui.start_stop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.Date;

import de.hosenhasser.duetime.R;
import de.hosenhasser.duetime.content.Contraction;
import de.hosenhasser.duetime.content.ContractionDao;
import de.hosenhasser.duetime.content.ContractionDatabase;

public class StartStopFragment extends Fragment {

    private StartStopViewModel startStopViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        startStopViewModel =
                ViewModelProviders.of(this).get(StartStopViewModel.class);
        View root = inflater.inflate(R.layout.fragment_start_stop, container, false);


        final ToggleButton startStopButton = root.findViewById(R.id.start_stop_button);

        startStopButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                startStopViewModel.triggerToggle(b);
            }
        });

        startStopViewModel.getToggled().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean toggled) {
                startStopButton.setChecked(toggled);
            }
        });

        startStopViewModel.getCaption().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                startStopButton.setText(s);
            }
        });

        return root;
    }
}