package com.example.smishingdetectionapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.R;

public class SupportFeedbackActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 280;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_feedback);

        // Views
        RadioGroup rgSolved = findViewById(R.id.rgSolved);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText editFeedback = findViewById(R.id.editFeedback);
        TextView tvCounter = findViewById(R.id.tvCounter);
        TextView tvRatingLabel = findViewById(R.id.tvRatingLabel);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // ---- Back button ----
        btnBack.setOnClickListener(v -> finish());

        // ---- Character counter ----
        editFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                tvCounter.setText(length + "/" + MAX_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // ---- RatingBar label ----
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            String label;
            switch ((int) rating) {
                case 1: label = "Poor 😞"; break;
                case 2: label = "Fair 😐"; break;
                case 3: label = "Average 🙂"; break;
                case 4: label = "Good 😀"; break;
                case 5: label = "Excellent 🤩"; break;
                default: label = "Select rating";
            }
            tvRatingLabel.setText(label);
        });

        // ---- Submit button ----
        btnSubmit.setOnClickListener(v -> {
            int selectedId = rgSolved.getCheckedRadioButtonId();

            if (selectedId == R.id.rbNo) {
                // go to details page if "No"
                Intent intent = new Intent(this, SupportFeedbackDetailsActivity.class);
                startActivity(intent);
            } else {
                // otherwise just thank
                Toast.makeText(this, getString(R.string.feedback_submitted), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
