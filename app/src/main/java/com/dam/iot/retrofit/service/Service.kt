package com.dam.iot.retrofit.service

import com.dam.iot.model.ApiHumidadeResponse
import com.dam.iot.model.ApiLedResponse
import com.dam.iot.model.ApiRAResponse


import com.dam.iot.model.RegaRequest
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

    @POST("rega/manual")
    fun setManualRega(@Body request: RegaRequest): Call<RegaRequest>

    @GET("rega/automatica")
    fun getAutomaticRega(): Call<ApiRAResponse>
}