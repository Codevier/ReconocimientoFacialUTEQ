package com.example.reconocimientofacialuteq.ui.misdatos;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reconocimientofacialuteq.Clase.Servidor;
import com.example.reconocimientofacialuteq.R;
import com.example.reconocimientofacialuteq.Socket.ClientThread;
import com.example.reconocimientofacialuteq.ui.gallery.GalleryViewModel;
import com.example.reconocimientofacialuteq.ui.home.HomeViewModel;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MisDatos extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private  Socket socket;
    private TextView correo, nombres;
    private String id_us="1";
    private byte[] imagen;
    private HomeViewModel homeViewModel;
    private ImageView img,cambiarimg,cargarf;
    private Bitmap bitmap;
    ProgressDialog progDailog;
    private GalleryViewModel galleryViewModel;
    Dialog dg;
    String absolutePhotoPath;
    Uri photoUri;
    private static final int PHOTO_CONST =1 ;
    private static Bitmap imageBitmap;
    String nombre;
    Uri imageUri;
    @SuppressLint("WrongViewCast")
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

        cargarf=  findViewById(R.id.cargarFoto);

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
    public void cargarFoto(View view){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    private File createphoto() {
        String filename= "imagen_";
        File photo=null;
        File storage= this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        try {
            photo = File.createTempFile(filename,".jpg",storage);
            absolutePhotoPath=photo.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return photo;
    }
    public void tomarFoto(View view){
        Intent i;
        i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(i.resolveActivity(this.getPackageManager())!=null){
            File photofile=null;
            try {
                photofile=createphoto();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(photofile !=null){
                try {
                    photoUri= FileProvider.getUriForFile(this,this.getPackageName(),photofile);
                    i.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                }catch (Exception c){
                    c.printStackTrace();
                }
                startActivityForResult(i,PHOTO_CONST);
            }
        }
    }
    public void close(View view){
        dg.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null )
        {
            String usuario ="1";// data.getExtras().getString("idUser");

            imageUri = data.getData();
            img.setImageURI(imageUri);
            dg.dismiss();
            Toast.makeText(this, "Imagen cargada de galeria", Toast.LENGTH_SHORT).show();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(requestCode == PHOTO_CONST && resultCode == Activity.RESULT_OK ){
            //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            //String username = getResources().getString(Integer.parseInt("idUser"))
            String userId="1";//data.getExtras().getString("idUser");
            Uri uri= Uri.parse(absolutePhotoPath);
            try {
                imageBitmap = BitmapFactory.decodeFile(uri.getPath());
                img.setImageURI(uri);
                D();
            }catch (Exception c){
                c.printStackTrace();
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            imageBitmap= Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            dg.dismiss();
            Toast.makeText(this, "Imagen cargada de galeria", Toast.LENGTH_SHORT).show();
        }

        }
    private void D() {
        try {
            // externalStorage
            String ExternalStorageDirectory = Environment.getExternalStorageDirectory() + File.separator;
            // uri de la imagen seleccionada
            Uri uri = Uri.fromFile(new File(ExternalStorageDirectory + "Download/imagenseleccionada.jpg"));
            //carpeta "imagenesguardadas"
            String rutacarpeta = "imagenesguardadas/";
            // nombre del nuevo png
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            nombre = "nuevo" + timestamp + ".jpg";
            // Compruebas si existe la carpeta "imagenesguardadas", sino, la crea
            File directorioImagenes = new File(ExternalStorageDirectory + rutacarpeta);
            if (!directorioImagenes.exists())
                directorioImagenes.mkdirs();
            // le pasas al bitmap la uri de la imagen seleccionada
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
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
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, filefinal.toString().toLowerCase(Locale.getDefault()).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, filefinal.getName().toLowerCase(Locale.getDefault()));
            values.put("_data", filefinal.getAbsolutePath());
            ContentResolver cr = this.getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            //

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
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
                            progDailog.dismiss();
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