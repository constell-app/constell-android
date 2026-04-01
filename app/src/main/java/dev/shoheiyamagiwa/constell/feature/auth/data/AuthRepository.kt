package dev.shoheiyamagiwa.constell.feature.auth.data

public interface AuthRepository {
    /**
     * Signs out the currently authenticated user.
     *
     * This method clears the current authentication session, removing any active user credentials
     * or tokens stored by the underlying authentication system. After calling this method,
     * the user will be considered unauthenticated until they sign in again.
     */
    public suspend fun signOut()

    /**
     * Checks whether there is a currently authenticated user session.
     *
     * @return The user id if authenticated, or null if there is no active session.
     */
    public suspend fun isAuthenticated(): String?

    /**
     * Refreshes the current user session to extend its validity period or restore the session state.
     *
     * @return The user id if successful, or null if the session could not be refreshed.
     */
    public suspend fun refreshSession(): String?
}