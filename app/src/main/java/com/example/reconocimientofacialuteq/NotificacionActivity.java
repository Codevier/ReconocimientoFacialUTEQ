package com.example.reconocimientofacialuteq;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

public class NotificacionActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion2);
        imageView= (ImageView) findViewById(R.id.imageNotification);
        Bundle bundle = getIntent().getExtras();
        String bitmapSstring= bundle.getString("img");
        Bitmap bitmap=StringToBitMap(bitmapSstring);
        imageView.setImageBitmap(bitmap);
    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}