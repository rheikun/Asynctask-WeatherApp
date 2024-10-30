package com.example.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private EditText cityEditText;
    private TextView cityTextView, tempTextView, descTextView;
    private TextView windSpeedTextView, humidityTextView, pressureTextView, sunriseTextView, sunsetTextView;
    private Button fetchWeatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi semua komponen
        cityEditText = findViewById(R.id.cityEditText);
        cityTextView = findViewById(R.id.cityTextView);
        tempTextView = findViewById(R.id.tempTextView);
        descTextView = findViewById(R.id.descTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        sunriseTextView = findViewById(R.id.sunriseTextView);
        sunsetTextView = findViewById(R.id.sunsetTextView);
        fetchWeatherButton = findViewById(R.id.fetchWeatherButton);


        // Set Listener untuk tombol
        fetchWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil nama kota dari EditText
                String city = cityEditText.getText().toString().trim();
                if (!city.isEmpty()) {
                    new FetchWeatherTask().execute(city);
                } else {
                    cityTextView.setText("Please enter a city name.");
                }
            }
        });
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String apiKey = "19a90ba0a52b486fe30687a9f3d6788f"; // Ganti dengan API key Anda
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
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String cityName = jsonObject.getString("name");

                    // Informasi suhu dan deskripsi cuaca
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temperature = main.getDouble("temp");
                    String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                    // Koordinat
                    JSONObject coord = jsonObject.getJSONObject("coord");
                    double latitude = coord.getDouble("lat");
                    double longitude = coord.getDouble("lon");

                    // Kecepatan angin
                    JSONObject wind = jsonObject.getJSONObject("wind");
                    double windSpeed = wind.getDouble("speed");

                    // Kelembapan dan tekanan udara
                    int humidity = main.getInt("humidity");
                    int pressure = main.getInt("pressure");

                    // Waktu matahari terbit dan terbenam
                    JSONObject sys = jsonObject.getJSONObject("sys");
                    long sunrise = sys.getLong("sunrise") * 1000L; // Convert to milliseconds
                    long sunset = sys.getLong("sunset") * 1000L; // Convert to milliseconds

                    // Format waktu untuk matahari terbit dan terbenam
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    String sunriseTime = sdf.format(new Date(sunrise));
                    String sunsetTime = sdf.format(new Date(sunset));

                    // Tampilkan hasil pada TextView
                    cityTextView.setText("City: " + cityName);
                    tempTextView.setText("Temperature: " + temperature + "°C");
                    descTextView.setText("Description: " + description);

                    // Tambahkan informasi tambahan
                    windSpeedTextView.setText("Wind Speed: " + windSpeed + " m/s");
                    humidityTextView.setText("Humidity: " + humidity + "%");
                    pressureTextView.setText("Pressure: " + pressure + " hPa");
                    sunriseTextView.setText("Sunrise: " + sunriseTime);
                    sunsetTextView.setText("Sunset: " + sunsetTime);

                } catch (Exception e) {
                    e.printStackTrace();
                    cityTextView.setText("Error: Could not parse weather data.");
                }
            } else {
                cityTextView.setText("Error: Could not retrieve weather data.");
            }
        }
    }
}
