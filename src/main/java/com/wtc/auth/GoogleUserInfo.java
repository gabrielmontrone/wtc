package com.wtc.auth;

/** Minimal identity extracted from a verified Google ID token. */
public record GoogleUserInfo(String email, String name) {}
