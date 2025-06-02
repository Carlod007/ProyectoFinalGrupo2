package com.example.proyectofinalgrupo2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectofinalgrupo2.databinding.ActivityBuscadorLabsBinding
import com.example.proyectofinalgrupo2.model.Laboratorios
import com.example.proyectofinalgrupo2.servicio.RetrofitClient
import kotlinx.coroutines.launch

class BuscadorLabs : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorLabsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBuscadorLabsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Boton retroceso
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        binding.btnBuscar.setOnClickListener {
            val codigoIngresado = binding.editTextCodigo.text.toString().trim()

            if(codigoIngresado.isEmpty()){
                Toast.makeText(this, "ingresar código", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //Llamar API
            lifecycleScope.launch {
                try {
                    val respuesta = RetrofitClient.webService.buscarLaboratoriosCodigo(codigoIngresado)
                    if (respuesta.isSuccessful && respuesta.body() != null){
                        val laboratorio = respuesta.body()!!
                        val intent = Intent(this@BuscadorLabs, DetalleLaboratorioActivity::class.java)
                        intent.putExtra("codigo",laboratorio.lab_codigo)
                        intent.putExtra("nombre",laboratorio.lab_nombre)
                        intent.putExtra("piso",laboratorio.lab_piso)
                        intent.putExtra("pabellon",laboratorio.lab_pabellon)
                        intent.putExtra("imgRecorrido", laboratorio.lab_imgrecorrido)
                        intent.putExtra("imgSalon",laboratorio.lab_imgsalon)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this@BuscadorLabs, "Laboratorio no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Toast.makeText(this@BuscadorLabs, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}