package com.wtc.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Default {@link GoogleTokenVerifier} backed by Google's {@link GoogleIdTokenVerifier},
 * which validates the token's signature against Google's public keys and checks that it
 * was issued for our OAuth client (audience).
 */
@Component
public class GoogleIdTokenVerifierAdapter implements GoogleTokenVerifier {

    private final String clientId;
    private final GoogleIdTokenVerifier verifier;

    public GoogleIdTokenVerifierAdapter(@Value("${google.oauth.client-id:}") String clientId) {
        this.clientId = clientId;
        List<String> audience = (clientId == null || clientId.isBlank())
                ? Collections.emptyList()
                : Collections.singletonList(clientId);
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(audience)
                .build();
    }

    @Override
    public GoogleUserInfo verify(String idTokenString) {
        if (clientId == null || clientId.isBlank()) {
            // Feature not provisioned on this backend — not a server fault, so surface 503
            // instead of a generic 500 so the app can show a clear message.
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Google Sign-In não está configurado neste servidor (defina GOOGLE_CLIENT_ID).");
        }
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token do Google inválido.");
            }
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            if (email == null || !Boolean.TRUE.equals(payload.getEmailVerified())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail do Google não verificado.");
            }
            return new GoogleUserInfo(email, (String) payload.get("name"));
        } catch (GeneralSecurityException | IOException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falha ao verificar o token do Google.", e);
        }
    }
}
