package com.example.reconocimientofacialuteq.ui.home;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.reconocimientofacialuteq.Clase.Servidor;
import com.example.reconocimientofacialuteq.MainActivity;
import com.example.reconocimientofacialuteq.R;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    String nombre_imagen;
    private  StorageReference storageReference;
    ImageView imageView;
    Button btfoto,btnNotificar;
    String timestamp;
    static private Uri downloadUri;
    Intent i;
    Uri photoUri;
    String absolutePhotoPath;
    String nombre;
    private static final int PHOTO_CONST =1 ;
    private static Bitmap imageBitmap;
    View root;
    RequestQueue requestQueue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        imageView = (ImageView) root.findViewById(R.id.imageView3);
        btnNotificar=(Button) root.findViewById(R.id.notificarbtn);
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
        btnNotificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject jsonObject=new JSONObject();
                    String topic="general";
                    jsonObject.put( "to","/topics/"+topic);
                    JSONObject notificacion = new JSONObject();
                    notificacion.put("titulo","Alerta");
                    notificacion.put("detalle","Persona no identificada");
                    notificacion.put("img",downloadUri.toString());
                    jsonObject.put("data",notificacion);
                    String URL="https://fcm.googleapis.com/fcm/send";
                    JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.POST,URL,jsonObject,null,null){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String,String> header= new HashMap<>();
                            header.put("content-type","application/json");
                            header.put("authorization","key=AAAArZOL960:APA91bFPOOGosYRGBmclbjJJfBwFnij04ZKg5enyUuGVr2zrEh2s1V3d7qwXno2PE_PgiS-oM16FH2X0NKZsSv-OafCkCx-v4jhmOv9WJ4r8pJhWV1pOLgLu5GwO8z2DBt_yBaQy9N0P");
                            return  header;
                        }
                    };
                    requestQueue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        //Notificacion();
        return root;
    }
    public void Notificacion(byte[] img){
        final StorageReference ref = storageReference.child("reconocimiento/"+nombre_imagen);
        UploadTask uploadTask = ref.putBytes(img);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(),"Se subio a firebase la imagen",Toast.LENGTH_SHORT).show();
                    downloadUri= task.getResult();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_CONST && resultCode == Activity.RESULT_OK ){
            //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            //String username = getResources().getString(Integer.parseInt("idUser"))
            String userId="1";//data.getExtras().getString("idUser");
        Uri uri= Uri.parse(absolutePhotoPath);
        try {

            imageBitmap = BitmapFactory.decodeFile(uri.getPath());
            imageView.setImageURI(uri);
            D();
        }catch (Exception c){
            c.printStackTrace();
        }
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            imageBitmap= Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            new Thread(new ClientThread(imageBitmap, userId)).start();
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
            nombre= "nuevo"+timestamp+".jpg";
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
    class ClientThread implements Runnable {
        private static final int SERVERPORT = 5555;
        private static final String SERVER_IP = "192.168.0.102";
        private Socket socket;
        private  String dim;
        private byte[] message;
        String user;
        private  Bitmap bitmap;


        public ClientThread(Bitmap bitmap, String user) {
            this.bitmap=bitmap;
            this.user=user;
            timestamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        public String BitMapToString(Bitmap bitmap){
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            String temp= Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
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
                Bitmap.CompressFormat Bitmap;
                //TextView salidaTextView = (TextView) findViewById(R.id.textView2);
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                    objectOutputStream.writeObject(byteArray);
                    objectOutputStream.writeObject(user);
                    objectOutputStream.writeObject(user+timestamp+".jpg");
                    nombre_imagen=user+timestamp+".jpg";
                    DataInputStream entrada = new DataInputStream(socket.getInputStream());
                    dim=  entrada.readUTF();
                    message=new byte[Integer.parseInt(dim)];
                    for( int i = 0; i < message.length; i++ )
                    {
                        message[ i ] = ( byte )entrada.read( );
                    }
                    bitmap = BitmapFactory.decodeByteArray(message, 0, message.length);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);

                        }
                    });
                    //ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
                    //StorageReference storageReference2= storageReference.child("");
                    //String imagenBitMap=BitMapToString(bitmap);

                    Notificacion(message);
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