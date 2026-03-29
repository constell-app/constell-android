package dev.shoheiyamagiwa.constell.feature.auth.data

public interface AuthRepository {
    public suspend fun signOut()

    public suspend fun isAuthenticated(): Boolean

    public suspend fun refreshSession()
}