package com.example.reconocimientofacialuteq.ui.misdatos;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reconocimientofacialuteq.clase.Servidor;
import com.example.reconocimientofacialuteq.R;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Mis_Datos extends Fragment {

    private MisDatosViewModel mViewModel;
    private  Socket socket;
    private TextView correo, nombres;
    private String imagen,id_us="1";
    private ImageView img;
    View root;
    public static Mis_Datos newInstance() {
        return new Mis_Datos();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.mis__datos_fragment, container, false);
        correo= root.findViewById(R.id.correo_us);
        nombres=root.findViewById(R.id.nombre_us);
        img=root.findViewById(R.id.profile_image);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id
                new Thread(new Mis_Datos.ClientThreadLog()).start();
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MisDatosViewModel.class);
        // TODO: Use the ViewModel
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
                    imagen= (String)entrada.readUTF();
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


