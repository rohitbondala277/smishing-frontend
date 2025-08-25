package com.example.smishingdetectionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.ui.FaqActivity;
import com.google.android.material.card.MaterialCardView;

public class HelpActivity extends SharedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_updated);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Back
        ImageButton back = findViewById(R.id.help_back);
        back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Call Us
        MaterialCardView cardCallUs = findViewById(R.id.cardCallUs);
        cardCallUs.setOnClickListener(v -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:+1234567890")); // TODO: replace
            startActivity(phoneIntent);
        });

        // Mail Us
        MaterialCardView cardMailUs = findViewById(R.id.cardMailUs);
        cardMailUs.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@example.com")); // TODO: replace
            startActivity(emailIntent);
        });

        // FAQ
        MaterialCardView cardFAQ1 = findViewById(R.id.cardFAQ1);
        cardFAQ1.setOnClickListener(v ->
                startActivity(new Intent(HelpActivity.this, FaqActivity.class))
        );

        // Feedback
        MaterialCardView cardFeedback = findViewById(R.id.cardFeedback);
        cardFeedback.setOnClickListener(v ->
                Toast.makeText(HelpActivity.this, "Feedback", Toast.LENGTH_SHORT).show()
        );
    }
}
