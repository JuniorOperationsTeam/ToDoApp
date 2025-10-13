package com.example.exampleutils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final String remetente = "teu.email@gmail.com";
    private final String password = "tua_senha_app"; // usa App Password se for Gmail
    private final String smtpHost = "smtp.gmail.com";
    private final int smtpPort = 587;

    public void enviarEmailConfirmacao(String destinatario, String descricaoTask) {
        try {
            SimpleEmail email = new SimpleEmail();
            email.setHostName(smtpHost);
            email.setSmtpPort(smtpPort);
            email.setAuthentication(remetente, password);
            email.setStartTLSEnabled(true);

            email.setFrom(remetente);
            email.setSubject("Confirmação de criação de Task");
            email.setMsg("Olá,\n\nA tua task \"" + descricaoTask + "\" foi criada com sucesso.\n\nCumprimentos,\nEquipa");
            email.addTo(destinatario);

            email.send();
            System.out.println("✅ Email enviado para " + destinatario);
        } catch (EmailException e) {
            System.err.println("❌ Erro ao enviar email: " + e.getMessage());
        }
    }
}
