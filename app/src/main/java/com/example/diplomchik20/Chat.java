package com.example.diplomchik20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.text.format.DateFormat;

public class Chat extends AppCompatActivity {

    int ID; //id по выбору пунка в списке чатов
    String chatID; //id для вывода и пердачи из бд
    private static int SIGN_IN_CODE = 1;
    private RelativeLayout activity_chat;
    private FirebaseListAdapter<message> adapter;
    private FloatingActionButton sendBtn;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE){
            if(resultCode == RESULT_OK){
                Snackbar.make(activity_chat, "Вы авторизованы", Snackbar.LENGTH_LONG).show(); //всплывающее окно авторизации
                displayAllMessages();
            }
            else {
                Snackbar.make(activity_chat, "Вы не авторизованы", Snackbar.LENGTH_LONG).show(); //всплывающее окно авторизации
                finish();
            }
        }
    }

    private void displayAllMessages() { //получение данных из БД и заполнение листа с сообщениями

        ID = getIntent().getIntExtra("chatID", 0); //ID чата
        chatID = "ChatID:" + ID;
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        FirebaseListOptions.Builder<message> builder = new FirebaseListOptions.Builder<>();
        builder.setLayout(R.layout.list_item).setQuery(FirebaseDatabase.getInstance().getReference().child(chatID), message.class).setLifecycleOwner(this);

        adapter = new FirebaseListAdapter<message>(builder.build()) {
            @Override
            protected void populateView(@NonNull View v, @NonNull message model, int position) {

                //int chat_ID = model.getChatID();

                //if(ID == chat_ID) {
                TextView mess_user, mess_time;
                BubbleTextView mess_text;

                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);

                mess_user.setText(model.getUserName());
                mess_text.setText(model.getTextMessage());
                mess_time.setText(DateFormat.format("dd-mm-yyyy HH:mm:ss", model.getMessageTime()));
                //}
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activity_chat = findViewById(R.id.activity_chat);
        sendBtn = findViewById(R.id.btnSend);

        sendBtn.setOnClickListener(new View.OnClickListener() { //нажатие кнопки отправить
            @Override
            public void onClick(View v) {
                EditText textField = findViewById(R.id.messageField);
                if(textField.getText().toString() == ""){
                    return;
                }
                FirebaseDatabase.getInstance().getReference().child(chatID).push().setValue(
                        new message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                textField.getText().toString()
                        )
                );
                textField.setText("");
            }
        });
        displayAllMessages();
    }
}