package dev.shoheiyamagiwa.constell.feature.auth.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Github
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email

public class SupabaseAuthRepository(private val supabaseClient: SupabaseClient) : AuthRepository, WithEmail, SignableInWithGoogle, SignableInWithGitHub {
    override suspend fun signOut() {
        supabaseClient.auth.signOut()
    }

    override suspend fun isAuthenticated(): Boolean {
        return supabaseClient.auth.currentSessionOrNull() != null
    }

    override suspend fun refreshSession() {
        supabaseClient.auth.refreshCurrentSession()
    }

    override suspend fun signUpWithEmail(email: String, password: String) {
        supabaseClient.auth.signUpWith(provider = Email, config = { // TODO: redirect url
            this.email = email
            this.password = password
        })
    }

    override suspend fun signInWithEmail(email: String, password: String) {
        supabaseClient.auth.signInWith(provider = Email, config = {
            this.email = email
            this.password = password
        })
    }

    override suspend fun requestPasswordReset(email: String) {
        supabaseClient.auth.resetPasswordForEmail(email) // TODO: redirect url
    }

    override suspend fun updatePassword(newPassword: String) {
        supabaseClient.auth.updateUser(config = {
            this.password = newPassword
        })
    }

    override suspend fun signInWithGoogle() {
        supabaseClient.auth.signInWith(provider = Google) // TODO set up specific configs
    }

    override suspend fun signInWithGitHub() {
        supabaseClient.auth.signInWith(provider = Github) // TODO set up specific configs
    }
}