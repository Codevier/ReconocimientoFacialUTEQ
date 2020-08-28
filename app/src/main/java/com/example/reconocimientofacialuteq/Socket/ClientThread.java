package com.example.reconocimientofacialuteq.Socket;

import android.graphics.Bitmap;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.reconocimientofacialuteq.R;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread implements Runnable {
    private static final int SERVERPORT = 5555;
    private static final String SERVER_IP = "192.168.0.102";
    private  Socket socket;
    Bitmap bitmap;

    public ClientThread(Bitmap bitmap) {
        this.bitmap=bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        try {
            socket = new Socket(SERVER_IP, SERVERPORT);
            DataOutputStream salida;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            //TextView salidaTextView = (TextView) findViewById(R.id.textView2);
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                objectOutputStream.writeObject(byteArray);
                objectOutputStream.writeObject("nombre");
            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
