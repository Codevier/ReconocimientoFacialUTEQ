package com.example.reconocimientofacialuteq.Clase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.reconocimientofacialuteq.NotificacionActivity;
import com.example.reconocimientofacialuteq.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        //Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("TAG", "Notificacion : " + remoteMessage.getFrom());
        if(remoteMessage.getNotification()!=null) {
            Log.d("TAG", "Titulo : " + remoteMessage.getNotification().getTitle());
            Log.d("TAG", "Cuerpo : " + remoteMessage.getNotification().getBody());
        }
        if(remoteMessage.getData().size()>0){
            String titulo,  cuerpo;
            titulo=remoteMessage.getData().get("titulo");
            cuerpo=remoteMessage.getData().get("detalle");
            Log.d("TAG", "Titulo : " + titulo);
            Log.d("TAG", "Detalle : " + cuerpo);
            Notificator(titulo,cuerpo);
        }
    }
    public void Notificator(String title, String cuerpo){

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NotificacionActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this,"Canal1")
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle(title)
                        .setContentText(cuerpo)
                        .setChannelId("CHANNEL_ID");
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, mBuilder.build());
    }
    private void sendRegistrationToServer(String token) {
        //DatabaseReference reference= FirebaseDatabase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Token");
        myRef.child("Xavier").setValue(token);
        //myRef.setValue(token);
        // TODO: Implement this method to send token to your app server.
    }
}
