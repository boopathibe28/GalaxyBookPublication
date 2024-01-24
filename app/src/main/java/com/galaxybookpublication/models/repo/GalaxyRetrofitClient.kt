package com.galaxybookpublication.models.repo

import com.galaxybookpublication.models.interfaces.GalaxyApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GalaxyRetrofitClient {
//    private const val BASE_URL = "https://staging.galaxybookpublication.in/api/v1/" // demo
    private const val BASE_URL = "https://galaxybookpublication.in/"  // live

    private val mLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

   // val okHttpClient = OkHttpClient.Builder().addInterceptor(OAuthInterceptor("Bearer", SharedPreferenceHelper.AUTH_TOKEN)).build()
   private val okHttpClient = OkHttpClient.Builder().addInterceptor(mLoggingInterceptor).build()

    private fun getClient(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    val apiService: GalaxyApi = getClient().create(GalaxyApi::class.java)
}