package com.example.diplomchik20;

import android.app.Dialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class spisok_dnevnikov extends AppCompatActivity {

    public void pFile(){
        reference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {//составления списка из названий в хранилище
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    adapter.add(item.getName());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    StorageReference reference;
    FirebaseUser UserFolder = FirebaseAuth.getInstance().getCurrentUser();
    String FolderName;

    String FILENAME;
    ArrayAdapter<String> adapter;
    Dialog renamednevnik;
    String fnname;
    EditText newname;
    String tmp;//временный файл для переименовки
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spisok_dnevnikov);

        FolderName = UserFolder.getUid();
        reference = FirebaseStorage.getInstance().getReference().child(FolderName);//связь с хранилищем

        Button createbtn = findViewById(R.id.create);//кнопка добавить
        pFile();//метод получения файлов из деректории

        //диалог создания дневника
        Dialog creatednevnik = new Dialog(spisok_dnevnikov.this);//диалог для создания дневника
        creatednevnik.setTitle("Создать дневник");//заголовок диалога
        //разметка диалога
        creatednevnik.setContentView(R.layout.create_dnevik);
        Button cancel = creatednevnik.findViewById(R.id.cancel);//кнопка отмены в диалоге создания
        Button accept = creatednevnik.findViewById(R.id.accept);//кнопка принятия в диалоге создания
        EditText name = creatednevnik.findViewById(R.id.name);//текстовое поле в диалоге создания

        //диалог переименования дневника
        renamednevnik = new Dialog((spisok_dnevnikov.this));
        renamednevnik.setTitle("Переименовать дневник");
        renamednevnik.setContentView(R.layout.rename_dnevnik);
        Button rename = renamednevnik.findViewById(R.id.rename);
        Button norename = renamednevnik.findViewById(R.id.norename);
        newname = renamednevnik.findViewById(R.id.newname);

        ListView mainList = findViewById(R.id.mList); //получаем список
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1); //создаем адаптер
        mainList.setAdapter(adapter);//добавляем адаптер
        //создаем контекстное меню
        registerForContextMenu(mainList);

        // добавляем для списка слушатель
        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener(){ //переход на выбранный дневник
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                // по позиции получаем выбранный элемент
                FILENAME = adapter.getItem(position);
                //связь между списком и окном с дневником
                Intent intent = new Intent(spisok_dnevnikov.this, dnevnik.class);
                //указываем пердаваемые данные
                intent.putExtra("fname", FILENAME);
                //открываем новую страницу
                startActivity(intent);
            }
        });

        //создать дневник
        createbtn.setOnClickListener(new View.OnClickListener() { //показать диалог создания дневника
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.create_dnevik);//замена layout
                creatednevnik.show();
                name.setText("");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() { //скрыть диалог создания дневника
            @Override
            public void onClick(View view) {
                creatednevnik.hide();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() { //создание нового дневника
            @Override
            public void onClick(View view) {
                String fname = name.getText().toString();//создаваемый файл

                reference = FirebaseStorage.getInstance().getReference().child(FolderName);
                String data = "";
                reference.child(fname).putBytes(data.getBytes());

                adapter.add(fname);
                adapter.notifyDataSetChanged();//уведомляем адаптер о изменнии данных
                creatednevnik.hide();
            }
        });

        //переименовать дневник
        norename.setOnClickListener(new View.OnClickListener() {//отмена переименовки
            @Override
            public void onClick(View view) {
                renamednevnik.hide();
            }
        });

        rename.setOnClickListener(new View.OnClickListener() {//переименовать
            @Override
            public void onClick(View view) {
                String fname = fnname; //название файал полученное при клике на пункт списка
                String nname = newname.getText().toString();

                reference = FirebaseStorage.getInstance().getReference().child(FolderName).child(fname);

                File localFile = new File("/data/data/com.example.diplomchik20/files/" + nname + ".txt");
                tmp = localFile.getName();

                if(localFile != null) {
                    reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {

                                InputStream inputStream = openFileInput(localFile.getName());

                                if (inputStream != null) {
                                    InputStreamReader isr = new InputStreamReader(inputStream);
                                    BufferedReader reader = new BufferedReader(isr);
                                    String line;
                                    StringBuilder builder = new StringBuilder();

                                    while ((line = reader.readLine()) != null) {
                                        builder.append(line + "\n");
                                    }
                                    inputStream.close();
                                    data = builder.toString();

                                    reference = FirebaseStorage.getInstance().getReference().child(FolderName);

                                    reference.child(nname).putBytes(data.getBytes()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(getApplicationContext(), "Файл успешно сохранен", Toast.LENGTH_SHORT).show();

                                            reference.child(nname).putBytes(data.getBytes()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Toast.makeText(getApplicationContext(), "Файл успешно сохранен", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            reference = FirebaseStorage.getInstance().getReference().child(FolderName).child(fname);
                                            reference.delete();
                                            deleteFile(tmp);

                                            adapter.remove(fname);
                                            adapter.add(nname);
                                            adapter.notifyDataSetChanged();
                                            renamednevnik.hide();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            } catch (Throwable t) {
                                Toast.makeText(getApplicationContext(),
                                        "Exception: " + t.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "ошибка.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //контекстное меню
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_list, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                editItem(info.position); // метод, выполняющий действие при редактировании пункта меню
                return true;
            case R.id.delete:
                deleteItem(info.position); //метод, выполняющий действие при удалении пункта меню
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void editItem(int position) {
        fnname = adapter.getItem(position);//получаем название файла по позиции
        renamednevnik.show();
        newname.setText("");
    }

    private void deleteItem(int position) {
        String fname = adapter.getItem(position);//получаем название файла по позиции
        reference = FirebaseStorage.getInstance().getReference().child(FolderName).child(fname);
        reference.delete();

        adapter.remove(fname);
        adapter.notifyDataSetChanged();
    }
}