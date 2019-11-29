package de.hosenhasser.duetime.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hosenhasser.duetime.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private RecyclerView contractionsListView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

//        contractionsListView = root.findViewById(R.id.home_contractions_list);
//        contractionsListView.setHasFixedSize(true);
//
//        mAdapter = new ContractionRecyclerViewAdapter(null);
//        contractionsListView.setAdapter(mAdapter);



        return root;
    }
}