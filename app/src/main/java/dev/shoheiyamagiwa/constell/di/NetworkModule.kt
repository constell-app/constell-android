package dev.shoheiyamagiwa.constell.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

public val networkModule = module {
    single {
        HttpClient(engineFactory = CIO)
    }
}