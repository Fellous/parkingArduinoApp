package com.example.parkingarduinoapp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.util.Log;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.251.111:3000";
    private static OkHttpClient client;

    public ApiClient() {
        this.client = new OkHttpClient();
    }

    public void login(String username, String password, Callback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(BASE_URL + "/users/login")
                .post(body)
                .build();

        Log.d("ApiClient", "Sending login request to: " + BASE_URL + "/users/login");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ApiClient", "Login request failed", e);
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("ApiClient", "Login request succeeded: " + responseData);
                    callback.onResponse(call, response);
                } else {
                    Log.e("ApiClient", "Login request failed: " + response.message());
                    callback.onFailure(call, new IOException("Unexpected code " + response));
                }
            }
        });
    }

    public static void get(String endpoint, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get()
                .build();

        Log.d("ApiClient", "Sending GET request to: " + BASE_URL + endpoint);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ApiClient", "GET request failed", e);
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("ApiClient", "GET request succeeded: " + responseData);
                    callback.onResponse(call, response);
                } else {
                    Log.e("ApiClient", "GET request failed: " + response.message());
                    callback.onFailure(call, new IOException("Unexpected code " + response));
                }
            }
        });
    }
}
