package de.hosenhasser.duetime.content;

import android.content.ContentValues;
import android.provider.BaseColumns;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Contraction {
    public static final String TABLE_NAME = "contractions";
    public static final String COLUMN_ID = BaseColumns._ID;

    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = COLUMN_ID)
    public long id;

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
}
