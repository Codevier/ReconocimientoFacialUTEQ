package com.example.reconocimientofacialuteq.Clase;

import android.util.Log;

import androidx.annotation.NonNull;

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
        Log.d("TAG", "Notificacion : " + remoteMessage.getNotification().getBody());
        super.onMessageReceived(remoteMessage);
    }
    private void sendRegistrationToServer(String token) {
        //DatabaseReference reference= FirebaseDatabase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Token");
        myRef.child("Joss").setValue(token);
        //myRef.setValue(token);
        // TODO: Implement this method to send token to your app server.
    }
}
