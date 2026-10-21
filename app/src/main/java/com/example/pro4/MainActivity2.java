package com.example.pro4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    private EditText editTextSecond;
    private RecyclerView recyclerViewSecond;
    private ChatAdapter chatAdapter;
    private final List<ChatMessage> messageList = new ArrayList<>();

    private String currentUser = "Anto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        editTextSecond = findViewById(R.id.editText_second);
        recyclerViewSecond = findViewById(R.id.recycler_messages_second);
        Button btnBurger = findViewById(R.id.btn_burger_second);
        Button btnSend = findViewById(R.id.button_second);
        Button btnBack = findViewById(R.id.button_back_main);

        String passedUser = getIntent().getStringExtra("CURRENT_USER");
        if (passedUser != null && !passedUser.isEmpty()) {
            currentUser = passedUser;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewSecond.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(messageList);
        recyclerViewSecond.setAdapter(chatAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barsAndIme = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            int padding12 = (int) (12 * getResources().getDisplayMetrics().density);
            v.setPadding(
                barsAndIme.left + padding12,
                barsAndIme.top + padding12,
                barsAndIme.right + padding12,
                barsAndIme.bottom + padding12
            );
            scrollToBottom();
            return WindowInsetsCompat.CONSUMED;
        });

        btnBurger.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity2.this, btnBurger);
            popup.getMenu().add("Peran: Anto");
            popup.getMenu().add("Peran: Alek");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().toString().contains("Alek")) {
                    currentUser = "Alek";
                    Toast.makeText(this, "Berpindah peran ke Alek", Toast.LENGTH_SHORT).show();
                } else {
                    currentUser = "Anto";
                    Toast.makeText(this, "Berpindah peran ke Anto", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            popup.show();
        });

        Intent intent = getIntent();
        if (intent != null) {
            List<ChatMessage> passedList = (List<ChatMessage>) intent.getSerializableExtra(MainActivity.EXTRA_CHAT_LIST);
            if (passedList != null) {
                messageList.clear();
                for (ChatMessage msg : passedList) {
                    boolean isMine = msg.getSender().equals(currentUser);
                    messageList.add(new ChatMessage(msg.getSender(), msg.getMessage(), msg.getTimestamp(), isMine));
                }
                chatAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        }

        btnSend.setOnClickListener(v -> sendReply());

        btnBack.setOnClickListener(v -> returnToMain());
    }

    private void sendReply() {
        String replyText = editTextSecond.getText().toString().trim();
        if (!replyText.isEmpty()) {
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            messageList.add(new ChatMessage(currentUser, replyText, time, true));
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            scrollToBottom();
            editTextSecond.setText("");
        }
    }

    private void returnToMain() {
        Intent replyIntent = new Intent();
        replyIntent.putExtra(MainActivity.EXTRA_CHAT_LIST, (ArrayList<ChatMessage>) messageList);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    private void scrollToBottom() {
        if (!messageList.isEmpty()) {
            recyclerViewSecond.scrollToPosition(messageList.size() - 1);
        }
    }
}
