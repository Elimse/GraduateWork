package com.example.diplomchik20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class chatlist extends AppCompatActivity {

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        Button createChat = findViewById(R.id.chatCreate);
        ListView chatList = findViewById(R.id.chatList);
        String[] chats = new String[] {"Harry Potter", "Requiem for a dream ", "Lord of the rings" , "Darkly Dreaming Dexter"
        };

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chats); //создаем адаптер
        chatList.setAdapter(adapter);

        // добавляем для списка слушатель
        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener(){ //переход на выбранный дневник
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                //связь между списком и окном с чатом
                Intent intent = new Intent(chatlist.this, Chat.class);
                //порядок выбранного чата
                Integer chatID = position;
                //указываем пердаваемые данные
                intent.putExtra("chatID", chatID);
                //открываем новую страницу
                startActivity(intent);
            }
        });

        createChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Добавить создание чата
            }
        });
    }
}