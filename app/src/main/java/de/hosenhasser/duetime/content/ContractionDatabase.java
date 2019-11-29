package de.hosenhasser.duetime.content;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Contraction.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class ContractionDatabase extends RoomDatabase {
    @SuppressWarnings("WeakerAccess")
    public abstract ContractionDao contractionDao();

    private static ContractionDatabase sInstance;

    public static synchronized ContractionDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room
                    .databaseBuilder(context.getApplicationContext(), ContractionDatabase.class, "ex")
                    .build();
        }
        return sInstance;
    }
}
