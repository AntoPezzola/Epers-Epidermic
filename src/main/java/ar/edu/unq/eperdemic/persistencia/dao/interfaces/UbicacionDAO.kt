package ar.edu.unq.eperdemic.persistencia.dao.interfaces

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector

interface UbicacionDAO{
    fun crear(ubicacion: Ubicacion): Ubicacion
    fun actualizar(ubicacion: Ubicacion)
    fun recuperar(idDeUbicacion: Long): Ubicacion
    fun recuperarATodos() : List<Ubicacion>
    fun recuperarUbicacionCon(nombre:String): Ubicacion
    fun obtenerVectoresInfectadosDeUbicacion(ubicacionId: Long): List<Vector>
    fun obtenerVectoresDeUbicacion(ubicacionId: Long): List<Vector>
    fun obtenerRandomInfectado(ubicacionId: Long): Vector
    fun cantidadDeVectores(unaUbicacion: Ubicacion): Int
}
