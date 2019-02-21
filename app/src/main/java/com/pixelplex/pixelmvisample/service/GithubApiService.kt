package com.pixelplex.pixelmvisample.service

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.pixelplex.pixelmvisample.model.RemoteGithubUser
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GithubApiService {

    @GET("/users")
    fun getUsersBatchAsync(
        @Query("since") lastSeenUserId: Int
    ): Deferred<List<RemoteGithubUser>>

    @GET("/users/{login}")
    fun getUserOverviewAsync(
        @Path("login") login: String
    ): Deferred<RemoteGithubUser>

    companion object {
        private const val BASE_URL = "https://api.github.com"

        private val log = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        private val serializeStrategy: ExclusionStrategy = object : ExclusionStrategy {
            override fun shouldSkipClass(clazz: Class<*>): Boolean {
                return false
            }

            override fun shouldSkipField(field: FieldAttributes): Boolean {
                return !(field.getAnnotation(Expose::class.java)?.serialize ?: true)
            }
        }

        private val gson = GsonBuilder()
            .addSerializationExclusionStrategy(serializeStrategy)
            .setLenient()
            .create()

        private val okHttpClient = OkHttpClient.Builder()
            .callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(log).build()

        fun instantiate(): GithubApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttpClient)
                .build().create(GithubApiService::class.java)
        }
    }
}