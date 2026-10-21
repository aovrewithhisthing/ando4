package com.example.pro4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    private EditText editTextSecond;
    private TextView textMessage;
    private ScrollView scrollLogSecond;
    private StringBuilder messageLog = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        editTextSecond = findViewById(R.id.editText_second);
        textMessage = findViewById(R.id.text_message);
        scrollLogSecond = findViewById(R.id.scroll_log_second);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barsAndIme = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            int padding12 = (int) (12 * getResources().getDisplayMetrics().density);
            v.setPadding(
                barsAndIme.left + padding12,
                barsAndIme.top + padding12,
                barsAndIme.right + padding12,
                barsAndIme.bottom + padding12
            );
            scrollLogSecond.post(() -> scrollLogSecond.fullScroll(View.FOCUS_DOWN));
            return WindowInsetsCompat.CONSUMED;
        });

        editTextSecond.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollLogSecond.postDelayed(() -> scrollLogSecond.fullScroll(View.FOCUS_DOWN), 200);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String log = intent.getStringExtra(MainActivity.EXTRA_LOG);
            if (log != null && !log.isEmpty()) {
                messageLog = new StringBuilder(log);
            }
            textMessage.setText(messageLog.toString());
            scrollLogSecond.post(() -> scrollLogSecond.fullScroll(View.FOCUS_DOWN));
        }
    }

    public void replyMessage(View view) {
        String reply = editTextSecond.getText().toString().trim();
        if (!reply.isEmpty()) {
            if (messageLog.length() > 0) {
                messageLog.append("\n");
            }
            messageLog.append("Anto: ").append(reply);

            Intent replyIntent = new Intent();
            replyIntent.putExtra(MainActivity.EXTRA_REPLY, reply);
            replyIntent.putExtra(MainActivity.EXTRA_LOG, messageLog.toString());
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }
}
