package ar.edu.unq.eperdemic.services.interfaces

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios

interface EstadisticaService {
    fun especieLider(): Especie
    fun lideres(): List<Especie>
    fun reporteDeContagios(nombreDeLaUbicacion: String): ReporteDeContagios
}