package com.dam.iot.retrofit.service

import com.dam.iot.model.ApiHumidadeResponse
import com.dam.iot.model.ApiLedResponse
import com.dam.iot.model.ApiRAResponse


import com.dam.iot.model.RegaRequest
import com.dam.iot.model.RegaResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface Service {
    @GET("led")
    fun getLedState(): Call<ApiLedResponse>

    @GET("humidade")
    fun getHumidity(): Call<ApiHumidadeResponse>

    @GET("rega/estado")
    fun getRega(): Call<RegaResponse>

    @POST("rega/manual")
    fun setManualRega(@Body request: RegaRequest): Call<RegaRequest>

    @GET("rega/automatica")
    fun getAutomaticRega(): Call<ApiRAResponse>
}