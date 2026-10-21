package com.example.pro4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.pro4.MESSAGE";
    public static final String EXTRA_REPLY = "com.example.pro4.REPLY";
    public static final String EXTRA_LOG = "com.example.pro4.LOG";

    private EditText editText;
    private TextView textMessage;
    private ScrollView scrollLog;
    private StringBuilder messageLog = new StringBuilder();

    private ActivityResultLauncher<Intent> secondActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        textMessage = findViewById(R.id.text_message);
        scrollLog = findViewById(R.id.scroll_log);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barsAndIme = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            int padding12 = (int) (12 * getResources().getDisplayMetrics().density);
            v.setPadding(
                barsAndIme.left + padding12,
                barsAndIme.top + padding12,
                barsAndIme.right + padding12,
                barsAndIme.bottom + padding12
            );
            scrollLog.post(() -> scrollLog.fullScroll(View.FOCUS_DOWN));
            return WindowInsetsCompat.CONSUMED;
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollLog.postDelayed(() -> scrollLog.fullScroll(View.FOCUS_DOWN), 200);
            }
        });

        secondActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String updatedLog = result.getData().getStringExtra(EXTRA_LOG);
                    if (updatedLog != null && !updatedLog.isEmpty()) {
                        messageLog = new StringBuilder(updatedLog);
                    } else {
                        String reply = result.getData().getStringExtra(EXTRA_REPLY);
                        if (reply != null && !reply.isEmpty()) {
                            if (messageLog.length() > 0) messageLog.append("\n");
                            messageLog.append("User 2: ").append(reply);
                        }
                    }
                    textMessage.setText(messageLog.toString());
                    scrollLog.post(() -> scrollLog.fullScroll(View.FOCUS_DOWN));
                }
            }
        );
    }

    public void openSecondActivity(View view) {
        String message = editText.getText().toString().trim();
        if (!message.isEmpty()) {
            if (messageLog.length() > 0) {
                messageLog.append("\n");
            }
            messageLog.append("Alek: ").append(message);
            textMessage.setText(messageLog.toString());
            scrollLog.post(() -> scrollLog.fullScroll(View.FOCUS_DOWN));

            Intent intent = new Intent(this, MainActivity2.class);
            intent.putExtra(EXTRA_MESSAGE, message);
            intent.putExtra(EXTRA_LOG, messageLog.toString());
            secondActivityLauncher.launch(intent);

            editText.setText("");
        }
    }
}
