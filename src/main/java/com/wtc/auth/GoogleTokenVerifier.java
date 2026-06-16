package com.wtc.auth;

/**
 * Verifies a Google Sign-In ID token and returns the authenticated user's identity.
 * Abstracted behind an interface so {@link AuthService} can be unit-tested without
 * reaching out to Google's servers.
 */
public interface GoogleTokenVerifier {

    /**
     * @param idToken the ID token issued by Google to the Android client
     * @return the verified user's email and name
     * @throws RuntimeException if the token is missing, invalid, expired, or the
     *                          email is not verified
     */
    GoogleUserInfo verify(String idToken);
}
