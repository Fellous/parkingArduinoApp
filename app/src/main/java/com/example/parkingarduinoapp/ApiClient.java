package com.example.parkingarduinoapp;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:3000"; // URL du backend

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

        client.newCall(request).enqueue(callback);
    }
    // Nouvelle méthode pour les requêtes GET
    public static void get(String endpoint, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }
}
