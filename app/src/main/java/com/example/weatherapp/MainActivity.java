package com.example.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.R;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView cityTextView, tempTextView, descTextView;
    private Button fetchWeatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTextView = findViewById(R.id.cityTextView);
        tempTextView = findViewById(R.id.tempTextView);
        descTextView = findViewById(R.id.descTextView);
        fetchWeatherButton = findViewById(R.id.fetchWeatherButton);

        fetchWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchWeatherTask().execute("Yogyakarta");
            }
        });
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String apiKey = "19a90ba0a52b486fe30687a9f3d6788f"; //Sesuaikan API nya
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                in.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String cityName = jsonObject.getString("name");
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                cityTextView.setText(cityName);
                tempTextView.setText("Temperature: " + temperature + "°C");
                descTextView.setText("Description: " + description);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
