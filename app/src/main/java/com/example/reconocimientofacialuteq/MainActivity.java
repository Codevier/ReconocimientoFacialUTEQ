package com.example.reconocimientofacialuteq;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int RQS_OPEN_DOCUMENT_TREE = 1;

    ImageView imageView;
    TextView textInfo;
    private static Bitmap imageBitmap;
    private static final int PHOTO_CONST =1 ;

    Intent i;
    Socket miSockte;
    TextView respuesta;
    private Socket socket;
    String currentPhotoPath;
    Uri uriTree;
    String absolutePhotoPath;
    Uri photoUri;


    private static final int SERVERPORT = 5555;
    private static final String SERVER_IP = "192.168.1.15";
    static final int REQUEST_TAKE_PHOTO = 1;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    Button btfoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<String> permisos = new ArrayList<String>();
        permisos.add(Manifest.permission.CAMERA);
        permisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permisos.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permisos.add(Manifest.permission.WRITE_CALENDAR);
        imageView = (ImageView) findViewById(R.id.imageView);
        textInfo = (TextView) findViewById(R.id.info);
        getPermission(permisos);
        btfoto=findViewById(R.id.camara);
        btfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
                //guardarFoto(v);
            }
        });
    }
    private void takePhoto(){
        i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(i.resolveActivity(getPackageManager())!=null){
            File photofile=null;
            try {
                photofile=createphoto();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(photofile !=null){
                try {
                    photoUri= FileProvider.getUriForFile(this,getPackageName(),photofile);
                    i.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                }catch (Exception c){
                    c.printStackTrace();
                }

                startActivityForResult(i,PHOTO_CONST);
            }
        }
    }
    private File createphoto() {
        String timestamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename= "imagen_";
        File photo=null;
        File storage= getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        try {
            photo = File.createTempFile(filename,".jpg",storage);
            absolutePhotoPath=photo.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }
    public void getPermission(ArrayList<String> permisosSolicitados) {

        ArrayList<String> listPermisosNOAprob = getPermisosNoAprobados(permisosSolicitados);
        if (listPermisosNOAprob.size() > 0)
            if (Build.VERSION.SDK_INT >= 23)
                requestPermissions(listPermisosNOAprob.toArray(new String[listPermisosNOAprob.size()]), 1);

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
            String nombre = "nuevo.png";

            // Compruebas si existe la carpeta "imagenesguardadas", sino, la crea
            File directorioImagenes = new File(ExternalStorageDirectory + rutacarpeta);
            if (!directorioImagenes.exists())
                directorioImagenes.mkdirs();

            // le pasas al bitmap la uri de la imagen seleccionada
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
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
            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            //

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    public ArrayList<String> getPermisosNoAprobados(ArrayList<String> listaPermisos) {
        ArrayList<String> list = new ArrayList<String>();
        for (String permiso : listaPermisos) {
            if (Build.VERSION.SDK_INT >= 23)
                if (checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED)
                    list.add(permiso);
        }
        return list;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String s = "";
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    s = s + "OK " + permissions[i] + "\n";
                else
                    s = s + "NO  " + permissions[i] + "\n";
            }
            Toast.makeText(this.getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }

    public void CargarFotoGaleria(View view){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    new Thread(new ClientThread()).start();
                    imageView.setImageBitmap(imageBitmap);
                    break;
                case PICK_IMAGE:
                    imageUri = data.getData();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageURI(imageUri);
                    new Thread(new ClientThread()).start();
                    break;
                case PHOTO_CONST:
                    Uri uri= Uri.parse(absolutePhotoPath);
                    try {
                        imageView.setImageURI(uri);
                        D();
                    }catch (Exception c){
                        c.printStackTrace();
                    }
                    break;

            }
        }
    }
    class ClientThread implements Runnable {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            try {
                socket = new Socket(SERVER_IP, SERVERPORT);
                DataOutputStream salida;

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String mens="nombre";
                TextView salidaTextView = (TextView) findViewById(R.id.textView2);
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                    objectOutputStream.writeObject(byteArray);
                    objectOutputStream.writeObject(mens);
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }
}