package ru.qualitylab.evotor.evotorMKH;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class Auth extends AppCompatActivity {

    private EditText loginEdit;
    private EditText passwordEdit;
    public static ProgressBar loadingIndicator;

    private SharedPreferences sharedPreferences;
    private String login;
    private String password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        checkAuth();

        init();
        button();

    }

    private void checkAuth() {
        String auth = getSharedPreferences("App", MODE_PRIVATE).getString("User", "");

        if(auth.length() > 0) {
            Intent intent = new Intent(Auth.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void init() {
        loadingIndicator = findViewById(R.id.progress_loading);
        loginEdit = findViewById(R.id.loginText);
        passwordEdit = findViewById(R.id.passwordText);

        sharedPreferences = getSharedPreferences("App", 0);

    }

    private void button() {
        findViewById(R.id.authorization).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRequestAuth();
            }
        });

    }


    private void getRequestAuth() {
        login = loginEdit.getText().toString();
        password = passwordEdit.getText().toString();

        if (login.length() != 0 && password.length() != 0) {
            URL generateUrl = Request.authRequestUrl(login, password);
            new GetAuth().execute(generateUrl);
        } else {
            Toast.makeText(this, "Заполните поля для авторизации!", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class GetAuth extends AsyncTask<URL, Void, String> {

        // Showing animation before receiving a response
        @Override
        protected void onPreExecute() {
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        // Function call http request
        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = Request.getRequest(urls[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        // Working with the received data
        @Override
        protected void onPostExecute(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONObject order = jsonArray.getJSONObject(0);
                    checkStatusAuthorize(order);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            loadingIndicator.setVisibility(View.INVISIBLE);
        }

    }

    private void checkStatusAuthorize(JSONObject data) throws JSONException {
        if (data.getString("СтатусАвторизации").equals("Ошибка")) {
            Toast.makeText(this, "Неверный логин или пароль!", Toast.LENGTH_SHORT).show();
        } else if (data.getString("СтатусАвторизации").equals("Успешно")) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("User", String.valueOf(data));
            editor.apply();

            Intent intent = new Intent(Auth.this, MainActivity.class);
            startActivity(intent);
        }
    }

}