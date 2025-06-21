package com.example.proyectofinalgrupo2.servicio

import com.example.proyectofinalgrupo2.model.Laboratorios
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object AppConstantes{
    const val BASE_URL = "http://192.168.18.133:5000"
}

// Data class para las sugerencias
data class Sugerencia(
    val lab_codigo: String,
    val lab_nombre: String
)

interface WebService {
    @GET("/laboratorios")
    suspend fun cargarLaboratorios(): Response<LaboratoriosResponse>

    @GET("/laboratorios/{codigo}")
    suspend fun buscarLaboratoriosCodigo(@retrofit2.http.Path("codigo") codigo: String): Response<Laboratorios>

    @GET("/laboratorios/sugerencias/{texto}")
    suspend fun obtenerSugerencias(@retrofit2.http.Path("texto") texto: String): Response<List<Sugerencia>>
}

object RetrofitClient{
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WebService::class.java)
    }
}