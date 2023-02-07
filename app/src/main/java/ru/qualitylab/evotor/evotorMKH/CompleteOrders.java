package ru.qualitylab.evotor.evotorMKH;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class CompleteOrders extends AppCompatActivity {

    private TextView errorMessage;
    private ProgressBar loadingIndicator;
    RecyclerView completeOrders;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_orders);

        init();
        button();
        getData();

    }

    private void init() {
        completeOrders = findViewById(R.id.completeOrders);
        loadingIndicator = findViewById(R.id.progress_loading);
        errorMessage = findViewById(R.id.errorMessage);

        sharedPreferences = getSharedPreferences("App",0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        completeOrders.setLayoutManager(linearLayoutManager);
    }

    private void button() {

        findViewById(R.id.restartPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Restart request server
            getRequest();
            }
        });

        findViewById(R.id.order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompleteOrders.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        findViewById(R.id.account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompleteOrders.this, Account.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

    }

    private void getData() {
        String data = getSharedPreferences("App", MODE_PRIVATE).getString("CompleteOrders", "");

        if(data.length() > 0) {
            ArrayList<JSONObject> orders = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(data);

                for (int i = 0; i < jsonArray.length(); i++) {
                    orders.add(jsonArray.getJSONObject(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(orders , false,CompleteOrders.this);
            completeOrders.setAdapter(recyclerAdapter);

            showResult();
        } else {
            getRequest();
        }
    }

    private void getRequest() {

        URL generateUrl = null;
        generateUrl = Request.getOrdersUrl();
        new GetOrderTask().execute(generateUrl);
    }

    // Function for launch http request in a separate thread
    @SuppressLint("StaticFieldLeak")
    private class GetOrderTask extends AsyncTask<URL, Void, String> {

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
            ArrayList<JSONObject> task_array = new ArrayList<>();
            ArrayList<JSONObject> completed_array = new ArrayList<>();

            if (response != null && !response.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Задании");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject order = jsonArray.getJSONObject(i);
                        // Filter order for completed and not completed
                        if (order.getBoolean("orderPaid")) {

                            completed_array.add(order);

                        } else {

                            task_array.add(order);

                        }

                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("TaskOrders", String.valueOf(task_array));
                    editor.putString("CompleteOrders", String.valueOf(completed_array));
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getData();
            } else {
                showError();
            }
            loadingIndicator.setVisibility(View.INVISIBLE);
        }

    }

    // Func for show Result
    public void showResult() {
        completeOrders.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    // Func for show error message
    public void showError() {
        completeOrders.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }
}