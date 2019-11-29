package de.hosenhasser.duetime.ui.home;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hosenhasser.duetime.R;
import de.hosenhasser.duetime.content.Contraction;
import de.hosenhasser.duetime.content.ContractionDatabase;
import de.hosenhasser.duetime.content.ContractionsContentProvider;

public class ContractionListFragment extends Fragment {

    private static final String TAG = "CLFragment";

    private static final int LOADER_CONTRACTIONS = 1;
    private OnListFragmentInteractionListener mListener;
    private ContractionRecyclerViewAdapter mContractionsAdapter;

    public ContractionListFragment() {
    }

    public static ContractionListFragment newInstance() {
        ContractionListFragment fragment = new ContractionListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contraction_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mContractionsAdapter = new ContractionRecyclerViewAdapter(mListener);
            recyclerView.setAdapter(mContractionsAdapter);

            LoaderManager.getInstance(this).initLoader(LOADER_CONTRACTIONS, null, mLoaderCallbacks);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Contraction item);
//        {
//            Log.i(TAG, "onListFragmentInteraction: " + item.toString());
//        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {

                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    switch (id) {
                        case LOADER_CONTRACTIONS:
                            return new CursorLoader(getContext(),
                                    ContractionsContentProvider.URI_CONTRACTIONS,
                                    new String[]{Contraction.COLUMN_ID},
                                    null, null, null);
                        default:
                            throw new IllegalArgumentException();
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    switch (loader.getId()) {
                        case LOADER_CONTRACTIONS:
                            mContractionsAdapter.setContractions(data);
                            break;
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    switch (loader.getId()) {
                        case LOADER_CONTRACTIONS:
                            mContractionsAdapter.setContractions(null);
                            break;
                    }
                }
            };

    private static class ContractionRecyclerViewAdapter extends RecyclerView.Adapter<ContractionRecyclerViewAdapter.ViewHolder> {

        private Cursor mCursor;
        private final OnListFragmentInteractionListener mListener;

        public ContractionRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
            mListener = listener;
        }

        @Override
        public ContractionRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_contraction, parent, false);
            return new ContractionRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ContractionRecyclerViewAdapter.ViewHolder holder, int position) {
            if (mCursor.moveToPosition(position)) {
                holder.itemId = mCursor.getInt(mCursor.getColumnIndexOrThrow(Contraction.COLUMN_ID));
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.
                            mListener.onListFragmentInteraction(holder.mItem);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mCursor == null ? 0 : mCursor.getCount();
        }

        void setContractions(Cursor cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public int itemId;
            private Contraction mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mItem = ContractionDatabase.getInstance(view.getContext()).contractionDao().findById(itemId);

                mIdView = (TextView) view.findViewById(R.id.item_number);
                mContentView = (TextView) view.findViewById(R.id.content);

                mIdView.setText(String.format("%l", mItem.id));
                mContentView.setText(String.format("%l", mItem.id));
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

}
