package com.example.proyectofinalgrupo2.servicio

import com.example.proyectofinalgrupo2.model.Laboratorios
import com.google.gson.annotations.SerializedName

data class LaboratoriosResponse(
    @SerializedName("listaLaboratorios") var listaLaboratorios: ArrayList<Laboratorios>
)
