package com.example.proyectofinalgrupo2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinalgrupo2.model.Laboratorios
import com.example.proyectofinalgrupo2.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AgregarLaboratorioActivity : AppCompatActivity() {

    private lateinit var etCodigo: EditText
    private lateinit var etNombre: EditText
    private lateinit var etPiso: EditText
    private lateinit var etPabellon: EditText
    private lateinit var etImgSalon: EditText
    private lateinit var etImgRecorrido: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_laboratorio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etCodigo = findViewById(R.id.etCodigo)
        etNombre = findViewById(R.id.etNombre)
        etPiso = findViewById(R.id.etPiso)
        etPabellon = findViewById(R.id.etPabellon)
        etImgSalon = findViewById(R.id.etImgSalon)
        etImgRecorrido = findViewById(R.id.etImgRecorrido)
        etDescripcion = findViewById(R.id.etDescripcion)
        btnGuardar = findViewById(R.id.btnGuardar)

        btnGuardar.setOnClickListener {
            agregarLaboratorio()
        }

    }

    private fun agregarLaboratorio() {
        val laboratorio = Laboratorios(
            lab_id = 0,
            lab_codigo = etCodigo.text.toString(),
            lab_nombre = etNombre.text.toString(),
            lab_piso = etPiso.text.toString().toInt(),
            lab_pabellon = etPabellon.text.toString(),
            lab_imgsalon = etImgSalon.text.toString(),
            lab_imgrecorrido = etImgRecorrido.text.toString(),
            lab_descripcion = etDescripcion.text.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.webService.agregarLaboratorio(laboratorio)
            runOnUiThread {
                if (response.isSuccessful) {
                    Toast.makeText(this@AgregarLaboratorioActivity, "Laboratorio agregado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AgregarLaboratorioActivity, "Error al agregar laboratorio", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}