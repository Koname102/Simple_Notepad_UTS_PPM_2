package com.diyas.uts_ppm_2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.diyas.uts_ppm_2.R;

public class NotepadFragment extends Fragment {

    private static final String ARG_DATE = "arg_date";
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_CONTENT = "arg_content";

    private String selectedDate;
    private String initialTitle;
    private String initialContent;

    private EditText titleInput;
    private EditText noteInput;

    private OnNoteUpdateListener noteUpdateListener;

    /**
     * Membuat instance baru dari NotepadFragment dengan data yang ada.
     *
     * @param date    Tanggal catatan.
     * @param title   Judul catatan (null jika tidak ada).
     * @param content Isi catatan (null jika tidak ada).
     * @return Instance NotepadFragment.
     */
    public static NotepadFragment newInstance(String date, String title, String content) {
        NotepadFragment fragment = new NotepadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notepad, container, false);

        // Ambil data dari argumen
        if (getArguments() != null) {
            selectedDate = getArguments().getString(ARG_DATE);
            initialTitle = getArguments().getString(ARG_TITLE, "");
            initialContent = getArguments().getString(ARG_CONTENT, "");
        }

        // Inisialisasi elemen input
        titleInput = view.findViewById(R.id.notepad_title);
        noteInput = view.findViewById(R.id.notepad_input);

        // Isi data awal jika ada
        initializeNoteData();

        return view;
    }

    /**
     * Mengisi data awal catatan jika sedang mengedit.
     */
    private void initializeNoteData() {
        if (titleInput != null) {
            titleInput.setText(initialTitle);
        }
        if (noteInput != null) {
            noteInput.setText(initialContent);
        }
    }

    /**
     * Mendapatkan tanggal yang dipilih.
     *
     * @return Tanggal yang dipilih atau null jika tidak tersedia.
     */
    public String getDate() {
        return selectedDate;
    }

    /**
     * Mendapatkan judul dari input.
     *
     * @return String judul atau string kosong jika tidak ada input.
     */
    public String getTitle() {
        return titleInput != null ? titleInput.getText().toString().trim() : "";
    }

    /**
     * Mendapatkan konten dari input.
     *
     * @return String konten atau string kosong jika tidak ada input.
     */
    public String getContent() {
        return noteInput != null ? noteInput.getText().toString().trim() : "";
    }

    /**
     * Mengecek apakah konten catatan atau judul berubah dari data awal.
     *
     * @return True jika ada perubahan, false jika tidak.
     */
    public boolean isNoteChanged() {
        String currentTitle = getTitle();
        String currentContent = getContent();
        return !initialTitle.equals(currentTitle) || !initialContent.equals(currentContent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Panggil listener untuk memperbarui catatan jika terjadi perubahan
        if (noteUpdateListener != null && isNoteChanged()) {
            String updatedTitle = getTitle();
            String updatedContent = getContent();

            noteUpdateListener.onNoteUpdated(selectedDate, updatedTitle, updatedContent);
        }
    }

    /**
     * Mengatur listener untuk menerima pembaruan catatan.
     *
     * @param listener Listener untuk pembaruan catatan.
     */
    public void setOnNoteUpdateListener(OnNoteUpdateListener listener) {
        this.noteUpdateListener = listener;
    }

    /**
     * Interface untuk menangani pembaruan catatan.
     */
    public interface OnNoteUpdateListener {
        void onNoteUpdated(String date, String title, String content);
    }
}
