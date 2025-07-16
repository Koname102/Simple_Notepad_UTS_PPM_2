package com.diyas.uts_ppm_2.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.diyas.uts_ppm_2.R;
import com.diyas.uts_ppm_2.database.CatatanRepository;
import com.diyas.uts_ppm_2.fragments.CalendarFragment;
import com.diyas.uts_ppm_2.fragments.CustomSearchFragment;
import com.diyas.uts_ppm_2.fragments.NotepadFragment;
import com.diyas.uts_ppm_2.models.Catatan;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int currentFragment = -1;
    private ImageView iconNavigation;
    private CatatanRepository repository;
    private String selectedDate;
    private boolean isInSelectionMode = false;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi repository
        repository = new CatatanRepository(getApplication());

        // Inisialisasi ikon navigasi
        iconNavigation = findViewById(R.id.icon_navigation);

        // Inisialisasi BottomNavigationView
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Fragment awal diatur ke CustomSearchFragment
        loadFragment(new CustomSearchFragment(), false);

        // Tambahkan listener untuk ikon navigasi
        iconNavigation.setOnClickListener(v -> {
            if (currentFragment == R.layout.fragment_custom_search || currentFragment == R.layout.fragment_calendar) {
                loadFragment(new NotepadFragment(), true);
            } else if (currentFragment == R.layout.fragment_notepad) {
                saveNote();
            }
        });

        // Atur listener untuk BottomNavigationView
        setupBottomNavigationView();

        // Listener untuk back stack perubahan
        getSupportFragmentManager().addOnBackStackChangedListener(this::updateIconForCurrentFragment);
    }

    private void setupBottomNavigationView() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Tangani navigasi utama
            if (!isInSelectionMode) {
                if (itemId == R.id.navigation_search) {
                    loadFragment(new CustomSearchFragment(), true); // Tambahkan ke back stack
                    return true;
                } else if (itemId == R.id.navigation_calendar) {
                    loadFragment(new CalendarFragment(), true); // Tambahkan ke back stack
                    return true;
                }
            }

            // Tangani menu seleksi jika dalam mode seleksi
            if (isInSelectionMode) {
                if (itemId == R.id.navigation_delete) {
                    handleDeleteAction();
                    return true;
                } else if (itemId == R.id.navigation_cancel) {
                    handleCancelAction();
                    return true;
                }
            }

            return false;
        });
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_view, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null); // Tambahkan ke back stack
        }

        transaction.commit();

        // Perbarui ikon berdasarkan fragment
        updateIconForFragment(fragment);
    }

    private void updateIconForCurrentFragment() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.container_view);
        if (current != null) {
            updateIconForFragment(current);
        }
    }

    private void updateIconForFragment(Fragment fragment) {
        if (fragment instanceof CustomSearchFragment || fragment instanceof CalendarFragment) {
            currentFragment = fragment instanceof CustomSearchFragment
                    ? R.layout.fragment_custom_search
                    : R.layout.fragment_calendar;
            iconNavigation.setImageResource(R.drawable.journal_alt);
        } else if (fragment instanceof NotepadFragment) {
            currentFragment = R.layout.fragment_notepad;
            iconNavigation.setImageResource(R.drawable.ic_save);
        }
    }

    private void saveNote() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.container_view);
        if (current instanceof NotepadFragment) {
            NotepadFragment notepadFragment = (NotepadFragment) current;
            String title = notepadFragment.getTitle();
            String content = notepadFragment.getContent();
            String dateToSave = notepadFragment.getDate(); // Ambil tanggal dari NotepadFragment

            if (dateToSave == null || dateToSave.isEmpty()) {
                dateToSave = getCurrentDate(); // Fallback ke tanggal hari ini jika tidak ada tanggal
            }

            if (title.isEmpty() && content.isEmpty()) {
                Toast.makeText(this, "Tidak ada yang perlu disimpan", Toast.LENGTH_SHORT).show();
            } else {
                Catatan newNote = new Catatan();
                newNote.setJudul(title.isEmpty() ? "Untitled" : title);
                newNote.setIsi(content.isEmpty() ? "No content" : content);
                newNote.setTanggal(dateToSave);

                repository.insert(newNote);

                Toast.makeText(this, "Catatan berhasil disimpan", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().popBackStack();
            }
        }
    }


    public void setSelectedDate(String date) {
        this.selectedDate = date;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void enterSelectionMode() {
        isInSelectionMode = true;
        bottomNavigation.getMenu().findItem(R.id.navigation_search).setVisible(false);
        bottomNavigation.getMenu().findItem(R.id.navigation_calendar).setVisible(false);
        bottomNavigation.getMenu().add(0, R.id.navigation_delete, 0, "Delete").setIcon(R.drawable.ic_delete);
        bottomNavigation.getMenu().add(0, R.id.navigation_cancel, 1, "Cancel").setIcon(R.drawable.ic_cancel);
    }

    public void exitSelectionMode() {
        isInSelectionMode = false;
        bottomNavigation.getMenu().clear();
        bottomNavigation.inflateMenu(R.menu.bottom_navigation_menu);
        setupBottomNavigationView();
    }

    private void handleDeleteAction() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.container_view);
        if (current instanceof CustomSearchFragment) {
            ((CustomSearchFragment) current).handleDeleteAction();
        }
        exitSelectionMode();
    }

    private void handleCancelAction() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.container_view);
        if (current instanceof CustomSearchFragment) {
            ((CustomSearchFragment) current).handleCancelAction();
        }
        exitSelectionMode();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(); // Tangani navigasi back
        } else {
            super.onBackPressed(); // Keluar dari aplikasi jika back stack kosong
        }
    }
}
