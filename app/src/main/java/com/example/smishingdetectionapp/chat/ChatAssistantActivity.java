package com.example.smishingdetectionapp.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.detections.DatabaseAccess;

import java.util.HashMap;
import java.util.Map;

public class ChatAssistantActivity extends AppCompatActivity {

    private EditText userInput;
    private ImageButton sendButton;
    private RecyclerView chatRecyclerView;
    private ProgressBar progressBar;
    private ChatAdapter chatAdapter;
    private OllamaClient ollamaClient;

    private int supportPromptCount = 0;
    private static final int MAX_SUPPORT_PROMPTS = 4;

    private Map<String, String> supportPrompts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_assistant);

        // ---- UI Elements ----
        userInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Back button in header (if present)
        ImageButton backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                hideKeyboard();
                finish();
            });
        }

        // ---- RecyclerView ----
        chatAdapter = new ChatAdapter(this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Friendly welcome on empty chat
        seedWelcomeIfEmpty();

        // ---- Local LLM client ----
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        ollamaClient = new OllamaClient(databaseAccess);

        // ---- Predefined quick-support prompts ----
        supportPrompts = new HashMap<>();
        supportPrompts.put("i need help", "Sure, I’m here to help. What exactly do you need help with?");
        supportPrompts.put("smishing", "Smishing is SMS-based phishing. You can report such messages via the Community Report section.");
        supportPrompts.put("is this a scam", "Let me check that for you in the system...");
        supportPrompts.put("report", "You can report suspicious SMS under the 'Community Report' tab.");

        // ---- Send button ----
        sendButton.setOnClickListener(v -> sendMessage());
    }

    /** Adds a friendly greeting if the chat is empty. */
    private void seedWelcomeIfEmpty() {
        if (chatAdapter.getItemCount() == 0) {
            chatAdapter.addMessage(new ChatMessage(getString(R.string.chat_welcome_1), ChatMessage.BOT));
            chatAdapter.addMessage(new ChatMessage(getString(R.string.chat_welcome_2), ChatMessage.BOT));
            chatAdapter.addMessage(new ChatMessage(getString(R.string.chat_welcome_3), ChatMessage.BOT));
            chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    /** Handles sending a message, quick-support routing, and fallback to LLM. */
    private void sendMessage() {
        String message = userInput.getText().toString().trim();
        if (message.isEmpty()) return;

        hideKeyboard();

        chatAdapter.addMessage(new ChatMessage(message, ChatMessage.USER));
        userInput.setText("");
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);

        String lowerMsg = message.toLowerCase();

        // Step 1: Quick-support responses
        for (String key : supportPrompts.keySet()) {
            if (lowerMsg.contains(key)) {
                String botReply = supportPrompts.get(key);
                supportPromptCount++;

                runOnUiThread(() -> {
                    respondToUser(botReply);

                    if (key.equals("is this a scam")) {
                        // A tiny follow-up after a brief delay
                        new Handler(Looper.getMainLooper()).postDelayed(
                                () -> respondToUser("Yes, this appears to be a scam. Please avoid engaging with it."),
                                2000
                        );
                    }

                    if (supportPromptCount >= MAX_SUPPORT_PROMPTS) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            respondToUser("Transferring you to Smishing Assistant for advanced help...");
                            Log.d("ChatFlow", "Support limit reached. Forwarding to Ollama with message: " + message);
                            Toast.makeText(ChatAssistantActivity.this, "Sending to Ollama (LLM)...", Toast.LENGTH_SHORT).show();

                            // Trigger Ollama (LLM)
                            ollamaClient.getResponse(message, response ->
                                    runOnUiThread(() -> respondToUser(response))
                            );
                        }, 2000);
                    }
                });

                return; // handled via quick-support
            }
        }

        // Step 2: Normal LLM call
        ollamaClient.getResponse(message, response ->
                runOnUiThread(() -> respondToUser(response))
        );
    }

    /** Adds a bot response and restores UI state. */
    private void respondToUser(String response) {
        progressBar.setVisibility(View.GONE);
        sendButton.setEnabled(true);
        chatAdapter.addMessage(new ChatMessage(response, ChatMessage.BOT));
        chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }

    /** Hides the soft keyboard safely. */
    private void hideKeyboard() {
        try {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }
    }
}