package ar.edu.unq.eperdemic.services.interfaces

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno

interface PatogenoService {
    fun crearPatogeno(patogeno: Patogeno): Patogeno
    fun recuperarPatogeno(id: Long?): Patogeno
    fun recuperarATodosLosPatogenos(): List<Patogeno>
    fun agregarEspecie(idDePatogeno: Long, nombreEspecie: String, idUbicacion : Long) : Especie
    fun actualizarPatogeno(patogeno:Patogeno)
    fun especiesDePatogeno(patogenoId: Long ): List<Especie>
    fun esPandemia(especieId: Long): Boolean
    fun clearAll()
}