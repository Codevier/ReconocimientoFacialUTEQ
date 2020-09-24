package com.example.reconocimientofacialuteq;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Notificacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion);
        Bundle bundle = getIntent().getExtras();
        String uri= bundle.getString("img");
        Log.d("TAG", "Uri : " + uri);
        Log.d("TAG", "Uri 2 : " + Uri.parse(uri));
       /* TextView textView =findViewById(R.id.textviNOT);
        String message= getIntent().getStringExtra("message");
        textView.setText(message);*/
    }
}