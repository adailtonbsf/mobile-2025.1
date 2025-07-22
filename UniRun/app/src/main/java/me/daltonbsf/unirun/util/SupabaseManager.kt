package me.daltonbsf.unirun.util

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import me.daltonbsf.unirun.BuildConfig

object SupabaseManager {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPA_BASE_URL,
        supabaseKey = BuildConfig.SUPA_API_KEY
    ) {
        install(Postgrest)
        install(Storage)
    }

    val postgrest = client.postgrest
    val storage = client.storage
}