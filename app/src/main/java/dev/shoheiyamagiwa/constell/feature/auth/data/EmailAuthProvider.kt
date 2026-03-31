package dev.shoheiyamagiwa.constell.feature.auth.data

public interface EmailAuthProvider {
    /**
     * Registers a new user with an email and password.
     *
     * Attempts to create a new user account in the authentication system using the provided
     * email and password.
     *
     * @param email The email address to be associated with the new user account.
     * @param password The password to secure the new user account.
     */
    public suspend fun signUpWithEmail(email: String, password: String)

    /**
     * Authenticates a user using their email and password.
     *
     * Attempts to sign in a user by verifying the provided email and password
     * against the authentication system. Returns a user identifier if the
     * authentication is successful or null if it fails.
     *
     * @param email The email address of the user attempting to sign in.
     * @param password The password corresponding to the user's email.
     * @return The authenticated user's id if successful, or null if authentication fails.
     */
    public suspend fun signInWithEmail(email: String, password: String): String?

    public suspend fun requestPasswordReset(email: String)

    public suspend fun updatePassword(newPassword: String)
}