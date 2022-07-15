package com.example.diplomchik20;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class dnevnik extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;


    //сохранение и открытие файла
    //private final static String FILENAME = "sample.txt"; // имя файла
    private EditText mEditText;
    String FILENAME;
    StorageReference reference;//связь с хранилищем
    FirebaseUser UserFolder = FirebaseAuth.getInstance().getCurrentUser();//имя пользователя для его папки
    String FolderName = UserFolder.getUid();
    File localFile;
    String fname;//имя фалйа для firebase
    String[] tmp = new String[99];
    int i = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnevnik);
        fname = getIntent().getStringExtra("fname");

        mEditText = findViewById(R.id.editText);
        //TextView selection = findViewById(R.id.selection); // получаем элемент TextView
        try {
            openFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        UserFolder = FirebaseAuth.getInstance().getCurrentUser();
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        } //если есть разрешение к фалйам
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dnevnik, menu);
        return true;
    }

    @Override //нажатие на верхнее меню
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveFile();
                return true;
            default:
                return true;
        }
    }

    // Метод для открытия файла
    private void openFile() throws IOException {

        reference = FirebaseStorage.getInstance().getReference().child(FolderName).child(fname);
        localFile = File.createTempFile(fname, ".txt", new File("/data/data/com.example.diplomchik20/files"));
        FILENAME = localFile.getName();

        reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Файл загружен", Toast.LENGTH_SHORT).show();

                try {

                    InputStream inputStream = openFileInput(FILENAME);

                    if (inputStream != null) {
                        InputStreamReader isr = new InputStreamReader(inputStream);
                        BufferedReader reader = new BufferedReader(isr);
                        String line;
                        StringBuilder builder = new StringBuilder();

                        while ((line = reader.readLine()) != null) {
                            builder.append(line + "\n");
                        }

                        inputStream.close();
                        mEditText.setText(builder.toString());

                        tmp[i] = FILENAME;
                        i++;
                        //localFile.deleteOnExit();
                    }
                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(),
                            "Exception: " + t.toString(),
                            Toast.LENGTH_LONG).show();
                    mEditText.setText(FILENAME);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод для сохранения файла
    private void saveFile() {

        reference = FirebaseStorage.getInstance().getReference().child(FolderName);

        String data = mEditText.getText().toString();

        reference.child(fname).putBytes(data.getBytes()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Файл успешно сохранен", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    } //разрешение на доступ к файлам

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(int j = 0; j <= i; j++){
            try {
                deleteFile(tmp[j]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}