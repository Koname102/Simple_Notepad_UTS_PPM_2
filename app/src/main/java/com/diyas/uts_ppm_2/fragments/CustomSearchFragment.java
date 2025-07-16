package com.diyas.uts_ppm_2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diyas.uts_ppm_2.R;
import com.diyas.uts_ppm_2.activities.MainActivity;
import com.diyas.uts_ppm_2.adapters.CatatanAdapter;
import com.diyas.uts_ppm_2.models.Catatan;
import com.diyas.uts_ppm_2.models.CatatanViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomSearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private CatatanAdapter adapter;
    private CatatanViewModel viewModel;
    private BottomNavigationView bottomNavigationView;

    private boolean isSelectionMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_search, container, false);

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inisialisasi ViewModel
        viewModel = new ViewModelProvider(this).get(CatatanViewModel.class);

        // Inisialisasi BottomNavigationView
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);

        // Observasi data dari ViewModel
        viewModel.getAllCatatan().observe(getViewLifecycleOwner(), this::updateUI);

        setupBottomNavigationListener();

        return view;
    }

    /**
     * Perbarui RecyclerView dan tangani mode seleksi.
     *
     * @param catatanList List catatan yang diperoleh dari ViewModel.
     */
    private void updateUI(List<Catatan> catatanList) {
        if (adapter == null) {
            adapter = new CatatanAdapter(catatanList);
            recyclerView.setAdapter(adapter);

            // Perbarui tampilan menu berdasarkan mode seleksi
            adapter.setOnSelectionChangeListener(isSelectionMode -> {
                this.isSelectionMode = isSelectionMode;
                updateBottomNavigationMenu();
            });

            // Tangani klik item untuk navigasi ke NotepadFragment
            adapter.setOnItemClickListener(catatan -> {
                if (!isSelectionMode) {
                    navigateToNotepadFragment(catatan);
                }
            });
        } else {
            adapter.updateData(catatanList);
        }
    }

    /**
     * Navigasi ke NotepadFragment untuk mengedit catatan.
     *
     * @param catatan Catatan yang dipilih.
     */
    private void navigateToNotepadFragment(Catatan catatan) {
        NotepadFragment fragment = NotepadFragment.newInstance(
                catatan.getTanggal(),
                catatan.getJudul(),
                catatan.getIsi()
        );

        fragment.setOnNoteUpdateListener((updatedDate, updatedTitle, updatedContent) -> {
            handleNoteUpdate(catatan, updatedDate, updatedTitle, updatedContent);
        });

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.loadFragment(fragment, true);
        }
    }

    /**
     * Tangani pembaruan catatan setelah diedit di NotepadFragment.
     *
     * @param originalCatatan Catatan asli sebelum diedit.
     * @param updatedDate     Tanggal yang diperbarui.
     * @param updatedTitle    Judul yang diperbarui.
     * @param updatedContent  Konten yang diperbarui.
     */
    private void handleNoteUpdate(Catatan originalCatatan, String updatedDate, String updatedTitle, String updatedContent) {
        boolean isChanged = false;

        // Perbarui hanya jika ada perubahan
        if (originalCatatan != null) {
            if (!originalCatatan.getJudul().equals(updatedTitle)) {
                originalCatatan.setJudul(updatedTitle);
                isChanged = true;
            }
            if (!originalCatatan.getIsi().equals(updatedContent)) {
                originalCatatan.setIsi(updatedContent);
                isChanged = true;
            }

            // Ubah tanggal hanya jika ada perubahan
            if (isChanged) {
                originalCatatan.setTanggal(getCurrentDate());
            }

            viewModel.update(originalCatatan);
        }
    }

    /**
     * Mendapatkan tanggal hari ini dalam format "yyyy-MM-dd".
     *
     * @return String tanggal hari ini.
     */
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Perbarui tampilan menu berdasarkan mode seleksi.
     */
    private void updateBottomNavigationMenu() {
        bottomNavigationView.getMenu().findItem(R.id.navigation_delete).setVisible(isSelectionMode);
        bottomNavigationView.getMenu().findItem(R.id.navigation_cancel).setVisible(isSelectionMode);
        bottomNavigationView.getMenu().findItem(R.id.navigation_search).setVisible(!isSelectionMode);
        bottomNavigationView.getMenu().findItem(R.id.navigation_calendar).setVisible(!isSelectionMode);
    }

    /**
     * Atur listener untuk BottomNavigationView.
     */
    private void setupBottomNavigationListener() {
        bottomNavigationView.setOnItemSelectedListener(this::handleBottomMenuActions);
    }

    /**
     * Tangani aksi berdasarkan item menu yang dipilih.
     *
     * @param item Item menu yang dipilih.
     * @return True jika aksi berhasil, false jika tidak.
     */
    private boolean handleBottomMenuActions(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_delete) {
            // Tangani aksi delete
            if (isSelectionMode) {
                handleDeleteAction();
            }
            return true;
        } else if (itemId == R.id.navigation_cancel) {
            // Tangani aksi cancel
            if (isSelectionMode) {
                handleCancelAction();
            }
            return true;
        } else if (itemId == R.id.navigation_search) {
            navigateToFragment(new CustomSearchFragment());
            return true;
        } else if (itemId == R.id.navigation_calendar) {
            navigateToFragment(CalendarFragment.newInstance());
            return true;
        }

        return false;
    }

    /**
     * Navigasi ke fragment tertentu.
     *
     * @param fragment Fragment tujuan.
     */
    private void navigateToFragment(Fragment fragment) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.loadFragment(fragment, true);
        }
    }

    /**
     * Tangani aksi delete pada mode seleksi.
     */
    public void handleDeleteAction() {

        if (adapter != null) {
            List<Catatan> selectedItems = adapter.getSelectedItems();
            viewModel.deleteCatatan(selectedItems);
            adapter.clearSelection();
        }
    }

    /**
     * Tangani aksi cancel pada mode seleksi.
     */
    public void handleCancelAction() {
        if (adapter != null) {
            adapter.clearSelection();
        }
    }

    /**
     * Pastikan tampilan kembali ke mode normal saat fragment dilepas.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isSelectionMode) {
            handleCancelAction();
        }
    }
}
