package com.example.reconocimientofacialuteq.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.reconocimientofacialuteq.Clase.Servidor;
import com.example.reconocimientofacialuteq.data.model.LoggedInUser;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        LoggedInUser fakeUser=null;
        try {
            String resp="Sin respuesta";
            String idUser="0";
            fakeUser =
                    new LoggedInUser(
                            idUser,
                            username);
            // TODO: handle loggedInUser authentication

            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}