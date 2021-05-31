package com.example.myapplication;
import android.os.Debug;
import android.util.Log;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
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
public class Connection implements Runnable {
    private  Socket  mSocket = null;
    private InputStream inputStream = null;
    private  String  mHost   = null;
    private  int     mPort   = 0;
    public  MainActivity mainActivity;
    ArrayList<String> listMsg = new ArrayList<>();
    private RecyclerView recyclerView     = null;
    private MyRecyclerViewAdapter adapter;

    public static final String LOG_TAG = "SOCKET";

    private Connection()  {
        this.mHost = "192.168.0.108";
        this.mPort = 5005;
    }

    private static Connection instance;

    public static Connection getInstance(){
        if(Connection.instance == null)
            instance = new Connection();
        return instance;
    }


    // Метод открытия сокета
    public void openConnection()
    {
        // Если сокет уже открыт, то он закрывается
        closeConnection();
        try {
            // Создание сокета
            mSocket = new Socket(mHost, mPort);
            inputStream = mSocket.getInputStream();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Ошибка при открытии сокета :" + e.getMessage());
        }
    }
    /**
     * Метод закрытия сокета
     */
    public void closeConnection()
    {
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Ошибка при закрытии сокета :"
                        + e.getMessage());
            } finally {
                mSocket = null;
            }
        }
        mSocket = null;
    }
    /**
     * Метод отправки данных
     */
    public void sendData(byte[] data) throws Exception {
        Log.e(LOG_TAG, "Отправляю сообщение ( в классе ) " + data.toString());
        // Проверка открытия сокета
        if (mSocket == null || mSocket.isClosed()) {
            Log.e(LOG_TAG,"Ошибка отправки данных. " +
                    "Сокет не создан или закрыт");
            throw new Exception("Ошибка отправки данных. " +
                    "Сокет не создан или закрыт");
        }
        // Отправка данных
        try {
            mSocket.getOutputStream().write(data);
            mSocket.getOutputStream().flush();
        } catch (IOException e) {
            Log.e(LOG_TAG,"Ошибка отправки данных. " +
                    e.getMessage());
            throw new Exception("Ошибка отправки данных : "
                    + e.getMessage());
        }
    }
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        closeConnection();
    }

    @Override
    public void run() {
        openConnection();
        while (true){
            byte[] data = new byte[1024*4];
            while(true) {
                try {
                    /*
                     * Получение информации :
                     *    count - количество полученных байт
                     */
                    int count;
                    count=inputStream.read(data,0,data.length);

                    if (count > 0) {
                        String msg=new String(data, 0, count);
                        listMsg.add(msg);
                        // Вывод в консоль сообщения
                        Log.e(LOG_TAG, "Получено сообщение :"+ msg);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    if( ((EditText) mainActivity.findViewById(R.id.edit_gchat_message)) != null) {
                                        ((EditText) mainActivity.findViewById(R.id.edit_gchat_message)).setText(msg);
                                        recyclerView = mainActivity.findViewById(R.id.recycler_gchat);
                                        adapter = new MyRecyclerViewAdapter(mainActivity.getApplicationContext(), listMsg);

                                        recyclerView.setAdapter(adapter);
                                    }
                                } catch (Exception e) {
                                    Log.e(LOG_TAG, e.getMessage());
                                }
                            }
                        }).start();

                    } else if (count == -1 ) {
                        // Если count=-1, то поток прерван
                        Log.e(LOG_TAG, "socket is closed");
                        mSocket.close();
                        break;
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Ошибка при  чтении сообщения :" + e.getMessage());
                }
            }
            Log.e(LOG_TAG, "Ошибка при  чтении сообщения :" + " stoped");

        }
    }
}