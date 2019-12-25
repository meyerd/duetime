package de.hosenhasser.duetime.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "contractions")
public class Contraction {
    public static final String TABLE_NAME = "contractions";
    public static final String COLUMN_ID = BaseColumns._ID;

    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";
    public static final String COLUMN_INTERVAL = "interval";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = COLUMN_ID)
    public long id;

    @ColumnInfo(name = COLUMN_INTERVAL)
    public long interval;

    @ColumnInfo(name = COLUMN_START)
    public Date start;

    @ColumnInfo(name = COLUMN_END)
    public Date end;

    public static Contraction fromContentValues(ContentValues values) {
        final Contraction contraction = new Contraction();
        if (values.containsKey(COLUMN_ID)) {
            contraction.id = values.getAsLong(COLUMN_ID);
        }
        if (values.containsKey(COLUMN_START)) {
            contraction.start = Converters.fromTimestamp(values.getAsLong(COLUMN_START));
        }
        if (values.containsKey(COLUMN_END)) {
            contraction.end = Converters.fromTimestamp(values.getAsLong(COLUMN_END));
        }
        return contraction;
    }

    public static Contraction fromCursorValues(Cursor data) {
        final Contraction contraction = new Contraction();
        contraction.id = data.getLong(data.getColumnIndexOrThrow(COLUMN_ID));
        contraction.interval = data.getLong(data.getColumnIndexOrThrow(COLUMN_INTERVAL));
        String starts = data.getString(data.getColumnIndexOrThrow(COLUMN_START));
        long start = data.getLong(data.getColumnIndexOrThrow(COLUMN_START));
        contraction.start = Converters.fromTimestamp(data.getLong(data.getColumnIndexOrThrow(COLUMN_START)));
        contraction.end = Converters.fromTimestamp(data.getLong(data.getColumnIndexOrThrow(COLUMN_END)));
        return contraction;
    }

    public Object[] toObjectArray() {
        return new Object[]{
                this.id, this.interval, this.start, this.end
        };
    }
}
