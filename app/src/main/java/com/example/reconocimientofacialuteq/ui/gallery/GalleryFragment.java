package com.example.reconocimientofacialuteq.ui.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.reconocimientofacialuteq.clase.Servidor;
import com.example.reconocimientofacialuteq.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment {

    private static final int PICK_IMAGE = 100;
    String timestamp;
    ImageView imageView;
    Uri imageUri;
    private static Bitmap imageBitmap;
    Button btnGaleria,btnNotificar;
    RequestQueue requestQueue;
    private  StorageReference storageReference;
    String nombre_imagen;
    static private Uri downloadUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        imageView= (ImageView) root.findViewById(R.id.imageGaleria);
        btnGaleria=(Button) root.findViewById(R.id.buttonGalllery);
        btnNotificar=(Button) root.findViewById(R.id.btnNotificar);
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("");
        storageReference = FirebaseStorage.getInstance().getReference().child("reconocimientos");
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
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
        private  Bitmap bitmap;


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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
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
