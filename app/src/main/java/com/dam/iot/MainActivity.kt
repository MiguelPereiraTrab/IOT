package com.dam.iot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.dam.iot.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listaFlores = findViewById<Button>(R.id.listaFlores)
        val adicionarFlores = findViewById<Button>(R.id.adicionarFlores)

        listaFlores.setOnClickListener {
            val intent = Intent(this, Flores::class.java)
            startActivity(intent)
        }

        adicionarFlores.setOnClickListener {
            val intent = Intent(this, AdicionarFlores::class.java)
            startActivity(intent)
        }
    }
}
