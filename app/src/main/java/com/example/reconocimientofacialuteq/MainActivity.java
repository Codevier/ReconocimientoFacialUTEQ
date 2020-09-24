package com.example.reconocimientofacialuteq;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.example.reconocimientofacialuteq.ui.login.LoginActivity;
import com.example.reconocimientofacialuteq.ui.misdatos.MisDatos;
import com.google.android.material.navigation.NavigationView;

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

import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_misdatos)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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

    public void getPermission(ArrayList<String> permisosSolicitados) {

        ArrayList<String> listPermisosNOAprob = getPermisosNoAprobados(permisosSolicitados);
        if (listPermisosNOAprob.size() > 0)
            if (Build.VERSION.SDK_INT >= 23)
                requestPermissions(listPermisosNOAprob.toArray(new String[listPermisosNOAprob.size()]), 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if(id==R.id.datos){
            Intent intent = new Intent(MainActivity.this, MisDatos.class);
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
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}