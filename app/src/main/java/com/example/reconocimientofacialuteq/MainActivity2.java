package com.example.reconocimientofacialuteq;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reconocimientofacialuteq.Clase.Servidor;
import com.example.reconocimientofacialuteq.ui.login.LoginActivity;
import com.example.reconocimientofacialuteq.ui.misdatos.MisDatos;
import com.example.reconocimientofacialuteq.ui.misdatos.Mis_Datos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private  Socket socket;
    NotificationCompat.Builder mBuilder;
    NotificationCompat.Builder notificacion;
    NotificationManagerCompat notificationManagerCompat;

    private NotificationManager notifManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ArrayList<String> permisos = new ArrayList<String>();
        permisos.add(Manifest.permission.CAMERA);
        permisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permisos.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permisos.add(Manifest.permission.WRITE_CALENDAR);
        getPermission(permisos);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = findViewById(R.id.fab);
        //imageView = (ImageView) findViewById(R.id.imageGaleria);
        //notificationManagerCompat = NotificationManagerCompat.from(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNotification("hola",MainActivity2.this);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_misdatos)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //new Thread(new NotificacionThread()).start();
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

    public String Usuario(){
        SharedPreferences sharedPreferences2= getSharedPreferences("Login",MODE_PRIVATE);
        return sharedPreferences2.getString("IdUser","0");
    }

    public void createNotification(String aMessage, Context context) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = context.getString(R.string.default_notification_channel_id); // default_channel_id
        String title = context.getString(R.string.default_notification_channel_title); // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }

    public void getPermission(ArrayList<String> permisosSolicitados) {

        ArrayList<String> listPermisosNOAprob = getPermisosNoAprobados(permisosSolicitados);
        if (listPermisosNOAprob.size() > 0)
            if (Build.VERSION.SDK_INT >= 23)
                requestPermissions(listPermisosNOAprob.toArray(new String[listPermisosNOAprob.size()]), 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            SharedPreferences sharedPreferences;
            sharedPreferences= getSharedPreferences("Login",MODE_PRIVATE);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.remove("Logeado");
            editor.commit();
            Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if(id==R.id.datos){
            Intent intent = new Intent(MainActivity2.this, MisDatos.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //SharedPreferences sharedPreferences2= getSharedPreferences("Login",MODE_PRIVATE);
       // String idUser =sharedPreferences2.getString("IdUser","0");
        //data.putExtra("idUser", idUser);
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void Notificar(){

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity2.class), PendingIntent.FLAG_UPDATE_CURRENT);
        /*
        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Titulo")
                .setContentText("Xavier equis de")
                .setVibrate(new long[] {100, 250, 100, 500})
                .setAutoCancel(true);

         */
        /*
        mBuilder=  new NotificationCompat.Builder(this,"Canal1")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Tutlane Send New Message")
                .setContentText("Hi, Welcome to tutlane tutorial site");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

         */

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this,"Canal1")
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setChannelId("CHANNEL_ID");
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, mBuilder.build());
        // notificationId is a unique int for each notification that you must define


    }
    class NotificacionThread implements Runnable{
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            try {
                socket = new Socket(Servidor.IpServidor, Servidor.PuertoNotificacion);
                //DataInputStream entrada = new DataInputStream(socket.getInputStream());
                //String notificacion;
                int notificacion;
                while (true){
                    try (DataInputStream objectOutputStream = new DataInputStream(socket.getInputStream())){
                        //notificacion= objectOutputStream.readInt();
                        //String anotificacion=
                        objectOutputStream.readUTF();
                        createNotification("notificacion",MainActivity2.this);
                    }
                }
            } catch (IOException c) {
                c.printStackTrace();
                createNotification("no recibe bien la notificacion ", MainActivity2.this);
            }
        }
    }

}