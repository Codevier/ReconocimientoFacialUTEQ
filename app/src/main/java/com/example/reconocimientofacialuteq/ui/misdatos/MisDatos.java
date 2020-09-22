package com.example.reconocimientofacialuteq.ui.misdatos;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reconocimientofacialuteq.Clase.Servidor;
import com.example.reconocimientofacialuteq.R;
import com.example.reconocimientofacialuteq.ui.home.HomeViewModel;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MisDatos extends AppCompatActivity {
    private MisDatosViewModel mViewModel;
    private  Socket socket;
    private TextView correo, nombres;
    private String id_us="1";
    private byte[] imagen;
    private HomeViewModel homeViewModel;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_datos);
        correo= findViewById(R.id.correo_us);
        nombres=findViewById(R.id.nombre_us);
        img=findViewById(R.id.profile_image);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id
                new Thread(new MisDatos.ClientThreadLog()).start();
            }
        });

    }
    class ClientThreadLog implements Runnable {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            try {
                String nombresS="";
                String correo="";

                socket = new Socket(Servidor.IpServidor, Servidor.PuertoMisdatos);
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                    objectOutputStream.writeObject(id_us);
                    DataInputStream entrada = new DataInputStream(socket.getInputStream());
                    nombresS= (String) entrada.readUTF();
                    correo =(String)entrada.readUTF();
                    entrada.readFully(imagen);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}