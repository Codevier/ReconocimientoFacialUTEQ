package com.example.reconocimientofacialuteq.socket;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.reconocimientofacialuteq.clase.Servidor;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientThread implements Runnable {
    private static final int SERVERPORT = 5555;
    private static final String SERVER_IP = "192.168.0.102";
    private  Socket socket;
    private  String dim;
    private byte[] message;
    String user;
    Bitmap bitmap;
    String timestamp;

    public ClientThread(Bitmap bitmap, String user) {
        this.bitmap=bitmap;
        this.user=user;
        timestamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        try {
            socket = new Socket(Servidor.IpServidor, Servidor.PuertoReconocomiento);
            DataOutputStream salida;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            //TextView salidaTextView = (TextView) findViewById(R.id.textView2);
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                objectOutputStream.writeObject(byteArray);
                objectOutputStream.writeObject(user);
                objectOutputStream.writeObject(user+timestamp+".jpg");
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                dim=(String) entrada.readUTF();
                message=new byte[Integer.parseInt(dim)];
                for( int i = 0; i < message.length; i++ )
                {
                    message[ i ] = ( byte )entrada.read( );
                }

            }
        }
        catch (UnknownHostException e1)
        {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
