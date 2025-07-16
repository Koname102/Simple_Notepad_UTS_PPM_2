package com.diyas.uts_ppm_2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.diyas.uts_ppm_2.models.Catatan;

@Database(entities = {Catatan.class}, version = 1)
public abstract class CatatanDatabase extends RoomDatabase {

    private static CatatanDatabase instance;

    public abstract CatatanDao catatanDao();

    public static synchronized CatatanDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            CatatanDatabase.class, "catatan_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
