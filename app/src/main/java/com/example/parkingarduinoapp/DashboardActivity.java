package com.example.parkingarduinoapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardActivity extends AppCompatActivity {

    private CardView spot1, spot2, spot3, spot4;
    private TextView availableSpots, parkingComplete;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        spot1 = findViewById(R.id.spot_1);
        spot2 = findViewById(R.id.spot_2);
        spot3 = findViewById(R.id.spot_3);
        spot4 = findViewById(R.id.spot_4);
        availableSpots = findViewById(R.id.available_spots);
        parkingComplete = findViewById(R.id.parking_complete);

        handler = new Handler();

        // Créer un Runnable qui sera exécuté périodiquement
        runnable = new Runnable() {
            @Override
            public void run() {
                updateParkingStatus();
                // Répéter cette tâche toutes les 5 secondes
                handler.postDelayed(this, 1000);
            }
        };

        // Démarrer la tâche périodique
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Arrêter la tâche périodique lorsque l'activité est détruite
        handler.removeCallbacks(runnable);
    }

    private void updateParkingStatus() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.251.111:3000/parking_spots")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    DashboardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(responseData);
                                int freeSpots = 0;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int spotNumber = jsonObject.getInt("spot_number");
                                    boolean isOccupied = jsonObject.getInt("is_occupied") == 1;
                                    updateSpot(spotNumber, isOccupied);
                                    if (!isOccupied) {
                                        freeSpots++;
                                    }
                                }
                                if (freeSpots == 0) {
                                    availableSpots.setText("0/4 places disponibles");
                                    availableSpots.setTextColor(Color.RED);
                                    parkingComplete.setVisibility(TextView.VISIBLE);
                                } else {
                                    availableSpots.setText(freeSpots + "/4 places disponibles");
                                    availableSpots.setTextColor(Color.WHITE);
                                    parkingComplete.setVisibility(TextView.GONE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateSpot(int spotNumber, boolean isOccupied) {
        CardView spot;
        switch (spotNumber) {
            case 1:
                spot = spot1;
                break;
            case 2:
                spot = spot2;
                break;
            case 3:
                spot = spot3;
                break;
            case 4:
                spot = spot4;
                break;
            default:
                return;
        }

        if (isOccupied) {
            spot.setCardBackgroundColor(Color.parseColor("#F44336")); // Occupied (red)
        } else {
            spot.setCardBackgroundColor(Color.parseColor("#4CAF50")); // Available (green)
        }
    }
}
