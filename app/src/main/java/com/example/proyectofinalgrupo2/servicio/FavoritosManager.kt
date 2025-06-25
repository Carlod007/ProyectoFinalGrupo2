package com.example.proyectofinalgrupo2.servicio

import android.content.Context
import com.example.proyectofinalgrupo2.model.Laboratorios
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritosManager {
    private fun getPrefs(context: Context, usuario: String) =
        context.getSharedPreferences("favoritos_$usuario", Context.MODE_PRIVATE)

    fun agregarFavorito(context: Context, usuario: String, lab: Laboratorios) {
        val prefs = getPrefs(context, usuario)
        val favoritos = obtenerFavoritos(context, usuario).toMutableList()
        favoritos.add(lab)
        val json = Gson().toJson(favoritos)
        prefs.edit().putString("favoritos", json).apply()
    }

    fun obtenerFavoritos(context: Context, usuario: String): List<Laboratorios> {
        val prefs = getPrefs(context, usuario)
        val json = prefs.getString("favoritos", null) ?: return emptyList()
        val type = object : TypeToken<List<Laboratorios>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun eliminarFavorito(context: Context, usuario: String, codigo: String) {
        val prefs = getPrefs(context, usuario)
        val favoritos = obtenerFavoritos(context, usuario).filter { it.lab_codigo != codigo }
        val json = Gson().toJson(favoritos)
        prefs.edit().putString("favoritos", json).apply()
    }
}
