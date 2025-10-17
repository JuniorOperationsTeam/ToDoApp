package com.example.exampleutils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EmailService {

    @Value("${mailgun.api.key}")
    private String apiKey;

    @Value("${mailgun.domain}")
    private String domain;

    @Value("${mailgun.from.email}")
    private String fromEmail;

    @Value("${mailgun.base-url:https://api.mailgun.net}")
    private String baseUrl;

    private final HttpClient http = HttpClient.newHttpClient();

    /**
     * Envia email (aceita texto e/ou html). Retorna true se Mailgun aceitou a mensagem.
     */
    public boolean sendEmail(String destinatario, String subject, String textBody, String htmlBody) throws IOException, InterruptedException {
        String url = baseUrl + "/v3/" + domain + "/messages";

        var sb = new StringBuilder();
        sb.append("from=").append(URLEncoder.encode(fromEmail, StandardCharsets.UTF_8));
        sb.append("&to=").append(URLEncoder.encode(destinatario, StandardCharsets.UTF_8));
        sb.append("&subject=").append(URLEncoder.encode(subject, StandardCharsets.UTF_8));
        if (textBody != null) {
            sb.append("&text=").append(URLEncoder.encode(textBody, StandardCharsets.UTF_8));
        }
        if (htmlBody != null) {
            sb.append("&html=").append(URLEncoder.encode(htmlBody, StandardCharsets.UTF_8));
        }

        String auth = "api:" + apiKey;
        String basic = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + basic)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(sb.toString()))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[Mailgun] status=" + response.statusCode() + " body=" + response.body());

        // 200 => Mailgun queued it
        return response.statusCode() == 200;
    }
}
