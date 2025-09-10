package com.example.smishingdetectionapp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.R;

import java.util.ArrayList;
import java.util.List;

public class SupportFeedbackDetailsActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 280;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_feedback_details);

        // Views
        CheckBox cbSolution = findViewById(R.id.cbSolution);
        CheckBox cbRobotic = findViewById(R.id.cbRobotic);
        CheckBox cbSlow = findViewById(R.id.cbSlow);
        CheckBox cbWrongWords = findViewById(R.id.cbWrongWords);
        CheckBox cbRepetitive = findViewById(R.id.cbRepetitive);
        CheckBox cbUnfriendly = findViewById(R.id.cbUnfriendly);

        EditText editFeedback = findViewById(R.id.editFeedbackDetails);
        TextView tvCounter = findViewById(R.id.tvCounterDetails);
        Button btnSubmit = findViewById(R.id.btnSubmitDetails);
        ImageButton btnBack = findViewById(R.id.btnBackDetails);

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

        // ---- Submit ----
        btnSubmit.setOnClickListener(v -> {
            List<String> issues = new ArrayList<>();
            if (cbSolution.isChecked()) issues.add(getString(R.string.problem_solution_not_helpful));
            if (cbRobotic.isChecked()) issues.add(getString(R.string.problem_robotic_replies));
            if (cbSlow.isChecked()) issues.add(getString(R.string.problem_slow_response));
            if (cbWrongWords.isChecked()) issues.add(getString(R.string.problem_wrong_words));
            if (cbRepetitive.isChecked()) issues.add(getString(R.string.problem_repetitive));
            if (cbUnfriendly.isChecked()) issues.add(getString(R.string.problem_unfriendly));

            String feedbackText = editFeedback.getText().toString().trim();

            // Debug log (you can later send to API/DB)
            System.out.println("Issues: " + issues);
            System.out.println("Feedback Text: " + feedbackText);

            Toast.makeText(this, getString(R.string.feedback_submitted), Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
