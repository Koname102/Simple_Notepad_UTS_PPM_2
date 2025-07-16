package com.diyas.uts_ppm_2.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.diyas.uts_ppm_2.models.Catatan;

import java.util.List;

@Dao
public interface CatatanDao {

    @Insert
    void insert(Catatan catatan);

    @Update
    void update(Catatan catatan);

    @Delete
    void delete(Catatan catatan);

    /**
     * Menghapus beberapa catatan berdasarkan daftar ID.
     *
     * @param ids Daftar ID catatan yang akan dihapus.
     */
    @Query("DELETE FROM catatan WHERE id IN (:ids)")
    void deleteMultiple(List<Integer> ids);

    @Query("SELECT * FROM catatan ORDER BY tanggal DESC")
    LiveData<List<Catatan>> getAllCatatan();

    @Query("SELECT * FROM catatan WHERE judul LIKE '%' || :query || '%' OR isi LIKE '%' || :query || '%' ORDER BY tanggal DESC")
    LiveData<List<Catatan>> searchCatatan(String query);

    @Query("SELECT DISTINCT tanggal FROM catatan ORDER BY tanggal DESC")
    LiveData<List<String>> getDistinctDates();
}
