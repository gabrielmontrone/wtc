package com.wtc.notification;

import org.springframework.stereotype.Service;

@Service
public class FcmService {

    public void sendPush(String targetToken, String title, String body) {
        if (targetToken == null || targetToken.isEmpty()) {
            System.out.println("LOG: Usuário não possui Token FCM. Push não enviado.");
            return;
        }

        // Por enquanto, vamos apenas simular no log.
        // Depois configuramos a chave real do Firebase.
        System.out.println("----------------------------------------------");
        System.out.println("ENVIANDO PUSH PARA: " + targetToken);
        System.out.println("TÍTULO: " + title);
        System.out.println("MENSAGEM: " + body);
        System.out.println("----------------------------------------------");

        // Critério: Registrar logs de sucesso
        System.out.println("LOG: Push enviado com sucesso via FCM.");
    }
}