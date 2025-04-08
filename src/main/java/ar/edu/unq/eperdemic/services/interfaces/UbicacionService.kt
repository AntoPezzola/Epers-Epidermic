package ar.edu.unq.eperdemic.services.interfaces

import ar.edu.unq.eperdemic.modelo.TipoCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.UbicacionNeo
import ar.edu.unq.eperdemic.modelo.Vector
import kotlin.jvm.Throws


interface UbicacionService {
    fun crearUbicacion(ubicacion: Ubicacion): Ubicacion
    fun crearUbicacionNeo(ubicacion: UbicacionNeo): UbicacionNeo
    fun actualizarUbicacion(ubicacion: Ubicacion)
    fun recuperarUbicacion(ubicacionId: Long): Ubicacion
    fun recuperarTodasLasUbicaciones(): List<Ubicacion>
    fun clearAll()
    fun mover(vectorId: Long, ubicacionId: Long)
    fun expandir(ubicacionId: Long)
    fun recuperarUbicacionCon(nombreDeLaUbicacion: String): Ubicacion
    fun cantidadDeVectores(ubicacionId: Long): Int
    fun obtenerVectoresDeUbicacion(ubicacionId: Long): List<Vector>
    fun conectar(nombreDeUbicacion1: String, nombreDeUbicacion2: String, tipoCamino: TipoCamino)
    fun conectados(nombreDeUbicacion: String): List<Ubicacion>
    fun obtenerVectoresInfectadosDeUbicacion(ubicacionId: Long): List<Vector>
    fun moverPorCaminoMasCorto(vectorId: Long, nombreUbicacion: String)
    fun capacidadDeExpansion(vectorId: Long, nombreDeUbicacion:String, movimientos:Int): Int
    fun recuperarUbicacionConNEO(nombreUbi:String) :UbicacionNeo
    fun puedeMover(nombreUbiOrigen:String, nombreDestino:String,vector: Vector):Boolean
    fun moverAlVectorPorCamino(vector: Vector, camino: List<UbicacionNeo>)
    fun actualizarVectoresEnUbicacion(ubiId: Long)
}