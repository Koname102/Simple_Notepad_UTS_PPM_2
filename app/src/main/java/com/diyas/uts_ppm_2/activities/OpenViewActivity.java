package com.diyas.uts_ppm_2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.diyas.uts_ppm_2.R;

public class OpenViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_open_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Pindah ke MainActivity setelah beberapa detik
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(OpenViewActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Tutup aktivitas ini agar tidak kembali saat tombol "back" ditekan
        }, 2000); // 2000ms = 2 detik
    }
}