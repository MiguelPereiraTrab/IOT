package com.dam.iot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dam.iot.model.ApiHumidadeResponse
import com.dam.iot.model.ApiLedResponse
import com.dam.iot.model.ApiRAResponse
import com.dam.iot.model.RegaRequest
import com.dam.iot.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var circuloLED: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        circuloLED = findViewById(R.id.circuloLED)
        val listaFlores = findViewById<Button>(R.id.listaFlores)
        val estadoRega = findViewById<EditText>(R.id.estadoRega)
        val adicionarFlores = findViewById<Button>(R.id.adicionarFLores)

        val botaoTipoRegaOFF: Button = findViewById(R.id.botaoTipoRegaOFF)
        val botaoTipoRegaON: Button = findViewById(R.id.botaoTipoRegaON)
        val botaoTipoRegaAUTO: Button = findViewById(R.id.botaoTipoRegaAUTO)

        val circle1: ImageView = findViewById(R.id.circle1)
        val circle2: ImageView = findViewById(R.id.circle2)
        val circle3: ImageView = findViewById(R.id.circle3)

        val circles = listOf(circle1, circle2, circle3)

        botaoTipoRegaOFF.setOnClickListener { activateCircle(circle1, circles , "OFF") }
        botaoTipoRegaON.setOnClickListener { activateCircle(circle2, circles , "ON") }
        botaoTipoRegaAUTO.setOnClickListener { activateCircle(circle3, circles, "AUTO") }

        listaFlores.setOnClickListener {
            getLedState()
            getHumidadeState()
            getRAState()
        }

        adicionarFlores.setOnClickListener {
            val text = estadoRega.text.toString()
            setManualIrrigationState(text)
        }
    }

    private fun activateCircle(activeCircle: ImageView, allCircles: List<ImageView>, mode: String) {
        for (circle in allCircles) {
            if (circle == activeCircle) {
                circle.setBackgroundResource(R.drawable.circle_on)
            } else {
                circle.setBackgroundResource(R.drawable.circle_off)
            }
        }
        when (mode) {
            "OFF" -> circuloLED.setBackgroundResource(R.drawable.circle_off)
            "ON" -> circuloLED.setBackgroundResource(R.drawable.circle_on)
            "AUTO" -> circuloLED.setBackgroundResource(R.drawable.circle_yellow)
        }
    }



    private fun getLedState() {
            val call = ApiService.api.getLedState()
            // To get LED state
            call.enqueue(object : Callback<ApiLedResponse> {
                override fun onResponse(call: Call<ApiLedResponse>, response: Response<ApiLedResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null ) {
                            val led = apiResponse.estado
                            Log.e("ADMIN!!", "Alerta Humidade: $led")
                            // Handle the LED state
                        } else {
                            Log.e("Erro", "Resposta vazia ou mal formatada")
                        }
                    } else {
                        Log.e("Erro", "Resposta não bem-sucedida")
                    }
                }


                override fun onFailure(call: Call<ApiLedResponse>, t: Throwable) {
                    // Handle failure
                    t.printStackTrace()
                    Log.e("Erro", "Erro na chamada à API: ${t.message}")
                }
            })
        }

        private fun getHumidadeState() {
            val call = ApiService.api.getHumidity()
            // To get LED state
            call.enqueue(object : Callback<ApiHumidadeResponse> {
                override fun onResponse(call: Call<ApiHumidadeResponse>, response: Response<ApiHumidadeResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null ) {
                            val humidade = apiResponse.humidade
                            Log.e("ADMIN!!", "Humidade: $humidade")
                            // Handle the LED state
                        } else {
                            Log.e("Erro", "Resposta vazia ou mal formatada")
                        }
                    } else {
                        Log.e("Erro", "Resposta não bem-sucedida")
                    }
                }


                override fun onFailure(call: Call<ApiHumidadeResponse>, t: Throwable) {
                    // Handle failure
                    t.printStackTrace()
                    Log.e("Erro", "Erro na chamada à API: ${t.message}")
                }
            })
        }

        private fun getRAState() {
            val call = ApiService.api.getAutomaticRega()
            // To get LED state
            call.enqueue(object : Callback<ApiRAResponse> {
                override fun onResponse(call: Call<ApiRAResponse>, response: Response<ApiRAResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null ) {
                            val regaAuto = apiResponse.estadoRegaAutomatica
                            Log.e("ADMIN!!", "Rega Automatica: $regaAuto")
                            // Handle the LED state
                        } else {
                            Log.e("Erro", "Resposta vazia ou mal formatada")
                        }
                    } else {
                        Log.e("Erro", "Resposta não bem-sucedida")
                    }
                }


                override fun onFailure(call: Call<ApiRAResponse>, t: Throwable) {
                    // Handle failure
                    t.printStackTrace()
                    Log.e("Erro", "Erro na chamada à API: ${t.message}")
                }
            })
        }


        // Função para definir o estado da rega manual
        private fun setManualIrrigationState(newState: String) {
            val request = RegaRequest(estadoRega = newState) // Crie um objeto RegaRequest com o estado desejado

            val call = ApiService.api.setManualRega(request)
            call.enqueue(object : Callback<RegaRequest> {
                override fun onResponse(call: Call<RegaRequest>, response: Response<RegaRequest>) {
                    if (response.isSuccessful) {
                        // Lidar com o sucesso (por exemplo, mostrar uma mensagem de sucesso)
                        Log.e("REGA MANUAL","Estado da rega manual atualizado com sucesso")
                    } else {
                        // Lidar com o erro (por exemplo, mostrar uma mensagem de erro)
                        Log.e("REGA MANUAL","Erro ao atualizar o estado da rega manual")
                    }
                }

                override fun onFailure(call: Call<RegaRequest>, t: Throwable) {
                    // Lidar com a falha (por exemplo, mostrar uma mensagem de erro)
                    Log.e("REGA MANUAL","Falha ao atualizar o estado da rega manual: ${t.message}")
                }
            })
        }

// Exemplo de uso:
// Para ligar a rega manual:
        //  setManualIrrigationState("on")

// Para desligar a rega manual:
        // setManualIrrigationState("off")


    }