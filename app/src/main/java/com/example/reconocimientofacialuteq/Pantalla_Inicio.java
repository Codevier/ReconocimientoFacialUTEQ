package com.example.reconocimientofacialuteq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reconocimientofacialuteq.ui.login.LoginActivity;

public class Pantalla_Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pantalla__inicio);
        // Agregar animaciones
        Animation animacion1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animacion2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        TextView deTextView = findViewById(R.id.txtJuntos);
        TextView codeliaTextView = findViewById(R.id.txtUTEQ);
        ImageView logoImageView = findViewById(R.id.imgImagenLogo);

        deTextView.setAnimation(animacion2);
        codeliaTextView.setAnimation(animacion2);
        logoImageView.setAnimation(animacion1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Pantalla_Inicio.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
        }
}