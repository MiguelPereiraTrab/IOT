package com.dam.iot

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dam.iot.model.ApiHumidadeResponse
import com.dam.iot.model.ApiLedResponse
import com.dam.iot.model.ApiRAResponse
import com.dam.iot.model.RegaRequest
import com.dam.iot.model.RegaResponse
import com.dam.iot.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var circuloLED: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var circuloHumidade: ImageView
    private lateinit var displayHumidade: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var regState: TextView // Adiciona esta linha
    private lateinit var circle1: ImageView // Declarar circle1 como propriedade da classe
    private lateinit var circle2: ImageView // Declarar circle2 como propriedade da classe
    private lateinit var circle3: ImageView // Declarar circle3 como propriedade da classe
    private lateinit var circles: List<ImageView>
    private val handler = Handler(Looper.getMainLooper()) // Criar um objeto Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refreshContent()
        }
        // Inicializar circuloLED
        circuloLED = findViewById(R.id.circuloLED)


        sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        //variaveis ainda n usadas
        circuloHumidade = findViewById(R.id.circuloHumidade)
        displayHumidade = findViewById<TextView>(R.id.displayHumidade)
        regState = findViewById(R.id.regState) // Inicializa o TextView do estado da rega
       // val atualizarHumidade = findViewById<Button>(R.id.atualizarHumidade)


        val listaFlores = findViewById<Button>(R.id.listaFlores)
        val estadoRega = findViewById<EditText>(R.id.estadoRega)
        val adicionarFlores = findViewById<Button>(R.id.adicionarFLores)

        val botaoTipoRegaOFF: Button = findViewById(R.id.botaoTipoRegaOFF)
        val botaoTipoRegaON: Button = findViewById(R.id.botaoTipoRegaON)
        val botaoTipoRegaAUTO: Button = findViewById(R.id.botaoTipoRegaAUTO)

        circle1 = findViewById(R.id.circle1)
        circle2 = findViewById(R.id.circle2)
        circle3 = findViewById(R.id.circle3)

         circles = listOf(circle1, circle2, circle3)


        // Definir o modo inicial como "AUTO"
        activateCircle(circle3, circles, "AUTO")

        botaoTipoRegaOFF.setOnClickListener {
            activateCircle(circle1, circles, "OFF")
            setManualIrrigationState("off")
            handler.postDelayed({ refreshContent() }, 1000)
        }
        botaoTipoRegaON.setOnClickListener {
            activateCircle(circle2, circles, "ON")
            setManualIrrigationState("on")
            handler.postDelayed({ refreshContent() }, 1000)
        }
        botaoTipoRegaAUTO.setOnClickListener {
            activateCircle(circle3, circles, "AUTO")
            // Optionally set the irrigation state for AUTO mode if required
            setManualIrrigationState("auto")  // If you need to handle AUTO mode, otherwise you can remove this line
            handler.postDelayed({ refreshContent() }, 1000)
        }


        adicionarFlores.setOnClickListener {
            val text = estadoRega.text.toString()
            setManualIrrigationState(text)
        }



        //atualizarHumidade.setOnClickListener{
           // getLedState()
         //   getHumidadeState()
       //     getRAState()
     //   }
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

        // Salvar o modo selecionado nas SharedPreferences
        with(sharedPreferences.edit()) {
            putString("mode", mode)
            apply()
        }

        // Verificar se o modo é AUTO e atualizar o estado da rega
        if (mode == "AUTO") {
            getRAState()
        } else {
            regState.text = "" // Limpa o estado da rega quando não está no modo AUTO
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
                            if (led == "on") {
                                circuloHumidade.setBackgroundResource(R.drawable.circle_on) // Green circle
                            } else {
                                circuloHumidade.setBackgroundResource(R.drawable.circle_off) // Red circle
                            }
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
                            val humidadeArredondada = String.format("%.1f", humidade)
                            Log.e("ADMIN!!", "Humidade: $humidade")
                            // Handle the LED state
                            displayHumidade.text = "$humidadeArredondada%"
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
                            if (regaAuto == "on") {
                                regState.text = "A regar"
                            } else {
                                regState.text = "Rega Desligada"
                            }
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

    private fun getRegaState(callback: (String) -> Unit) {
        val call = ApiService.api.getRega()
        // To get LED state
        call.enqueue(object : Callback<RegaResponse> {
            override fun onResponse(call: Call<RegaResponse>, response: Response<RegaResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        val rega = apiResponse.estadoRegaManual
                        Log.e("ADMIN!!", "Rega: $rega")
                        // Handle the LED state
                        callback(rega) // Chama a função de retorno de chamada com o estado da rega
                    } else {
                        Log.e("Erro", "Resposta vazia ou mal formatada--------------")
                    }
                } else {
                    Log.e("Erro", "Resposta não bem-sucedida-------------------")
                }
            }

            override fun onFailure(call: Call<RegaResponse>, t: Throwable) {
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

    private fun refreshContent() {
        // Chame as funções para atualizar o conteúdo
        getLedState()
        getHumidadeState()
        getRAState()
        getRegaState { rega ->
            when (rega) {
                "off" -> activateCircle(circle1, circles, "OFF")
                "on" -> activateCircle(circle2, circles, "ON")
                "auto" -> activateCircle(circle3, circles, "AUTO")
            }
            // Parar a animação de atualização apenas quando todas as chamadas da API forem concluídas
            swipeRefreshLayout.isRefreshing = false
            // Aqui você pode fazer algo com o estado da rega se precisar

        }
    }

// Exemplo de uso:
// Para ligar a rega manual:
        //  setManualIrrigationState("on")

// Para desligar a rega manual:
        // setManualIrrigationState("off")


    }