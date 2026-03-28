package dev.shoheiyamagiwa.constell.feature.auth

public sealed class AuthException(message: String) : Exception(message) {
    public class PasswordMismatch : AuthException("Passwords do not match")
    public class EmailAuthNotSupported : AuthException("Email authentication is not supported")
}