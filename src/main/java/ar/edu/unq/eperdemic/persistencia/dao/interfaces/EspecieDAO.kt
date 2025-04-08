package ar.edu.unq.eperdemic.persistencia.dao.interfaces

import ar.edu.unq.eperdemic.modelo.Especie

interface EspecieDAO {

    fun crearEspecie(especie:Especie):Especie
    fun actualizar(especie: Especie)
    fun recuperar(especieId: Long): Especie
    fun recuperarATodos() : List<Especie>
    fun cantidadDeInfectados(especieId: Long ): Int
    fun infectadorProfesionalEn(nombreDeLaUbicacion:String):String
    fun especieLider(): Especie?
    fun lideres(): List<Especie>

}