package com.example.reconocimientofacialuteq.ui.gallery;

import android.app.Activity;
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

import com.example.reconocimientofacialuteq.MainActivity;
import com.example.reconocimientofacialuteq.MainActivity2;
import com.example.reconocimientofacialuteq.R;
import com.example.reconocimientofacialuteq.Socket.ClientThread;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
        Notif=(Button) root.findViewById(R.id.butonNotificacion);
        btnGaleria=(Button) root.findViewById(R.id.buttonGalllery);
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });
        Notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return root;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null )
        {
            String usuario = data.getExtras().getString("idUser");
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


}
