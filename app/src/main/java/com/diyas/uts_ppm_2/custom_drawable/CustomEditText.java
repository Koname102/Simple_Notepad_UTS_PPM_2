package com.diyas.uts_ppm_2.custom_drawable;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.diyas.uts_ppm_2.R;

public class CustomEditText extends FrameLayout {

    private TextView hintText;
    private EditText editText;

    public CustomEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_edittext, this, true);

        hintText = findViewById(R.id.hint_text);
        editText = findViewById(R.id.custom_edit_text);

        // Animasi hint saat fokus
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Pindahkan hint keluar EditText, kecilkan ukuran, ubah warna
                hintText.animate()
                        .translationY(-90f)  // Geser keluar
                        .translationX(-30f)  // Sejajarkan ke kiri dengan EditText
                        .scaleX(0.8f)        // Kecilkan ukuran
                        .scaleY(0.8f)        // Kecilkan ukuran
                        .setDuration(200)
                        .start();
                hintText.setTextColor(0xFF2196F3); // Ubah warna ke biru saat fokus
            } else if (editText.getText().toString().isEmpty()) {
                // Kembalikan hint ke dalam EditText, ukuran, dan warna semula
                hintText.animate()
                        .translationY(0f)    // Kembali ke posisi awal
                        .translationX(0f)    // Reset posisi horizontal
                        .scaleX(1f)         // Ukuran normal
                        .scaleY(1f)         // Ukuran normal
                        .setDuration(200)
                        .start();
                hintText.setTextColor(0xFF9E9E9E); // Ubah warna kembali ke abu-abu
            }
        });

        // Opacity hint berdasarkan teks
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    hintText.setAlpha(0.5f); // Transparansi saat ada teks
                } else {
                    hintText.setAlpha(1f);   // Opacity penuh jika kosong
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Method untuk menghapus fokus dan keyboard
    public void clearFocusAndHideKeyboard() {
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    // Public methods
    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String text) {
        editText.setText(text);
    }

    public void setHint(String hint) {
        hintText.setText(hint);
    }
}
