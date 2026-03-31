package dev.shoheiyamagiwa.constell.feature.auth.data

public interface GitHubAuthProvider {
    public suspend fun signInWithGitHub(): String?
}