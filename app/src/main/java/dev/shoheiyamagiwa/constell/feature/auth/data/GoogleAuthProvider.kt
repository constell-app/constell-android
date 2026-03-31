package dev.shoheiyamagiwa.constell.feature.auth.data

public interface GoogleAuthProvider {
    public suspend fun signInWithGoogle(): String?
}