package de.hosenhasser.duetime.ui.contraction_list;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.MatrixCursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hosenhasser.duetime.R;
import de.hosenhasser.duetime.content.Contraction;
import de.hosenhasser.duetime.content.ContractionDao;
import de.hosenhasser.duetime.content.ContractionDatabase;
import de.hosenhasser.duetime.content.ContractionsContentProvider;
import de.hosenhasser.duetime.content.Converters;

public class ContractionListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG = "CLFragment";

    private static final int LOADER_CONTRACTIONS = 1;
    private ContractionListCursorRecyclerViewAdapter mContractionsAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout mMainCoordinatorLayout;

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
            mRecyclerView = (RecyclerView) view;
            mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mContractionsAdapter = new ContractionListCursorRecyclerViewAdapter(context, null);
            mRecyclerView.setAdapter(mContractionsAdapter);

            LoaderManager.getInstance(this).initLoader(LOADER_CONTRACTIONS, null, this);

            ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                    new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
////             throw new RuntimeException(context.toString()
////                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ContractionListViewHolder) {
            final Contraction c = mContractionsAdapter.getItem(viewHolder.getAdapterPosition());
            mContractionsAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(mRecyclerView, String.format(Locale.getDefault(),"%d", c.id) +
                            " " + getString(R.string.removed), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mContractionsAdapter.restoreItem(c);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Contraction item);
//        {
//            Log.i(TAG, "onListFragmentInteraction: " + item.toString());
//        }
    }

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
//                MatrixCursor mx = new MatrixCursor(data.getColumnNames());
//
//                Cursor cursor = ((ContractionListCursorRecyclerViewAdapter) mRecyclerView.getAdapter()).getCursor();
//                if(cursor != null)
//                    // fill all exisitng in adapter
//                    fillMx(cursor, mx);
//
//                // fill with additional result
//                fillMx(data, mx);

                ((ContractionListCursorRecyclerViewAdapter) mRecyclerView.getAdapter()).swapCursor(data);
                break;
        }
    }

    private void fillMx(Cursor data, MatrixCursor mx) {
        if (data == null)
            return;

        data.moveToPosition(-1);
        while (data.moveToNext()) {
            mx.addRow(Contraction.fromCursorValues(data).toObjectArray());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        switch (loader.getId()) {
//            case LOADER_CONTRACTIONS:
//                mContractionsAdapter.setContractions(null);
//                break;
//        }
    }

    private abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

        protected Context mContext;

        private Cursor mCursor;

        private boolean mDataValid;

        private int mRowIdColumn;

        private DataSetObserver mDataSetObserver;

        public CursorRecyclerViewAdapter(Context context, Cursor cursor) {
            mContext = context;
            mCursor = cursor;
            mDataValid = cursor != null;
            mRowIdColumn = mDataValid ? mCursor.getColumnIndexOrThrow(Contraction.COLUMN_ID) : -1;
            mDataSetObserver = new NotifyingDataSetObserver(this);
            if (mCursor != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
        }

        public Cursor getCursor() {
            return mCursor;
        }

        @Override
        public int getItemCount() {
            if (mDataValid && mCursor != null) {
                return mCursor.getCount();
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
                return mCursor.getInt(mRowIdColumn);
            }
            return 0;
        }

        @Override
        public void setHasStableIds(boolean hasStableIds) {
            super.setHasStableIds(true);
        }

        public final String TAG = CursorRecyclerViewAdapter.class.getSimpleName();

        public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(VH viewHolder, int position) {
            if (!mDataValid) {
                throw new IllegalStateException("this should only be called when the cursor is valid");
            }
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("couldn't move cursor to position " + position);
            }
            onBindViewHolder(viewHolder, mCursor);
        }

        /**
         * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
         * closed.
         */
        public void changeCursor(Cursor cursor) {
            Cursor old = swapCursor(cursor);
            if (old != null) {
                old.close();
            }
        }

        /**
         * Swap in a new Cursor, returning the old Cursor.  Unlike
         * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
         * closed.
         */
        public Cursor swapCursor(Cursor newCursor) {
            if (newCursor == mCursor) {
                return null;
            }
            final Cursor oldCursor = mCursor;
            if (oldCursor != null && mDataSetObserver != null) {
                oldCursor.unregisterDataSetObserver(mDataSetObserver);
            }
            mCursor = newCursor;
            if (mCursor != null) {
                if (mDataSetObserver != null) {
                    mCursor.registerDataSetObserver(mDataSetObserver);
                }
                mRowIdColumn = newCursor.getColumnIndexOrThrow(Contraction.COLUMN_ID);
                mDataValid = true;
                notifyDataSetChanged();
            } else {
                mRowIdColumn = -1;
                mDataValid = false;
                notifyDataSetChanged();
                //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
            }
            return oldCursor;
        }

        public void setDataValid(boolean mDataValid) {
            this.mDataValid = mDataValid;
        }

        private class NotifyingDataSetObserver extends DataSetObserver {
            private RecyclerView.Adapter adapter;

            public NotifyingDataSetObserver(RecyclerView.Adapter adapter) {
                this.adapter = adapter;
            }

            @Override
            public void onChanged() {
                super.onChanged();
                ((CursorRecyclerViewAdapter) adapter).setDataValid(true);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                ((CursorRecyclerViewAdapter) adapter).setDataValid(false);
            }
        }
    }

    public class ContractionListViewHolder extends RecyclerView.ViewHolder {
        public TextView idView;
        public TextView idInterval;
        public TextView idStart;
        public TextView idEnd;
        public TextView idDuration;
        public RelativeLayout viewBackground, viewForeground;

        private final SimpleDateFormat start_end_format = new SimpleDateFormat("H:m:s",
                Locale.getDefault());

        public ContractionListViewHolder(View itemView) {
            super(itemView);
            idView = (TextView) itemView.findViewById(R.id.item_number);
            idInterval = (TextView) itemView.findViewById(R.id.item_interval);
            idStart = (TextView) itemView.findViewById(R.id.item_start);
            idEnd = (TextView) itemView.findViewById(R.id.item_end);
            idDuration = (TextView) itemView.findViewById(R.id.item_duration);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }

        public void setData(Cursor c) {
            Contraction con = Contraction.fromCursorValues(c);
            idView.setText(String.format(Locale.getDefault(), "%d", con.id));
            final long interval = con.interval;
            final long interval_minutes = interval / 60;
            final long interval_seconds = interval - interval_minutes * 60;
            idInterval.setText(String.format(Locale.getDefault(), "%d:%d (%ds)",
                    interval_minutes, interval_seconds,interval));
            final Date start = con.start;
            final Date end = con.end;
            idStart.setText(start_end_format.format(start));
            idEnd.setText(start_end_format.format(end));
            final long diffInMillies = Math.abs(end.getTime() - start.getTime());
            final long diff_seconds_abs = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            final long diff_minutes = diff_seconds_abs / 60;
            final long diff_seconds = diff_seconds_abs - diff_minutes * 60;
            idDuration.setText(String.format(Locale.getDefault(), "%d:%d (%ds)",
                    diff_minutes, diff_seconds, diff_seconds_abs));
        }
    }

    public class ContractionListCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter {

        public ContractionListCursorRecyclerViewAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_contraction, parent, false);
            return new ContractionListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
            ContractionListViewHolder holder = (ContractionListViewHolder) viewHolder;
            cursor.moveToPosition(cursor.getPosition());
            holder.setData(cursor);
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        public Contraction getItem(int position) {
            Cursor c = getCursor();
            int oldposition = c.getPosition();
            c.moveToPosition(position);
            Contraction cont = Contraction.fromCursorValues(c);
            c.moveToPosition(oldposition);
            return cont;
        }

        private class DeleteContractionAsyncTask extends AsyncTask<Long, Integer, Integer> {
            private WeakReference<Context> weakContext;
            private long idToDelete;

            public DeleteContractionAsyncTask(Context context, Long idToDelete) {
                this.weakContext = new WeakReference<>(context);
                this.idToDelete = idToDelete;
            }

            @Override
            protected Integer doInBackground(Long... params) {
                Context context = weakContext.get();
                if(context == null) {
                    return 0;
                }

                ContractionDao contractionDao = ContractionDatabase.getInstance(
                        context).contractionDao();
                contractionDao.deleteById(idToDelete);
                context.getContentResolver().notifyChange(ContractionsContentProvider.URI_CONTRACTIONS, null);
                return 0;
            }

            @Override
            protected void onPostExecute(Integer agentsCount) {
                Context context = weakContext.get();
                if(context == null) {
                    return;
                }
            }
        }

        private class InsertContractionAsyncTask extends AsyncTask<Contraction, Integer, Integer> {
            private WeakReference<Context> weakContext;
            private Contraction c;

            public InsertContractionAsyncTask(Context context, Contraction c) {
                this.weakContext = new WeakReference<>(context);
                this.c = c;
            }

            @Override
            protected Integer doInBackground(Contraction... params) {
                Context context = weakContext.get();
                if(context == null) {
                    return 0;
                }

                ContractionDao contractionDao = ContractionDatabase.getInstance(
                        context).contractionDao();
                contractionDao.insert(c);
                context.getContentResolver().notifyChange(ContractionsContentProvider.URI_CONTRACTIONS, null);
                return 0;
            }

            @Override
            protected void onPostExecute(Integer agentsCount) {
                Context context = weakContext.get();
                if(context == null) {
                    return;
                }
            }
        }

        public void removeItem(int position) {
            long idToDelete = getItemId(position);
            new DeleteContractionAsyncTask(getContext(), idToDelete).execute();
        }

        public void restoreItem(Contraction c) {
            new InsertContractionAsyncTask(getContext(), c).execute();
        }
    }
}
