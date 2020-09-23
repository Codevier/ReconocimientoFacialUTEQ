package com.example.reconocimientofacialuteq.ui.gallery;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.reconocimientofacialuteq.Clase.Servidor;
import com.example.reconocimientofacialuteq.MainActivity;
import com.example.reconocimientofacialuteq.MainActivity2;
import com.example.reconocimientofacialuteq.R;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private static final int PICK_IMAGE = 100;
    ImageView imageView;
    Uri imageUri;
    private static Bitmap imageBitmap;
    Button btnGaleria;
    Button Notif;
    private static final int SERVERPORT = 5555;
    private static final String SERVER_IP = "192.168.0.102";
    private Socket socket;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);


        //final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        imageView= (ImageView) root.findViewById(R.id.imageGaleria);
        //Notif=(Button) root.findViewById(R.id.butonNotificacion);
        btnGaleria=(Button) root.findViewById(R.id.buttonGalllery);
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });
        /*Notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        return root;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null )
        {
            String usuario ="1";// data.getExtras().getString("idUser");

            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new ClientThread(imageBitmap, usuario)).start();
        }
        Toast.makeText(getActivity(), "Imagen cargado de galeria", Toast.LENGTH_SHORT).show();
    }
    class ClientThread implements Runnable {
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
                    dim=  entrada.readUTF();
                    message=new byte[Integer.parseInt(dim)];
                    for( int i = 0; i < message.length; i++ )
                    {
                        message[ i ] = ( byte )entrada.read( );
                    }
                    bitmap = BitmapFactory.decodeByteArray(message, 0, message.length);

                             //TextView imageDetail = (TextView)findViewById(R.id.txtResult);
                            //imageDetail.setText(text.getText());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });



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

}
