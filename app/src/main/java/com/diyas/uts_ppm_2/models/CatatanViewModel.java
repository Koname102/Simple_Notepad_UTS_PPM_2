package com.diyas.uts_ppm_2.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.diyas.uts_ppm_2.database.CatatanRepository;

import java.util.List;

public class CatatanViewModel extends AndroidViewModel {

    private final CatatanRepository repository;
    private final LiveData<List<Catatan>> allCatatan;

    public CatatanViewModel(Application application) {
        super(application);
        repository = new CatatanRepository(application);
        allCatatan = repository.getAllCatatan();
    }

    /**
     * Mendapatkan semua catatan dari repository.
     *
     * @return LiveData berisi daftar catatan.
     */
    public LiveData<List<Catatan>> getAllCatatan() {
        return allCatatan;
    }

    /**
     * Menambahkan catatan baru.
     *
     * @param catatan Catatan yang akan ditambahkan.
     */
    public void insert(Catatan catatan) {
        repository.insert(catatan);
    }

    /**
     * Memperbarui satu catatan.
     *
     * @param catatan Catatan yang akan diperbarui.
     */
    public void update(Catatan catatan) {
        repository.update(catatan);
    }

    /**
     * Memperbarui daftar catatan.
     *
     * @param catatanList Daftar catatan yang akan diperbarui.
     */
    public void updateCatatan(List<Catatan> catatanList) {
        repository.updateMultiple(catatanList);
    }

    /**
     * Menghapus satu catatan.
     *
     * @param catatan Catatan yang akan dihapus.
     */
    public void delete(Catatan catatan) {
        repository.delete(catatan);
    }

    /**
     * Menghapus daftar catatan.
     *
     * @param catatanList Daftar catatan yang akan dihapus.
     */
    public void deleteCatatan(List<Catatan> catatanList) {
        repository.deleteMultiple(catatanList);
    }
}
