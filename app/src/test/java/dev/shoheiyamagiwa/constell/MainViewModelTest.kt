package dev.shoheiyamagiwa.constell

import dev.shoheiyamagiwa.constell.data.repository.UserPreferencesRepository
import dev.shoheiyamagiwa.constell.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
public class MainViewModelTest {
    @get:Rule
    public val mainDispatcherRule = MainDispatcherRule()

    private val repository: UserPreferencesRepository = mockk()

    @Test
    public fun `initial state becomes Success when repository emits values`() = runTest {
        val isLoggedInFlow = MutableStateFlow(value = false)
        val isFirstLaunchFlow = MutableStateFlow(value = true)
        every { repository.isLoggedIn } returns isLoggedInFlow
        every { repository.isFirstLaunch } returns isFirstLaunchFlow

        val viewModel = MainViewModel(repository)
        assertEquals(MainUiState.Success(isLoggedIn = false, isFirstLaunch = true), viewModel.uiState.value)
    }

    @Test
    public fun `uiState updates when repository flows emit new values`() = runTest {
        val isLoggedInFlow = MutableStateFlow(value = false)
        val isFirstLaunchFlow = MutableStateFlow(value = true)
        every { repository.isLoggedIn } returns isLoggedInFlow
        every { repository.isFirstLaunch } returns isFirstLaunchFlow

        val viewModel = MainViewModel(repository)
        assertEquals(MainUiState.Success(isLoggedIn = false, isFirstLaunch = true), viewModel.uiState.value)

        isFirstLaunchFlow.value = false
        assertEquals(MainUiState.Success(isLoggedIn = false, isFirstLaunch = false), viewModel.uiState.value)

        isLoggedInFlow.value = true
        assertEquals(MainUiState.Success(isLoggedIn = true, isFirstLaunch = false), viewModel.uiState.value)
    }
}
