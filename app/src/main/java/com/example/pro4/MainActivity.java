package com.example.pro4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_CHAT_LIST = "com.example.pro4.CHAT_LIST";

    private EditText editText;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private final List<ChatMessage> messageList = new ArrayList<>();

    private String currentUser = "Alek";
    private String otherUser = "Anto";

    private ActivityResultLauncher<Intent> secondActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        recyclerView = findViewById(R.id.recycler_messages);
        Button btnBurger = findViewById(R.id.btn_burger);
        Button btnSend = findViewById(R.id.button);
        Button btnSwitch = findViewById(R.id.button_switch);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);

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

        // Burger menu for choosing who starts / who is current user
        btnBurger.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, btnBurger);
            popup.getMenu().add("Mulai dari: Alek (Default)");
            popup.getMenu().add("Mulai dari: Anto");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().toString().contains("Anto")) {
                    currentUser = "Anto";
                    otherUser = "Alek";
                    Toast.makeText(this, "Percakapan dimulai oleh Anto", Toast.LENGTH_SHORT).show();
                } else {
                    currentUser = "Alek";
                    otherUser = "Anto";
                    Toast.makeText(this, "Percakapan dimulai oleh Alek", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            popup.show();
        });

        btnSend.setOnClickListener(v -> sendMessage());

        btnSwitch.setOnClickListener(v -> openSecondActivity());

        secondActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    List<ChatMessage> returnedList = (List<ChatMessage>) result.getData().getSerializableExtra(EXTRA_CHAT_LIST);
                    if (returnedList != null) {
                        messageList.clear();
                        // Re-adjust isSentByMe relative to MainActivity's currentUser (Alek)
                        for (ChatMessage msg : returnedList) {
                            boolean isMine = msg.getSender().equals(currentUser);
                            messageList.add(new ChatMessage(msg.getSender(), msg.getMessage(), msg.getTimestamp(), isMine));
                        }
                        chatAdapter.notifyDataSetChanged();
                        scrollToBottom();
                    }
                }
            }
        );

        // Initial welcome message if list is empty
        if (messageList.isEmpty()) {
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            messageList.add(new ChatMessage(currentUser, "Halo, mari mulai ngobrol!", time, true));
            chatAdapter.notifyDataSetChanged();
        }
    }

    private void sendMessage() {
        String messageText = editText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            messageList.add(new ChatMessage(currentUser, messageText, time, true));
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            scrollToBottom();
            editText.setText("");
        }
    }

    private void openSecondActivity() {
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra(EXTRA_CHAT_LIST, (ArrayList<ChatMessage>) messageList);
        intent.putExtra("CURRENT_USER", otherUser);
        secondActivityLauncher.launch(intent);
    }

    private void scrollToBottom() {
        if (!messageList.isEmpty()) {
            recyclerView.scrollToPosition(messageList.size() - 1);
        }
    }
}
