package dev.shoheiyamagiwa.constell.di

import dev.shoheiyamagiwa.constell.MainViewModel
import dev.shoheiyamagiwa.constell.data.repository.UserPreferencesRepository
import dev.shoheiyamagiwa.constell.feature.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

public val appModule = module {
    single { UserPreferencesRepository(context = androidContext()) }
    viewModel { MainViewModel(repository = get()) }
    viewModel { HomeViewModel() }
}
