package dev.shoheiyamagiwa.constell.feature.auth.data

public interface WithEmail {
    public suspend fun signUpWithEmail(email: String, password: String)

    public suspend fun signInWithEmail(email: String, password: String)

    public suspend fun requestPasswordReset(email: String)

    public suspend fun updatePassword(newPassword: String)
}