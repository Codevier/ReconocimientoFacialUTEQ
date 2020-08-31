package com.example.reconocimientofacialuteq;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Notificacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion);
        TextView textView =findViewById(R.id.textviNOT);
        String message= getIntent().getStringExtra("message");
        textView.setText(message);
    }
}