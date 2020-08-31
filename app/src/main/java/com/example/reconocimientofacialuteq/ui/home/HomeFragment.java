package com.example.reconocimientofacialuteq.ui.home;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.reconocimientofacialuteq.MainActivity;
import com.example.reconocimientofacialuteq.R;
import com.example.reconocimientofacialuteq.Socket.ClientThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ImageView imageView;
    Button btfoto;
    Intent i;
    Uri photoUri;
    String absolutePhotoPath;
    String nombre;
    private static final int PHOTO_CONST =1 ;
    private static Bitmap imageBitmap;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        imageView = (ImageView) root.findViewById(R.id.imageView3);
        btfoto=root.findViewById(R.id.camara);
        btfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(i.resolveActivity(getActivity().getPackageManager())!=null){
                    File photofile=null;
                    try {
                        photofile=createphoto();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(photofile !=null){
                        try {
                            photoUri= FileProvider.getUriForFile(getActivity(),getActivity().getPackageName(),photofile);
                            i.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                        }catch (Exception c){
                            c.printStackTrace();
                        }
                        startActivityForResult(i,PHOTO_CONST);
                    }
                }
            }
        });
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_CONST && resultCode == Activity.RESULT_OK ){
            //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String username = getResources().getString(Integer.parseInt("idUser"));
        Uri uri= Uri.parse(absolutePhotoPath);
        try {
            imageBitmap = BitmapFactory.decodeFile(uri.getPath());
            imageView.setImageURI(uri);
            D();
        }catch (Exception c){
            c.printStackTrace();
        }
       new Thread(new ClientThread(imageBitmap, username)).start();
    }
    }
    private void D(){
        try {
            // externalStorage
            String ExternalStorageDirectory = Environment.getExternalStorageDirectory() + File.separator;
            // uri de la imagen seleccionada
            Uri uri = Uri.fromFile(new File(ExternalStorageDirectory + "Download/imagenseleccionada.jpg"));
            //carpeta "imagenesguardadas"
            String rutacarpeta = "imagenesguardadas/";
            // nombre del nuevo png
            String timestamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            nombre= "nuevo"+timestamp+".png";
            // Compruebas si existe la carpeta "imagenesguardadas", sino, la crea
            File directorioImagenes = new File(ExternalStorageDirectory + rutacarpeta);
            if (!directorioImagenes.exists())
                directorioImagenes.mkdirs();
            // le pasas al bitmap la uri de la imagen seleccionada
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
            // pones las medidas que quieras del nuevo .png
            int bitmapWidth = bitmap.getWidth(); // para utilizar width de la imagen original: bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight(); // para utilizar height de la imagen original: bitmap.getHeight();
            Bitmap bitmapout = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, false);
            //creas el nuevo png en la nueva ruta
            bitmapout.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(ExternalStorageDirectory + rutacarpeta + nombre));
            // le pones parametros necesarios a la imagen para que se muestre en cualquier galería
            File filefinal = new File(ExternalStorageDirectory + rutacarpeta + nombre);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Titulo");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Descripción");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, filefinal.toString().toLowerCase(Locale.getDefault()).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, filefinal.getName().toLowerCase(Locale.getDefault()));
            values.put("_data", filefinal.getAbsolutePath());
            ContentResolver cr = getActivity().getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            //

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    private File createphoto() {
        String timestamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename= "imagen_";
        File photo=null;
        File storage= getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        try {
            photo = File.createTempFile(filename,".jpg",storage);
            absolutePhotoPath=photo.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }
}