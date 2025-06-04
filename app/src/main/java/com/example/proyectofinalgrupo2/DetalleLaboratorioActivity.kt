package com.example.proyectofinalgrupo2

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetalleLaboratorioActivity : AppCompatActivity() {

    private lateinit var tvCodigo: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvPiso: TextView
    private lateinit var tvPabellon: TextView
    private lateinit var imgSalon: ImageView
    private lateinit var btnMostrarRecorrido: Button


    private var codigoLaboratorio: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_laboratorio)

        //Vincular vistas
        tvNombre = findViewById(R.id.tvNombre)
        tvPiso = findViewById(R.id.tvPiso)
        tvPabellon = findViewById(R.id.tvPabellon)
        imgSalon = findViewById(R.id.imgSalon)
        btnMostrarRecorrido = findViewById(R.id.button3)

        //Obtener datos del Intent
        val codigo = intent.getStringExtra("codigo")
        val nombre = intent.getStringExtra("nombre")
        val piso = intent.getIntExtra("piso", 0)
        val pabellon = intent.getStringExtra("pabellon")
        val imgSalonNombre = intent.getStringExtra("imgSalon")
        val imageViewSalon = findViewById<ImageView>(R.id.imgSalon)
        val imgRecorridoNombre = intent.getStringExtra("imgRecorrido")

        //Asignar datos a vistas
        tvNombre.text = nombre
        tvPiso.text = "Piso $piso"
        tvPabellon.text = "Pabellón $pabellon"

        if (!imgSalonNombre.isNullOrEmpty()) {
            // Elimina la extensión .jpg si existe
            val nombreSinExtension = imgSalonNombre.substringBeforeLast(".")
            val resourceId = resources.getIdentifier(nombreSinExtension, "drawable", packageName)
            Log.d("DetalleLaboratorio", "Buscando imagen: $nombreSinExtension con ID: $resourceId")

            if (resourceId != 0) {
                imageViewSalon.setImageResource(resourceId)
            } else {
                imageViewSalon.setImageResource(R.drawable.logo_upn)
            }
        } else {
            imageViewSalon.setImageResource(R.drawable.logo_upn)
        }
        btnMostrarRecorrido.setOnClickListener {
            mostrarDialogoRecorrido(imgRecorridoNombre)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun mostrarDialogoRecorrido(imgRecorridoNombre: String?) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_recorrido)
        val imageView = dialog.findViewById<ImageView>(R.id.imgRecorrido)

        if (!imgRecorridoNombre.isNullOrEmpty()){
            val nombreSinExtension = imgRecorridoNombre.substringBeforeLast(".")
            val resourceId = resources.getIdentifier(nombreSinExtension, "drawable", packageName)
            if (resourceId != 0) {
                imageView.setImageResource(resourceId)
            } else {
                imageView.setImageResource(R.drawable.logo_upn)
            }
        }else{
            imageView.setImageResource(R.drawable.logo_upn)
        }
        dialog.show()
    }
}