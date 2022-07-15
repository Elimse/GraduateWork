package com.example.diplomchik20;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //файл с настройками приложения
    public static final String APP_PREFERENCES = "mysettings";// это будет именем файла настроек
    public static final Integer APP_CHAT_LIST_ID = 0; // ID для нового чата

    private static int SIGN_IN_CODE = 1;
    private LinearLayout activity_main;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//всплывающее окно авторизации
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE){
            if(resultCode == RESULT_OK){
                Snackbar.make(activity_main, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(activity_main, "Вы не авторизованы", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity_main = findViewById(R.id.activity_main);
        ImageButton toSpisok = findViewById(R.id.toSpisok);
        ImageButton toChat = findViewById(R.id.toChat);

        FirebaseAuth.getInstance().signOut(); //разлогиниться
        //FirebaseAuth.getInstance().getCurrentUser().delete(); //удаление текущего пользователя
        //проверка авторизации пользователя
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        else{
            Snackbar.make(activity_main, "Вы авторизованы", Snackbar.LENGTH_LONG).show(); //всплывающее окно авторизации
        }

        toSpisok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, spisok_dnevnikov.class);
                startActivity(intent);
            }
        });

        toChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, chatlist.class);
                startActivity(intent);
            }
        });
    }
}