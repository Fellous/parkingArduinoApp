package com.example.parkingarduinoapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.graphics.Color;


public class DashboardActivity extends AppCompatActivity {

    private TextView spot1;
    private TextView spot2;
    private TextView spot3;
    private TextView spot4;
    private TextView availableSpots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        spot1 = findViewById(R.id.spot_1);
        spot2 = findViewById(R.id.spot_2);
        spot3 = findViewById(R.id.spot_3);
        spot4 = findViewById(R.id.spot_4);
        availableSpots = findViewById(R.id.available_spots);

        updateParkingSpots();
    }

    private void updateParkingSpots() {
        ApiClient.get("/parkingSpots", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);
                            int available = 4;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject spot = jsonArray.getJSONObject(i);
                                boolean isOccupied = spot.getBoolean("is_occupied");

                                if (i == 0) {
                                    updateSpot(spot1, isOccupied);
                                } else if (i == 1) {
                                    updateSpot(spot2, isOccupied);
                                } else if (i == 2) {
                                    updateSpot(spot3, isOccupied);
                                } else if (i == 3) {
                                    updateSpot(spot4, isOccupied);
                                }

                                if (isOccupied) {
                                    available--;
                                }
                            }

                            availableSpots.setText(available + "/4 places disponibles");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private void updateSpot(TextView spot, boolean isOccupied) {
        if (isOccupied) {
            spot.setBackgroundColor(Color.RED);
            spot.setTextColor(Color.WHITE);
        } else {
            spot.setBackgroundColor(Color.GREEN);
            spot.setTextColor(Color.BLACK);
        }
    }
}
