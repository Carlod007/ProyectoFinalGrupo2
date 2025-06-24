package com.example.proyectofinalgrupo2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectofinalgrupo2.model.CredencialesRequest
import com.example.proyectofinalgrupo2.servicio.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var txtUsuario: TextInputEditText
    private lateinit var txtContraseña: TextInputEditText
    private lateinit var btnAcceder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txtUsuario = findViewById(R.id.txtUsuario)
        txtContraseña = findViewById(R.id.txtContraseña)
        btnAcceder = findViewById(R.id.btnAcceder)

        btnAcceder.setOnClickListener {
            val usuario = txtUsuario.text.toString().trim()
            val contrasena = txtContraseña.text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty()){
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            }else{
                login(usuario, contrasena)
            }
        }
    }

    private fun login(usuario: String, contrasena: String){
        lifecycleScope.launch {
            try {
                val credenciales = CredencialesRequest(usuario, contrasena)
                val respuesta = RetrofitClient.webService.loginUsuario(credenciales)

                if(respuesta.isSuccessful && respuesta.body() != null){
                    val user = respuesta.body()!!

                    if (user.success){
                        val intent = Intent(this@LoginActivity, BuscadorLabs::class.java)
                        intent.putExtra("tipoUsuario", user.tipo)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@LoginActivity, "Error al conectar", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}