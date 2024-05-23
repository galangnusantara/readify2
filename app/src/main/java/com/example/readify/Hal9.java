package com.example.readify;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Hal9 extends AppCompatActivity {

    private TextView bookContent;
    private int currentPage = 1;
    private TextToSpeech textToSpeech;
    private boolean isSpeaking = false;
    private int clickCount = 0;

    private boolean isPaused = false;
    // Menggunakan variabel untuk menyimpan posisi terakhir pembacaan
    private int lastPosition = 0;
    private boolean isStoppedOrPaused = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hal9);

        bookContent = findViewById(R.id.bookContent);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        ImageButton speakerButton = findViewById(R.id.Speaker);
        // Menggunakan variabel untuk melacak jumlah klik


        // Menggunakan variabel untuk melacak jumlah klik


        // Tambahkan variabel untuk menandai apakah pembacaan sedang dihentikan atau dijeda


        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Jika pembacaan sedang dihentikan atau dijeda, lanjutkan pembacaan
                if (isStoppedOrPaused) {
                    // Mendapatkan teks dari posisi terakhir hingga akhir teks
                    String text = bookContent.getText().toString().substring(lastPosition);
                    // Memulai pembacaan dari posisi terakhir
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    // Mengubah status menjadi tidak dihentikan atau dijeda
                    isStoppedOrPaused = false;
                } else {
                    // Menghentikan pembacaan jika sedang berbicara
                    if (isSpeaking) {
                        textToSpeech.stop();
                        isSpeaking = false;
                        // Mengubah status menjadi dihentikan atau dijeda
                        isStoppedOrPaused = true;
                    }
                }
            }
        });

// Menambahkan onLongClickListener untuk tombol speaker
        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused) {
                    // Jika sedang di pause, lanjutkan pembacaan
                    String text = bookContent.getText().toString().substring(lastPosition);
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    isPaused = false;
                } else {
                    if (isSpeaking) {
                        // Jika sedang berbicara, hentikan pembacaan
                        textToSpeech.stop();
                        isPaused = true;
                    } else {
                        // Mulai pembacaan dari awal jika tidak sedang berbicara
                        String text = bookContent.getText().toString();
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                        isSpeaking = true;
                    }
                }
            }
        });





// Menambahkan onLongClickListener untuk tombol speaker



        Button backButtonn = findViewById(R.id.backButtonn);
        backButtonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 1) {
                    currentPage--;
                    showPageContent(currentPage);
                }
            }
        });

        // Ambil referensi tombol listMode
        ImageButton listModeButton = findViewById(R.id.listMode);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton slideIcon = findViewById(R.id.slide);
        ImageButton bookmarkIcon = findViewById(R.id.bookmarkButton);
        ImageButton otherButton = findViewById(R.id.Lainnya);
        Spinner pageSpinner = findViewById(R.id.pageSpinner);

        final boolean[] isDarkTheme = {false};

        LinearLayout linearLayout = findViewById(R.id.Bar);

        listModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout mainLayout = findViewById(R.id.mainLayout);

                if (!isDarkTheme[0]) {
                    mainLayout.setBackgroundColor(Color.BLACK);

                    TextView bookTitle = findViewById(R.id.bookTitle);
                    bookTitle.setTextColor(Color.WHITE);

                    TextView bookContent = findViewById(R.id.bookContent);
                    bookContent.setTextColor(Color.WHITE);

                    listModeButton.setColorFilter(Color.WHITE);
                    backButton.setColorFilter(Color.WHITE);
                    menuButton.setColorFilter(Color.WHITE);
                    speakerButton.setColorFilter(Color.WHITE);
                    slideIcon.setColorFilter(Color.WHITE);
                    bookmarkIcon.setColorFilter(Color.WHITE);
                    otherButton.setColorFilter(Color.WHITE); // Tambahkan kode untuk otherButton

                    pageSpinner.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

                    // Mengubah warna teks Spinner menjadi putih
                    ((TextView) pageSpinner.getSelectedView()).setTextColor(Color.WHITE);

                    linearLayout.setBackgroundColor(Color.BLACK);

                    listModeButton.setBackgroundColor(Color.BLACK);
                    backButton.setBackgroundColor(Color.BLACK);
                    menuButton.setBackgroundColor(Color.BLACK);
                    speakerButton.setBackgroundColor(Color.BLACK);
                    slideIcon.setBackgroundColor(Color.BLACK);
                    bookmarkIcon.setBackgroundColor(Color.BLACK);
                    otherButton.setBackgroundColor(Color.BLACK); // Tambahkan kode untuk otherButton

                    isDarkTheme[0] = true;
                } else {
                    mainLayout.setBackgroundColor(Color.WHITE);

                    TextView bookTitle = findViewById(R.id.bookTitle);
                    bookTitle.setTextColor(Color.BLACK);

                    TextView bookContent = findViewById(R.id.bookContent);
                    bookContent.setTextColor(Color.BLACK);

                    listModeButton.setColorFilter(null);
                    backButton.setColorFilter(null);
                    menuButton.setColorFilter(null);
                    speakerButton.setColorFilter(null);
                    slideIcon.setColorFilter(null);
                    bookmarkIcon.setColorFilter(null);
                    otherButton.setColorFilter(null); // Tambahkan kode untuk otherButton

                    pageSpinner.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

                    // Mengubah warna teks Spinner menjadi hitam
                    ((TextView) pageSpinner.getSelectedView()).setTextColor(Color.BLACK);

                    linearLayout.setBackgroundColor(Color.WHITE);

                    listModeButton.setBackgroundColor(Color.WHITE);
                    backButton.setBackgroundColor(Color.WHITE);
                    menuButton.setBackgroundColor(Color.WHITE);
                    speakerButton.setBackgroundColor(Color.WHITE);
                    slideIcon.setBackgroundColor(Color.WHITE);
                    bookmarkIcon.setBackgroundColor(Color.WHITE);
                    otherButton.setBackgroundColor(Color.WHITE); // Tambahkan kode untuk otherButton

                    isDarkTheme[0] = false;
                }
            }
        });








        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxPages = 3;
                if (currentPage < maxPages) {
                    currentPage++;
                    showPageContent(currentPage);
                }
            }
        });

        Button firstPageButton = findViewById(R.id.firstPageButton);
        firstPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = 1;
                showPageContent(currentPage);
            }
        });

        Button lastPageButton = findViewById(R.id.lastPageButton);
        lastPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxPages = 3;
                currentPage = maxPages;
                showPageContent(currentPage);
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.page_numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pageSpinner.setAdapter(adapter);

// Mengubah warna teks pada item Spinner menjadi biru muda
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pageSpinner.setAdapter(adapter);
        pageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPage = position + 1;
                showPageContent(currentPage);

                // Mengubah warna teks pada item terpilih Spinner menjadi biru muda
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        showPageContent(currentPage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Memastikan bahwa jika sedang berbicara, sedang di pause, atau sedang di hentikan, sumber daya textToSpeech tidak dibersihkan secara otomatis
        if (textToSpeech != null && (isSpeaking || isPaused || isStoppedOrPaused)) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }



    private void showPageContent(int page) {
        switch (page) {
            case 1:
                bookContent.setText(getString(R.string.page_1_content));
                break;
            case 2:
                bookContent.setText(getString(R.string.page_2_content));
                break;

            case 3:
                bookContent.setText(getString(R.string.page_3_content));
                break;
            // Tambahkan case untuk setiap halaman berikutnya
            default:
                // Default action, jika tidak ada halaman yang sesuai
                break;
        }
        Spinner pageSpinner = findViewById(R.id.pageSpinner);
        pageSpinner.setSelection(page - 1);
    }
}
