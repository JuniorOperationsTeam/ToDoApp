package com.example.examplefeature.backend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class CurrencyService {
    private static final String BASE = "https://api.exchangerate.host/convert";
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final Gson gson = new Gson();



    public BigDecimal convert(BigDecimal amount, String from, String to) {
        try {
            String url = "https://api.frankfurter.dev/v1/latest?base=" + from + "&symbols=" + to;
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP " + response.statusCode() + " - " + response.body());
            }

            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
            JsonObject rates = json.getAsJsonObject("rates");

            if (rates == null || rates.get(to) == null) {
                throw new RuntimeException("Taxa não encontrada para " + to + " (body: " + response.body() + ")");
            }

            BigDecimal rate = rates.get(to).getAsBigDecimal();
            return amount.multiply(rate);
        } catch (Exception e) {
            throw new RuntimeException("Falha na conversão: " + e.getMessage(), e);
        }
    }


}

