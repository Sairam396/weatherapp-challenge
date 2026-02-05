package com.example.weatherapp.di

import android.app.Application
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.local.Prefs
import com.example.weatherapp.data.location.AndroidLocationProvider
import com.example.weatherapp.data.location.LocationProvider
import com.example.weatherapp.data.remote.OpenWeatherApi
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.util.AppLogger
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.openweathermap.org/"

    @Provides @Singleton
    fun provideJson(): Json =
        Json {
            // API can add extra fields anytime; I don't want parsing crashes.
            ignoreUnknownKeys = true
            explicitNulls = false
            isLenient = true
        }

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor { msg ->
            // This is very helpful when debugging issues like 401.
            AppLogger.d("OkHttp: $msg")
        }.apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.BASIC
        }

        val apiKeyInterceptor = Interceptor { chain ->
            val key = BuildConfig.OPEN_WEATHER_API_KEY

            // If key is empty, I want to know immediately.
            require(key.isNotBlank()) {
                "OPEN_WEATHER_API_KEY is empty. Check local.properties + buildConfigField."
            }

            val original = chain.request()
            val newUrl = original.url.newBuilder()
                .addQueryParameter("appid", key)
                .build()

            AppLogger.d("Request URL (final): $newUrl")

            chain.proceed(original.newBuilder().url(newUrl).build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides @Singleton
    fun provideApi(retrofit: Retrofit): OpenWeatherApi =
        retrofit.create(OpenWeatherApi::class.java)

    @Provides @Singleton
    fun providePrefs(app: Application): Prefs = Prefs(app)

    @Provides @Singleton
    fun provideLocationProvider(app: Application): LocationProvider =
        AndroidLocationProvider(app)

    @Provides @Singleton
    fun provideRepository(api: OpenWeatherApi, prefs: Prefs): WeatherRepository =
        WeatherRepositoryImpl(api, prefs)
}
