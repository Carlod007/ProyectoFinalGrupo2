package com.example.proyectofinalgrupo2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinalgrupo2.model.Laboratorios
import com.example.proyectofinalgrupo2.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class AgregarLaboratorioActivity : AppCompatActivity() {

    private lateinit var etCodigo: EditText
    private lateinit var etNombre: EditText
    private lateinit var etPiso: EditText
    private lateinit var etPabellon: EditText
    private lateinit var etImgSalon: EditText
    private lateinit var etImgRecorrido: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnEditar: Button

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
        btnEliminar = findViewById(R.id.btnEliminar)
        btnEditar = findViewById(R.id.btnEditar)

        btnGuardar.setOnClickListener {
            agregarLaboratorio()
        }

        btnEliminar.setOnClickListener {
            val codigo = etCodigo.text.toString().trim()
            if (codigo.isEmpty()) {
                Toast.makeText(this, "Ingresa el código a eliminar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de eliminar el laboratorio con código $codigo?")
                .setPositiveButton("Sí") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = RetrofitClient.webService.eliminarLaboratorio(codigo)
                            runOnUiThread {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@AgregarLaboratorioActivity, "Laboratorio eliminado", Toast.LENGTH_SHORT).show()
                                    clearFields()
                                } else {
                                    // Parse error message from server
                                    val errorBody = response.errorBody()?.string()
                                    val errorMessage = try {
                                        JSONObject(errorBody ?: "{}").getString("mensaje")
                                    } catch (e: Exception) {
                                        "Error al eliminar"
                                    }
                                    Toast.makeText(this@AgregarLaboratorioActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: HttpException) {
                            runOnUiThread {
                                val errorMessage = try {
                                    JSONObject(e.response()?.errorBody()?.string() ?: "{}").getString("mensaje")
                                } catch (ex: Exception) {
                                    "Error al eliminar"
                                }
                                Toast.makeText(this@AgregarLaboratorioActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(this@AgregarLaboratorioActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        btnEditar.setOnClickListener {
            val codigo = etCodigo.text.toString().trim()
            if (codigo.isEmpty()) {
                Toast.makeText(this, "Ingresa el código a editar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Fetch existing laboratory data
                    val response = RetrofitClient.webService.buscarLaboratoriosCodigo(codigo)
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = try {
                                JSONObject(errorBody ?: "{}").getString("mensaje")
                            } catch (e: Exception) {
                                "Laboratorio no encontrado"
                            }
                            Toast.makeText(this@AgregarLaboratorioActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val existingLab = response.body() ?: return@launch runOnUiThread {
                        Toast.makeText(this@AgregarLaboratorioActivity, "Error al obtener datos del laboratorio", Toast.LENGTH_SHORT).show()
                    }

                    // Create updated laboratory object, keeping existing values for empty fields
                    val lab = Laboratorios(
                        lab_id = existingLab.lab_id,
                        lab_codigo = codigo,
                        lab_nombre = etNombre.text.toString().trim().ifEmpty { existingLab.lab_nombre },
                        lab_piso = etPiso.text.toString().toIntOrNull() ?: existingLab.lab_piso,
                        lab_pabellon = etPabellon.text.toString().trim().ifEmpty { existingLab.lab_pabellon },
                        lab_imgsalon = etImgSalon.text.toString().trim().ifEmpty { existingLab.lab_imgsalon },
                        lab_imgrecorrido = etImgRecorrido.text.toString().trim().ifEmpty { existingLab.lab_imgrecorrido },
                        lab_descripcion = etDescripcion.text.toString().trim().ifEmpty { existingLab.lab_descripcion }
                    )

                    // Send update request
                    val updateResponse = RetrofitClient.webService.editarLaboratorio(codigo, lab)
                    runOnUiThread {
                        if (updateResponse.isSuccessful) {
                            Toast.makeText(this@AgregarLaboratorioActivity, "Laboratorio actualizado", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorBody = updateResponse.errorBody()?.string()
                            val errorMessage = try {
                                JSONObject(errorBody ?: "{}").getString("mensaje")
                            } catch (e: Exception) {
                                "Error al actualizar"
                            }
                            Toast.makeText(this@AgregarLaboratorioActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: HttpException) {
                    runOnUiThread {
                        val errorMessage = try {
                            JSONObject(e.response()?.errorBody()?.string() ?: "{}").getString("mensaje")
                        } catch (ex: Exception) {
                            "Error al obtener datos del laboratorio"
                        }
                        Toast.makeText(this@AgregarLaboratorioActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@AgregarLaboratorioActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun agregarLaboratorio() {
        // Validate all fields are filled
        val codigo = etCodigo.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val piso = etPiso.text.toString().trim()
        val pabellon = etPabellon.text.toString().trim()
        val imgSalon = etImgSalon.text.toString().trim()
        val imgRecorrido = etImgRecorrido.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()

        if (codigo.isEmpty() || nombre.isEmpty() || piso.isEmpty() || pabellon.isEmpty() || imgSalon.isEmpty() || imgRecorrido.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios para agregar un laboratorio", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate piso is a valid integer
        val pisoInt = piso.toIntOrNull()
        if (pisoInt == null) {
            Toast.makeText(this, "El piso debe ser un número válido", Toast.LENGTH_SHORT).show()
            return
        }

        val laboratorio = Laboratorios(
            lab_id = 0,
            lab_codigo = codigo,
            lab_nombre = nombre,
            lab_piso = pisoInt,
            lab_pabellon = pabellon,
            lab_imgsalon = imgSalon,
            lab_imgrecorrido = imgRecorrido,
            lab_descripcion = descripcion
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.agregarLaboratorio(laboratorio)
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AgregarLaboratorioActivity, "Laboratorio agregado correctamente", Toast.LENGTH_SHORT).show()
                        clearFields()
                        finish()
                    } else {
                        // Parse error message from server
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody ?: "{}").getString("mensaje")
                        } catch (e: Exception) {
                            "Error al agregar laboratorio"
                        }
                        Toast.makeText(this@AgregarLaboratorioActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: HttpException) {
                runOnUiThread {
                    val errorMessage = try {
                        JSONObject(e.response()?.errorBody()?.string() ?: "{}").getString("mensaje")
                    } catch (ex: Exception) {
                        "Error al agregar laboratorio"
                    }
                    Toast.makeText(this@AgregarLaboratorioActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AgregarLaboratorioActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearFields() {
        etCodigo.text.clear()
        etNombre.text.clear()
        etPiso.text.clear()
        etPabellon.text.clear()
        etImgSalon.text.clear()
        etImgRecorrido.text.clear()
        etDescripcion.text.clear()
    }
}