package com.example.proyectofinalgrupo2

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_laboratorio)

        tvNombre = findViewById(R.id.tvNombre)
        tvPiso = findViewById(R.id.tvPiso)
        tvPabellon = findViewById(R.id.tvPabellon)

        val codigo = intent.getStringExtra("codigo")
        val nombre = intent.getStringExtra("nombre")
        val piso = intent.getIntExtra("piso", 0)
        val pabellon = intent.getStringExtra("pabellon")


        tvNombre.text = nombre
        tvPiso.text = "Piso $piso"
        tvPabellon.text = "Pabellón $pabellon"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}