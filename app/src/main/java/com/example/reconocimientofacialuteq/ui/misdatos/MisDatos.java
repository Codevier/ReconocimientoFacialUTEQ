package com.example.reconocimientofacialuteq.ui.misdatos;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private ImageView img,cambiarimg;
    private Bitmap bitmap;
    ProgressDialog progDailog;
    Dialog dg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_datos);
        correo= findViewById(R.id.correo_us);
        dg=new Dialog(this);
        nombres=findViewById(R.id.nombre_us);
        img=(ImageView) findViewById(R.id.profile_image);
        cambiarimg=(ImageView) findViewById(R.id.cambiar_img);
        progDailog = new ProgressDialog(MisDatos.this);
        progDailog.setMessage("Cargando datos...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
        new Thread(new MisDatos.ClientThreadLog()).start();
        cambiarimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id
                dg.setContentView(R.layout.custompopup);
                dg.show();

            }
        });
    }
    public void close(View view){
        dg.dismiss();
    }

    class ClientThreadLog implements Runnable {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            try {
                String nombresS="";
                String correos="";
                String im;
                String dim;
                byte[] message;
                socket = new Socket(Servidor.IpServidor, Servidor.PuertoMisdatos);
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                    objectOutputStream.writeObject(id_us);
                    DataInputStream entrada = new DataInputStream(socket.getInputStream());
                    nombresS= (String) entrada.readUTF();
                    correos =(String)entrada.readUTF();
                    dim=entrada.readUTF();
                    nombres.setText(nombresS);
                    correo.setText(correos);
                    message=new byte[Integer.parseInt(dim)];
                    for( int i = 0; i < message.length; i++ )
                    {
                        message[ i ] = ( byte )entrada.read( );
                    }
                    bitmap = BitmapFactory.decodeByteArray(message, 0, message.length);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TextView imageDetail = (TextView)findViewById(R.id.txtResult);
                            //imageDetail.setText(text.getText());
                            img.setImageBitmap(bitmap);
                        }
                    });


                }


            }
             catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

}