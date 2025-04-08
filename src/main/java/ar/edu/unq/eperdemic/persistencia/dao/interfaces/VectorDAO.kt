package ar.edu.unq.eperdemic.persistencia.dao.interfaces

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector

interface VectorDAO {

    fun crear(vector : Vector) : Vector
    fun actualizar(vector : Vector)
    fun recuperar(id : Long) : Vector
    fun recuperarTodos() : List<Vector>
    fun enfermedades(vectorId: Long): List<Especie>
    fun cantEn(ubicacion: String):Int
}