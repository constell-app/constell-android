package dev.shoheiyamagiwa.constell.feature.auth.data

public interface AuthRepository {
    public suspend fun signOut()
}