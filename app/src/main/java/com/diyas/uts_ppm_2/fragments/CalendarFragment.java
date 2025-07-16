package com.diyas.uts_ppm_2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.diyas.uts_ppm_2.R;
import com.diyas.uts_ppm_2.activities.MainActivity;
import com.diyas.uts_ppm_2.database.CatatanRepository;
import com.diyas.uts_ppm_2.models.Catatan;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private TextView notesTextView;
    private Button addNoteButton;
    private CatatanRepository repository;
    private List<Catatan> catatanList = new ArrayList<>();
    private String selectedDate;

    /**
     * Buat instance baru dari CalendarFragment.
     *
     * @return CalendarFragment instance.
     */
    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Inisialisasi repository
        repository = new CatatanRepository(requireActivity().getApplication());

        // Inisialisasi elemen UI
        calendarView = view.findViewById(R.id.calendar_view);
        notesTextView = view.findViewById(R.id.notes_text_view);
        addNoteButton = view.findViewById(R.id.add_note_button);

        // Sembunyikan tombol "Add Note" di awal
        addNoteButton.setVisibility(View.GONE);

        // Observasi data catatan
        repository.getAllCatatan().observe(getViewLifecycleOwner(), catatan -> {
            catatanList = catatan;
            updateCalendar();
        });

        // Tangani klik pada tanggal
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = formatDate(date);
            showNotesForDate(date.getDate());
            addNoteButton.setVisibility(View.VISIBLE); // Tampilkan tombol setelah tanggal dipilih
        });

        // Tangani klik tombol tambah/edit catatan
        addNoteButton.setOnClickListener(v -> {
            if (selectedDate != null) {
                Catatan existingNote = findNoteForDate(selectedDate);
                String title = existingNote != null ? existingNote.getJudul() : "";
                String content = existingNote != null ? existingNote.getIsi() : "";
                navigateToFragment(NotepadFragment.newInstance(selectedDate, title, content));
            } else {
                notesTextView.setText("Pilih tanggal terlebih dahulu.");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Pastikan tampilan diperbarui setiap kali fragment aktif kembali
        updateCalendar();
    }

    /**
     * Perbarui kalender untuk menandai tanggal yang memiliki catatan.
     */
    private void updateCalendar() {
        if (catatanList == null || catatanList.isEmpty()) return;

        // Tandai tanggal dengan catatan
        List<CalendarDay> eventDates = new ArrayList<>();
        for (Catatan catatan : catatanList) {
            try {
                if (catatan.getTanggal() != null) {
                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(catatan.getTanggal());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    eventDates.add(CalendarDay.from(calendar));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Tambahkan dekorasi ke kalender
        calendarView.addDecorator(new EventDecorator(getResources().getColor(R.color.primary), eventDates));
    }

    /**
     * Tampilkan catatan untuk tanggal yang dipilih.
     *
     * @param date Tanggal yang dipilih.
     */
    private void showNotesForDate(Date date) {
        if (catatanList == null || catatanList.isEmpty()) {
            notesTextView.setText("Tidak ada catatan untuk tanggal ini.");
            return;
        }

        String formattedDate = formatDate(CalendarDay.from(date));
        StringBuilder notesForDate = new StringBuilder();

        for (Catatan catatan : catatanList) {
            if (formattedDate.equals(catatan.getTanggal())) {
                notesForDate.append("- ").append(catatan.getJudul()).append("\n");
            }
        }

        if (notesForDate.length() == 0) {
            notesTextView.setText("Tidak ada catatan untuk tanggal ini.");
        } else {
            notesTextView.setText(notesForDate.toString());
        }
    }

    /**
     * Format tanggal ke dalam string dengan format "yyyy-MM-dd".
     *
     * @param date Tanggal yang akan diformat.
     * @return String hasil format tanggal.
     */
    private String formatDate(CalendarDay date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date.getDate());
    }

    /**
     * Cari catatan untuk tanggal tertentu.
     *
     * @param date Tanggal dalam format "yyyy-MM-dd".
     * @return Catatan yang ditemukan atau null jika tidak ada.
     */
    private Catatan findNoteForDate(String date) {
        for (Catatan catatan : catatanList) {
            if (catatan.getTanggal().equals(date)) {
                return catatan;
            }
        }
        return null;
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
}
