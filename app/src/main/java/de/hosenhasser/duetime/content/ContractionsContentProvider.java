package de.hosenhasser.duetime.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.Room;

import java.util.Date;
import java.util.List;

public class ContractionsContentProvider extends ContentProvider {

    private static final String TAG = "CCProvider";
    public static final String AUTHORITY =
            "de.hosenhasser.duetime.content.ContractionsContentProvider";

    public static final Uri URI_CONTRACTIONS = Uri.parse(
            "content://" + AUTHORITY + "/" + Contraction.TABLE_NAME);

    private static final int CODE_CONTRACTION_DIR = 1;
    private static final int CODE_CONTRACTION_ITEM = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, Contraction.TABLE_NAME, CODE_CONTRACTION_DIR);
        uriMatcher.addURI(AUTHORITY, Contraction.TABLE_NAME + "/*", CODE_CONTRACTION_ITEM);
    }


    public ContractionsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CODE_CONTRACTION_DIR:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case CODE_CONTRACTION_ITEM:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final int count = ContractionDatabase.getInstance(context).contractionDao()
                        .deleteById(ContentUris.parseId(uri));
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CODE_CONTRACTION_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + Contraction.TABLE_NAME;
            case CODE_CONTRACTION_ITEM:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + Contraction.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case CODE_CONTRACTION_DIR:
                final Context context = getContext();
                if (context == null) {
                    return null;
                }
                final long id = ContractionDatabase.getInstance(context).contractionDao()
                        .insert(Contraction.fromContentValues(values));
                context.getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            case CODE_CONTRACTION_ITEM:
                throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final int code = uriMatcher.match(uri);
        if (code == CODE_CONTRACTION_DIR || code == CODE_CONTRACTION_ITEM) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }
            ContractionDao contractionDao = ContractionDatabase.getInstance(context)
                    .contractionDao();
            final Cursor cursor;
            if (code == CODE_CONTRACTION_DIR) {
                cursor = contractionDao.selectAll();
            } else {
                long ids[] = {ContentUris.parseId(uri)};
                cursor = contractionDao.selectAllByIds(ids);
            }
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CODE_CONTRACTION_DIR:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case CODE_CONTRACTION_ITEM:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final Contraction contraction = Contraction.fromContentValues(values);
                contraction.id = ContentUris.parseId(uri);
                final int count = ContractionDatabase.getInstance(context).contractionDao()
                        .update(contraction);
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
