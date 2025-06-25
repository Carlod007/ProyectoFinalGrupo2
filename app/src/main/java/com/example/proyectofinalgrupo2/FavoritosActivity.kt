package com.example.proyectofinalgrupo2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinalgrupo2.servicio.FavoritosManager

class FavoritosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favoritos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usuario = intent.getStringExtra("usuario") ?: "default"

        val listView = findViewById<ListView>(R.id.listViewFavoritos)
        val favoritos = FavoritosManager.obtenerFavoritos(this, usuario)

        val codigos = favoritos.map { "${it.lab_codigo} - ${it.lab_nombre}" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, codigos)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val lab = favoritos[position]
            val intent = Intent(this, DetalleLaboratorioActivity::class.java)
            intent.putExtra("codigo", lab.lab_codigo)
            intent.putExtra("nombre", lab.lab_nombre)
            intent.putExtra("piso", lab.lab_piso)
            intent.putExtra("pabellon", lab.lab_pabellon)
            intent.putExtra("imgRecorrido", lab.lab_imgrecorrido)
            intent.putExtra("imgSalon", lab.lab_imgsalon)
            intent.putExtra("descripcion", lab.lab_descripcion)
            intent.putExtra("usuario", usuario) // pásalo aquí también para mantener la sesión
            startActivity(intent)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val lab = favoritos[position]
            FavoritosManager.eliminarFavorito(this, usuario, lab.lab_codigo)
            Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()

            // Actualizar la lista después de eliminar
            val nuevosFavoritos = FavoritosManager.obtenerFavoritos(this, usuario)
            val nuevosCodigos = nuevosFavoritos.map { "${it.lab_codigo} - ${it.lab_nombre}" }
            val nuevoAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nuevosCodigos)
            listView.adapter = nuevoAdapter

            true  // Indicar que el long click se manejó
        }
    }
}