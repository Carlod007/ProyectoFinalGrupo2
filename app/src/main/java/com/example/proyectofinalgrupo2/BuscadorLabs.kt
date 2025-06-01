package com.example.proyectofinalgrupo2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinalgrupo2.databinding.ActivityBuscadorLabsBinding

class BuscadorLabs : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorLabsBinding

    @SuppressLint("ClickableViewAccessibility")
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
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
        val editTextSearch = binding.editTextText
        editTextSearch.setOnTouchListener {
            v, event ->
            val editText = v as? EditText ?: return@setOnTouchListener false
            if (event.action == MotionEvent.ACTION_UP){
                val drawableEnd = editText.compoundDrawablesRelative[2]
                if (drawableEnd != null){
                    if (event.x >= (editText.width - editText.paddingRight - drawableEnd.intrinsicWidth)){
                        val query = editText.text.toString()
                        Toast.makeText(this, "Lupa clickeada. Buscando: $query", Toast.LENGTH_SHORT).show()
                        v.performClick()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }
}