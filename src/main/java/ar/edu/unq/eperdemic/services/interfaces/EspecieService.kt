package ar.edu.unq.eperdemic.services.interfaces

import ar.edu.unq.eperdemic.modelo.Especie

interface EspecieService {
    fun crearEspecie(especie: Especie):Especie
    fun actualizar(especie:Especie)
    fun recuperar(especieId:Long) : Especie
    fun recuperarTodasLasEspecies() : List<Especie>
    fun cantidadDeInfectados(especieId: Long ): Int
    fun infectadorProfesionalEn(nombreDeLaUbicacion: String) :String
    fun clearAll()

}
