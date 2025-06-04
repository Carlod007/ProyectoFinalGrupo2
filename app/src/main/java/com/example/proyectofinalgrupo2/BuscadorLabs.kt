package com.example.proyectofinalgrupo2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.example.proyectofinalgrupo2.servicio.Sugerencia
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class BuscadorLabs : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorLabsBinding
    private lateinit var adapter: ArrayAdapter<String>
    private var searchJob: Job? = null

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

        setupAutoComplete()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        binding.btnBuscar.setOnClickListener {
            var codigoIngresado = binding.editTextCodigo.text.toString().trim()


            if (codigoIngresado.contains(" - ")) {
                codigoIngresado = codigoIngresado.split(" - ")[0]
            }

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
                        binding.editTextCodigo.setText("")
                    }else{
                        Toast.makeText(this@BuscadorLabs, "Laboratorio no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Toast.makeText(this@BuscadorLabs, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupAutoComplete() {
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>()
        )
        binding.editTextCodigo.setAdapter(adapter)
        binding.editTextCodigo.threshold = 1
        binding.editTextCodigo.dropDownVerticalOffset = 16
        binding.editTextCodigo.post {
            val anchoActual = binding.editTextCodigo.width
            binding.editTextCodigo.dropDownWidth = 900
            binding.editTextCodigo.dropDownHorizontalOffset = -((900 - anchoActual) / 2)
        }

        binding.editTextCodigo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().trim()
                if (texto.isNotEmpty() && texto.length >= 1) {
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        delay(300)
                        buscarSugerencias(texto)
                    }
                }
            }
        })

        //Buscar en sugerencia
        binding.editTextCodigo.setOnItemClickListener { parent, view, position, id ->
            val itemSeleccionado = parent.getItemAtPosition(position).toString()
            val codigo = itemSeleccionado.split(" - ")[0]

            lifecycleScope.launch {
                try {
                    val respuesta = RetrofitClient.webService.buscarLaboratoriosCodigo(codigo)
                    if (respuesta.isSuccessful && respuesta.body() != null){
                        val laboratorio = respuesta.body()!!
                        val intent = Intent(this@BuscadorLabs, DetalleLaboratorioActivity::class.java)
                        intent.putExtra("codigo", laboratorio.lab_codigo)
                        intent.putExtra("nombre", laboratorio.lab_nombre)
                        intent.putExtra("piso", laboratorio.lab_piso)
                        intent.putExtra("pabellon", laboratorio.lab_pabellon)
                        intent.putExtra("imgRecorrido", laboratorio.lab_imgrecorrido)
                        intent.putExtra("imgSalon", laboratorio.lab_imgsalon)
                        startActivity(intent)
                        binding.editTextCodigo.setText("")
                    } else {
                        Toast.makeText(this@BuscadorLabs, "Laboratorio no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@BuscadorLabs, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun buscarSugerencias(texto: String) {
        try {
            val response = RetrofitClient.webService.obtenerSugerencias(texto)
            if (response.isSuccessful) {
                val sugerencias = response.body() ?: emptyList()
                updateSugerencias(sugerencias)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateSugerencias(sugerencias: List<Sugerencia>) {
        runOnUiThread {
            val listaSugerencias = sugerencias.map { "${it.lab_codigo} - ${it.lab_nombre}" }
            adapter.clear()
            adapter.addAll(listaSugerencias)
            adapter.notifyDataSetChanged()
        }
    }
}