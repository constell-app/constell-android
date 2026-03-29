package dev.shoheiyamagiwa.constell.feature.auth

public sealed class AuthException(message: String) : Exception(message) {
    public class PasswordMismatch : AuthException("Passwords do not match")
    public class EmailAuthNotSupported : AuthException("Email authentication is not supported")
    public class InvalidCredentials : AuthException("Invalid login credentials")
    public class UserNotFound : AuthException("User not found")
    public class UserAlreadyExists : AuthException("User already registered")
    public class SessionNotFound : AuthException("Session not found")
    public class RefreshTokenNotFound : AuthException("Refresh token not found")
    public class RefreshTokenAlreadyUsed : AuthException("Refresh token already used")
}