package dev.shoheiyamagiwa.constell.di

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

/**
 * Ktor HTTP Client (singleton)
 */
public val networkModule = module {
    single {
        HttpClient(engineFactory = CIO)
    }
}

/**
 * Supabase client (singleton)
 */
public val supabase = module {
    single {
        createSupabaseClient(
            supabaseUrl = "https://bmylggxjtskzmroxiedh.supabase.co",
            supabaseKey = "sb_publishable_ozofoZCC34Jk91BwTKT3jA__hap3ZTe"
        ) {
            install(plugin = Auth)
            install(plugin = Postgrest)
        }
    }
}