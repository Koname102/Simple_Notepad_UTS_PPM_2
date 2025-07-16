package com.diyas.uts_ppm_2.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.diyas.uts_ppm_2.models.Catatan;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatatanRepository {

    private final CatatanDao catatanDao;
    private final LiveData<List<Catatan>> allCatatan;
    private final ExecutorService executorService;

    public CatatanRepository(Application application) {
        CatatanDatabase database = CatatanDatabase.getInstance(application);
        catatanDao = database.catatanDao();
        allCatatan = catatanDao.getAllCatatan();
        executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * Mengambil semua catatan.
     *
     * @return LiveData berisi daftar semua catatan.
     */
    public LiveData<List<Catatan>> getAllCatatan() {
        return allCatatan;
    }

    /**
     * Menambahkan catatan baru ke database.
     *
     * @param catatan Catatan yang akan ditambahkan.
     */
    public void insert(Catatan catatan) {
        executorService.execute(() -> catatanDao.insert(catatan));
    }

    /**
     * Memperbarui satu catatan.
     *
     * @param catatan Catatan yang akan diperbarui.
     */
    public void update(Catatan catatan) {
        executorService.execute(() -> catatanDao.update(catatan));
    }

    /**
     * Memperbarui daftar catatan.
     *
     * @param catatanList Daftar catatan yang akan diperbarui.
     */
    public void updateMultiple(List<Catatan> catatanList) {
        executorService.execute(() -> {
            for (Catatan catatan : catatanList) {
                catatanDao.update(catatan); // Pastikan dao mendukung operasi update
            }
        });
    }

    /**
     * Menghapus satu catatan dari database.
     *
     * @param catatan Catatan yang akan dihapus.
     */
    public void delete(Catatan catatan) {
        executorService.execute(() -> catatanDao.delete(catatan));
    }

    /**
     * Menghapus daftar catatan dari database berdasarkan ID.
     *
     * @param catatanList Daftar catatan yang akan dihapus.
     */
    public void deleteMultiple(List<Catatan> catatanList) {
        executorService.execute(() -> {
            for (Catatan catatan : catatanList) {
                catatanDao.delete(catatan);
            }
        });
    }

    /**
     * Mencari catatan berdasarkan query tertentu.
     *
     * @param query Query pencarian.
     * @return LiveData berisi daftar catatan yang cocok dengan query.
     */
    public LiveData<List<Catatan>> searchCatatan(String query) {
        return catatanDao.searchCatatan(query);
    }
}
