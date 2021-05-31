package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    Thread thread;
    public static final String LOG_TAG = "SOCKET";
    private Button mBtnSend  = null;
    private EditText mEdit     = null;
    private RecyclerView recyclerView     = null;
    private MyRecyclerViewAdapter adapter;
    ArrayList<String> listMsg = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        thread =  new Thread(Connection.getInstance());
        thread.start();
        Connection.getInstance().mainActivity = this;
        mEdit     = (EditText) findViewById(R.id.edit_gchat_message);
        // mBtnSend     = (Button) findViewById(R.id.button_gchat_send);




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        listMsg.add("User 1: текст");
        listMsg.add("User 2: текст");
        listMsg.add("User 3: текст");
        listMsg.add("User 4: текст");
        listMsg.add("User 5: текст");

*/


        recyclerView = findViewById(R.id.recycler_gchat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(getApplicationContext() , listMsg);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


    }


    public void sendMsg(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String text;
                    text = ((EditText) findViewById(R.id.edit_gchat_message)).getText().toString();
                    if (text.trim().length() == 0)
                        text = "Test message";
                    // отправляем сообщение
                    Log.e(LOG_TAG, "Отправляю сообщение (в кнопке) " + text);
                    Connection.getInstance().sendData(text.getBytes());
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        }).start();
    }

    public void updateText(String msg) {
        //listMsg.add(msg);
        /*listMsg.add("Horse");
        listMsg.add("Cow");
        adapter = new MyRecyclerViewAdapter(getApplicationContext(), listMsg);
        recyclerView.setAdapter(adapter);*/
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //adapter = new MyRecyclerViewAdapter(getApplicationContext(), listMsg);

                    //recyclerView.setAdapter(adapter);
                    ((EditText) findViewById(R.id.edit_gchat_message)).setText(msg);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

    }
}

