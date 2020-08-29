package com.example.reconocimientofacialuteq.ui.login;

import android.app.Activity;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reconocimientofacialuteq.Clase.Logearse;
import com.example.reconocimientofacialuteq.MainActivity;
import com.example.reconocimientofacialuteq.MainActivity2;
import com.example.reconocimientofacialuteq.R;
import com.example.reconocimientofacialuteq.Socket.ClientThread;
import com.example.reconocimientofacialuteq.Socket.LoginThread;
import com.example.reconocimientofacialuteq.ui.login.LoginViewModel;
import com.example.reconocimientofacialuteq.ui.login.LoginViewModelFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private static final String IP = "192.168.1.15"; // Puedes cambiar a localhost
    private static final int PUERTO = 1100;
    private static final int SERVER_PORT = 5556;
    private static final String SERVER_IP = "192.168.1.15";
    private  Socket socket;
    private String usuario="null";
    private String clave="null";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                usuario=usernameEditText.getText().toString();
                clave=passwordEditText.getText().toString();
                /*
                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);

                 */
                /*
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                 */
                new Thread(new ClientThreadLog()).start();
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    class ClientThreadLog implements Runnable {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            try {
                String resp="";
                String idUser="";
                socket = new Socket(SERVER_IP, SERVER_PORT);
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                    objectOutputStream.writeObject(usuario);
                    objectOutputStream.writeObject(clave);
                    DataInputStream entrada = new DataInputStream(socket.getInputStream());
                    resp= (String) entrada.readUTF();
                    idUser= (String) entrada.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if("Ok".equals(resp)){
                    Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                    intent.putExtra("usuario", usuario);
                    intent.putExtra("idUser", idUser);
                    startActivity(intent);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

