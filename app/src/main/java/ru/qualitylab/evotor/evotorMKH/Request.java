package ru.qualitylab.evotor.evotorMKH;

import static ru.qualitylab.evotor.evotorMKH.Auth.loadingIndicator;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Request {

    private static final String BASE = "http://api.kirgu.ru/";
    private static final String GET_ORDERS = "staff/api/dostavka/logistics";
    private static final String SEND_ORDER = "?";
    private static final String AUTHORIZE = "http://public.kirgu.ru/mobile/hs/exchangemobileapp/auth/";  // %D0%A3  У000072161/40036";
    private static final String MKH_TEST = "https://mkh-production.ru/orders.json";


// Generate url address for connection
    public static URL MMMgetOrdersUrl(String login_UT) {

        // General url address
        Uri buildUrl = Uri.parse(BASE + GET_ORDERS)
                .buildUpon()
                .appendQueryParameter("code", login_UT)
                .build();

        URL url = null;

        try {
            url = new URL(buildUrl.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL sendPaidOrdersUrl(String login ,String uidOrder) {

        // General url address
        Uri buildUrl = Uri.parse(BASE + "?")
                .buildUpon()
                .appendQueryParameter("login", login)
                .appendQueryParameter("uidOrder", uidOrder)
                .build();

        URL url = null;

        try {
            url = new URL(buildUrl.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL authRequestUrl(String login, String password) {

        URI uri = null;
        try {
            URL url = new URL(BASE + "api/1c-proxy" + "?url=" + AUTHORIZE + login + "/" + password);
            uri = new URI(url.getProtocol(), url.getUserInfo(), IDN.toASCII(url.getHost()), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String correctEncodedURL = uri.toASCIIString();

        Uri buildUrl = Uri.parse(correctEncodedURL)
                .buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(buildUrl.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL getOrdersUrl() {

        // General url address
        Uri buildUrl = Uri.parse(MKH_TEST)
                .buildUpon()
                .build();

        URL url = null;

        try {
            url = new URL(buildUrl.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL getTestUrl() {

        // General url address
//        Uri buildUrl = Uri.parse("http://192.168.50.235:8000")
        Uri buildUrl = Uri.parse(BASE + GET_ORDERS)
                .buildUpon()
                .appendQueryParameter("code","000000001")
                .build();

        URL url = null;

        try {
            url = new URL(buildUrl.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }



// Network Request
    public static String getRequest(URL url) throws IOException {

        HttpURLConnection urlConnection = null;

        try {
            // Open connect http connect
            urlConnection = (HttpURLConnection) url.openConnection();

            // Reading the  server response
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            Log.i("NetworkRequest", "INTERNET SUCCESS CONNECT");

            // Check server response
            if (hasInput) {
                return scanner.next();
            } else {
                Log.e(Request.class.getSimpleName(), "ERROR reading data server!!!");
                return null;
            }

        } catch (UnknownHostException e) {
            Log.e(Request.class.getSimpleName(), "ERROR get request");
            return null;
        } finally {
            // Close connect http
            urlConnection.disconnect();
        }
    }

    public static String postRequest(URL url, String data) throws IOException {

        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            // Post request setting
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            // Preparation json order for send "paidOrder"
            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = data.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Reading the  server response
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            // Check server response
            if (hasInput) {
                Log.e(Request.class.getSimpleName(), "ERROR reading response server");
                return scanner.next();
            } else {
                return null;
            }

        } catch (UnknownHostException e) {
            Log.e(Request.class.getSimpleName(), "ERROR post request");
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }



// Saving request data

//    public JSONArray getRequest(RecyclerView list, TextView error, ProgressBar progressBar) {
//
//        GetOrderTask getOrderTask = new GetOrderTask();
//        URL generateUrl = null;
//        generateUrl = Request.getOrdersUrl();
//        getOrderTask.setProps(list, error, progressBar);
//        getOrderTask.execute(generateUrl);
//        return getOrderTask.getData();
//    }
//
//    // Function for launch http request in a separate thread
//    @SuppressLint("StaticFieldLeak")
//    private static class GetOrderTask extends AsyncTask<URL, Void, String> {
//        private JSONArray jsonArray;
//        private TextView errorMessage;
//        private RecyclerView listItem;
//        private ProgressBar loadingIndicator;
//
//        public void setProps(RecyclerView list, TextView error, ProgressBar progressBar) {
//            listItem = list;
//            errorMessage = error;
//            loadingIndicator = progressBar;
//        }
//
//        public JSONArray getData() {
//            return jsonArray;
//        }
//        // Showing animation before receiving a response
//        @Override
//        protected void onPreExecute() {
//            loadingIndicator.setVisibility(View.VISIBLE);
//        }
//
//        // Function call http request
//        @Override
//        protected String doInBackground(URL... urls) {
//            String response = null;
//            try {
//                response = Request.getRequest(urls[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return response;
//        }
//
//        // Working with the received data
//        @Override
//        protected void onPostExecute(String response) {
//
//            if (response != null && !response.equals("")) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    jsonArray = jsonObject.getJSONArray("Задании");
//                    Log.i("00", String.valueOf(jsonObject));
//                    Log.i("11", String.valueOf(jsonArray));
////                    for (int i = 0; i < jsonArray.length(); i++) {
////
////                        JSONObject order = jsonArray.getJSONObject(i);
////                        Filter order for completed and not completed
////                        task_array.add(order);
////                        filterOrder(order);
////
////                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                // Func call set list data
////                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(task_array , false,MainActivity.this);
////                listAddress.setAdapter(recyclerAdapter);
//
//                listItem.setVisibility(View.VISIBLE);
//                errorMessage.setVisibility(View.INVISIBLE);
//            } else {
//                listItem.setVisibility(View.INVISIBLE);
//                errorMessage.setVisibility(View.VISIBLE);
//            }
//            loadingIndicator.setVisibility(View.INVISIBLE);
//        }
//
//    }

}
