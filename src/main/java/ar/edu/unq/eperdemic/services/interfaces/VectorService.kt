package ar.edu.unq.eperdemic.services.interfaces

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector

interface VectorService {
    fun crearVector(vector: Vector):Vector
    fun actualizarVector(vector: Vector)
    fun recuperarVector(vectorId: Long):Vector
    fun recuperarTodosLosVectores():List<Vector>
    fun infectar(vectorId: Long, especieId: Long)
    fun enfermedades(vectorId: Long): List<Especie>
    fun cantEn(nombreDeLaUbicacion: String):Int
    fun contagiarAVectores(vectorInfectado: Vector, vectores: List<Vector>)

    fun clearAll()
}