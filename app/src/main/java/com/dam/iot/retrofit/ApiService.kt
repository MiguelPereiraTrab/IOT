package com.dam.iot.retrofit

// ApiService.kt
import com.dam.iot.retrofit.service.Service
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    private val gson: Gson = GsonBuilder().setLenient().create()
    private const val BASE_URL = "https://briefly-bold-mule.ngrok-free.app/" // Atualize com o endereço do seu servidor API
    private const val USERNAME = "diogo" // Substitua pelo nome de usuário
    private const val PASSWORD = "12345" // Substitua pela senha

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val credentials = Credentials.basic(USERNAME, PASSWORD)
            val request = chain.request().newBuilder()
                .header("Authorization", credentials)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: Service = retrofit.create(Service::class.java)
}

