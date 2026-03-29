package dev.shoheiyamagiwa.constell.di

import dev.shoheiyamagiwa.constell.feature.auth.AuthViewModel
import dev.shoheiyamagiwa.constell.feature.auth.data.AuthRepository
import dev.shoheiyamagiwa.constell.feature.auth.data.SupabaseAuthRepository
import dev.shoheiyamagiwa.constell.feature.walkthrough.WalkthroughViewModel
import dev.shoheiyamagiwa.constell.MainViewModel
import dev.shoheiyamagiwa.constell.data.repository.UserPreferencesRepository
import dev.shoheiyamagiwa.constell.feature.home.HomeViewModel
import dev.shoheiyamagiwa.constell.feature.home.data.ArticleRepository
import dev.shoheiyamagiwa.constell.feature.home.data.SupabaseArticleRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Repositories
    single<AuthRepository> { SupabaseAuthRepository(supabaseClient = get()) }
    single<ArticleRepository> { SupabaseArticleRepository(supabaseClient = get()) }

    // ViewModels
    viewModel { AuthViewModel(authRepository = get()) }
    viewModel { WalkthroughViewModel() }
    single { UserPreferencesRepository(context = androidContext()) }
    viewModel { MainViewModel(repository = get()) }
    viewModel { HomeViewModel(articleRepository = get()) }
}
