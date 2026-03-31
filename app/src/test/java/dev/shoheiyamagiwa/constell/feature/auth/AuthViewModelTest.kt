package dev.shoheiyamagiwa.constell.feature.auth

import dev.shoheiyamagiwa.constell.data.repository.UserPreferencesRepository
import dev.shoheiyamagiwa.constell.feature.auth.data.AuthRepository
import dev.shoheiyamagiwa.constell.feature.auth.data.EmailAuthProvider
import dev.shoheiyamagiwa.constell.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
public class AuthViewModelTest {
    @get:Rule
    public val mainDispatcherRule = MainDispatcherRule()

    private val authRepository =
        mockk<AuthRepository>(moreInterfaces = arrayOf(EmailAuthProvider::class))
    private val userPreferencesRepository = mockk<UserPreferencesRepository>()

    private lateinit var viewModel: AuthViewModel

    @Before
    public fun setup() {
        // FIXME: Patterns.EMAIL_ADDRESS is null in unit tests.
        // We avoid testing email validation that depends on it, 
        // or we test cases that return early (like blank email).
        coEvery { userPreferencesRepository.updateLoggedIn(isLoggedIn = any()) } returns Unit
        coEvery { userPreferencesRepository.updateUserId(any()) } returns Unit

        viewModel = AuthViewModel(
            authRepository = authRepository,
            userPreferencesRepository = userPreferencesRepository
        )
    }

    @Test
    public fun `initial state is Loading`() {
        assertTrue(viewModel.uiState.value is AuthUiState.Loading)
    }

    @Test
    public fun `validateSession sets state to SignIn when not authenticated`() =
        runTest(context = mainDispatcherRule.testDispatcher) {
            coEvery { authRepository.isAuthenticated() } returns null

            viewModel.validateSession()
            runCurrent()

            assertTrue(viewModel.uiState.value is AuthUiState.SignIn)
        }

    @Test
    public fun `validateSession emits NavigateToHome event when authenticated`() =
        runTest(context = mainDispatcherRule.testDispatcher) {
            coEvery { authRepository.isAuthenticated() } returns "u1"
            coEvery { authRepository.refreshSession() } returns "u1"

            val events = mutableListOf<AuthUiEvent>()
            val job = launch(context = mainDispatcherRule.testDispatcher) {
                viewModel.uiEvent.collect { events.add(it) }
            }

            viewModel.validateSession()
            runCurrent()

            assertTrue(
                "Events should contain NavigateToHome. Current events: $events",
                events.contains(AuthUiEvent.NavigateToHome)
            )
            job.cancelAndJoin()
        }

    @Test
    public fun `changeToSignUpUi updates state from SignIn to SignUp`() =
        runTest(context = mainDispatcherRule.testDispatcher) {
            coEvery { authRepository.isAuthenticated() } returns null
            viewModel.validateSession() // To get to SignIn state
            runCurrent()

            viewModel.changeToSignUpUi()

            assertTrue(viewModel.uiState.value is AuthUiState.SignUp)
        }

    @Test
    public fun `submitSignIn with blank email sets error without using Patterns`() =
        runTest(context = mainDispatcherRule.testDispatcher) {
            coEvery { authRepository.isAuthenticated() } returns null
            viewModel.validateSession()
            runCurrent()

            viewModel.updateEmail(value = "")
            viewModel.submit()

            val state = viewModel.uiState.value as AuthUiState.SignIn
            assertTrue(state.email.hasError())
        }
}
