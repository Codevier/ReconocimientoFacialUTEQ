package com.example.reconocimientofacialuteq;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class NotificationActivity extends AppCompatActivity {
    ImageView imageView;
    ProgressDialog progDailog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion);
        imageView= (ImageView) findViewById(R.id.img_not);
        Bundle bundle = getIntent().getExtras();
        String uri= bundle.getString("img");
        Log.d("TAG", "Uri : " + uri);
        Log.d("TAG", "Uri 2 : " + Uri.parse(uri));
        progDailog = new ProgressDialog(NotificationActivity.this);
        progDailog.setMessage("Cargando datos...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
        Picasso.with(this)
                .load(uri)
                .into(imageView);
        progDailog.dismiss();
    }
}