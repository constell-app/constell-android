package dev.shoheiyamagiwa.constell.di

import dev.shoheiyamagiwa.constell.feature.auth.AuthViewModel
import dev.shoheiyamagiwa.constell.feature.auth.data.AuthRepository
import dev.shoheiyamagiwa.constell.feature.auth.data.SupabaseAuthRepository
import dev.shoheiyamagiwa.constell.feature.walkthrough.WalkthroughViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Repositories
    single<AuthRepository> { SupabaseAuthRepository(supabaseClient = get()) }

    // ViewModels
    viewModel { AuthViewModel(authRepository = get()) }
    viewModel { WalkthroughViewModel() }
}