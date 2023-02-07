package ru.qualitylab.evotor.evotorMKH;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Account extends AppCompatActivity {

    private TextView name;
    private TextView login;
    private TextView position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        init();
        button();

        try {
            getData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void init() {
        name = findViewById(R.id.nameUser);
        login = findViewById(R.id.loginUser);
        position = findViewById(R.id.positionUser);
    }

    private void button() {
        findViewById(R.id.exitAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitAccount();
//                test();
            }
        });

        findViewById(R.id.order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Account.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        findViewById(R.id.completeOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Account.this, CompleteOrders.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    private void exitAccount() {
        Intent intent = new Intent(Account.this, Auth.class);
        getSharedPreferences("App",0).edit().clear().apply();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void getData() throws JSONException {
        String data = getSharedPreferences("App",0).getString("User","0");
        JSONObject response = null;
        if (data != null) {
            try {
                response = new JSONObject(data);
            } catch (JSONException e) {

            }
        }

        name.setText(response.getString("name"));
        login.setText(response.getString("code"));
        position.setText(response.getString("post"));
    }

    private void test() {
        String data = getSharedPreferences("App", MODE_PRIVATE).getString("CompleteOrders", "");
        String data2 = getSharedPreferences("App", MODE_PRIVATE).getString("TaskOrders", "");
        JSONArray jsonArray = null;
        JSONArray jsonArray2 = null;
        JSONObject item = null;
        if(data.length() > 0) {
            try {
                jsonArray = new JSONArray(data);
                jsonArray2 = new JSONArray(data2);
                for (int i = 0; i < jsonArray.length(); i++) {

                    if (jsonArray.getJSONObject(i).getString("УИД").equals("7302a9a2-9a34-11ed-933b-000c2914021f")) {
                        Log.i("Ooppps",jsonArray.getJSONObject(i).getString("Контрагент"));
                        item = jsonArray.getJSONObject(i);
                        jsonArray.remove(i);
//                        jsonArray.getJSONObject(i).remove("orderPaid");
//                        jsonArray.getJSONObject(i).put("MKH","false");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            getSharedPreferences("App", MODE_PRIVATE).edit().remove("CompleteOrders").apply();
            SharedPreferences.Editor editor = getSharedPreferences("App",0).edit();
            editor.putString("CompleteOrders", String.valueOf(jsonArray)).apply();
//
//            jsonArray2.
//            getSharedPreferences("App", MODE_PRIVATE).edit().remove("TaskOrders").apply();
//            SharedPreferences.Editor editor2 = getSharedPreferences("App",0).edit();
//            editor.putString("CompleteOrders", String.valueOf(jsonArray)).apply();

        } else {
            Log.i("00000", "ERROR");
        }
    }

}