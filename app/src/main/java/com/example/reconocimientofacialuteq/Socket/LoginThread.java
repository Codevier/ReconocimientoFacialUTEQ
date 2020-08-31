package com.example.reconocimientofacialuteq.Socket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.reconocimientofacialuteq.MainActivity2;
import com.example.reconocimientofacialuteq.ui.login.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoginThread implements Runnable {
    private static final int SERVERPORT = 5556;
    private static final String SERVER_IP = "192.168.0.102";
    private  Socket socket;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        try {
            socket = new Socket(SERVER_IP, SERVERPORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String Login(String usuario, String clave) throws IOException {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
            salida.writeUTF(usuario);
            salida.flush();
            salida.writeUTF(clave);
            salida.flush();
        return "Ok";
    }
}
