package com.example.atividade_api_mobileclima_ianvieira

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // Declarando as variáveis da tela
    private lateinit var etCityName: TextInputEditText
    private lateinit var btnSearchWeather: Button
    private lateinit var tvTemperature: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvTimezone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Conectando com os novos IDs do XML do Material Design
        etCityName = findViewById(R.id.etCityName)
        btnSearchWeather = findViewById(R.id.btnSearchWeather)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvWind = findViewById(R.id.tvWind)
        tvTimezone = findViewById(R.id.tvTimezone)

        // 2. Ação do Botão
        btnSearchWeather.setOnClickListener {
            val city = etCityName.text.toString().trim()

            // 3. Validação de campo vazio (Regra da N1)
            if (city.isEmpty()) {
                Toast.makeText(this, "Por favor, digite o nome de uma cidade!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Buscando o clima de $city...", Toast.LENGTH_SHORT).show()
                buscarCoordenadasDaCidade(city)
            }
        }
    }

    // Função 1: Acha a Latitude e Longitude da cidade digitada
    private fun buscarCoordenadasDaCidade(city: String) {
        val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=${city.replace(" ", "+")}&count=1&language=pt"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, geoUrl, null,
            { response ->
                try {
                    if (response.has("results")) {
                        val location = response.getJSONArray("results").getJSONObject(0)

                        val lat = location.getDouble("latitude")
                        val lon = location.getDouble("longitude")
                        val timezone = location.getString("timezone")

                        // Com as coordenadas em mãos, busca o clima!
                        buscarClimaExato(lat, lon, timezone)
                    } else {
                        Toast.makeText(this, "Cidade não encontrada.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Erro ao ler dados da cidade.", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Erro de rede ao buscar a cidade.", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(request)
    }

    // Função 2: Pega o clima baseado nas coordenadas (A base perfeita para a N2)
    private fun buscarClimaExato(lat: Double, lon: Double, timezone: String) {
        val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, weatherUrl, null,
            { response ->
                try {
                    val currentWeather = response.getJSONObject("current_weather")

                    // Extrai as 3 informações úteis exigidas
                    val temperature = currentWeather.getDouble("temperature")
                    val windSpeed = currentWeather.getDouble("windspeed")

                    // Exibe na tela com o novo layout
                    tvTemperature.text = "Temperatura: $temperature °C"
                    tvWind.text = "Vento: $windSpeed km/h"
                    tvTimezone.text = "Fuso Horário: $timezone"

                } catch (e: Exception) {
                    Toast.makeText(this, "Erro ao processar o clima.", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Erro de rede ao buscar o clima.", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(request)
    }
}