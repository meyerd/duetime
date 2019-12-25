package de.hosenhasser.duetime.content;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface ContractionDao {
    @Query("SELECT * FROM " + Contraction.TABLE_NAME)
    List<Contraction> getAll();

    @Query("SELECT * FROM " + Contraction.TABLE_NAME + " ORDER BY " + Contraction.COLUMN_ID + " DESC")
    Cursor selectAll();

    @Query("SELECT * FROM " + Contraction.TABLE_NAME + " WHERE " + Contraction.COLUMN_ID +
            " IN (:ids)")
    List<Contraction> getAllByIds(long[] ids);

    @Query("SELECT * FROM " + Contraction.TABLE_NAME + " WHERE " + Contraction.COLUMN_ID +
            " IN (:ids)")
    Cursor selectAllByIds(long[] ids);

    @Query("SELECT * FROM " + Contraction.TABLE_NAME + " WHERE " + Contraction.COLUMN_ID +
            " = :id LIMIT 1")
    Contraction findById(int id);

    @Query("SELECT * FROM " + Contraction.TABLE_NAME + " WHERE " + Contraction.COLUMN_START +
            " >= :startDate")
    List<Contraction> getNewerThan(Date startDate);

    @Query("SELECT * FROM " + Contraction.TABLE_NAME + " ORDER by " + Contraction.COLUMN_END +
        " DESC LIMIT 1")
    Contraction selectNewest();

    @Query("SELECT COUNT(*) FROM " + Contraction.TABLE_NAME)
    int count();

    @Insert
    long[] insertAll(Contraction... contractions);

    @Insert
    long insert(Contraction contraction);

    @Update
    int update(Contraction contraction);

    @Query("DELETE FROM " + Contraction.TABLE_NAME + " WHERE " + Contraction.COLUMN_ID + " = :id")
    int deleteById(long id);

    @Delete
    int delete(Contraction contraction);

    @Query("DELETE FROM " + Contraction.TABLE_NAME)
    void deleteAll();
}
