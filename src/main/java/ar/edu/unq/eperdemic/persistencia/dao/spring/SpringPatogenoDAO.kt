package ar.edu.unq.eperdemic.persistencia.dao.spring

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface SpringPatogenoDAO : CrudRepository<Patogeno, Long> {

    @Query("""SELECT DISTINCT e FROM Especie e WHERE e.patogeno.id = ?1"""  )
    fun especiesDePatogeno(patogenoId:Long) : List<Especie>


    @Query("SELECT COUNT(*) / 2 AS CantidadTotalUbicaciones  FROM Ubicacion")
    fun esPandemia(especieId: Long): Boolean

    //fun esPandemia(especieId: Long):Boolean
      // mitadCantUbicaciones() > ubicacionesDeEspecie(especieId)

//    @Query("SELECT COUNT(*) / 2 AS CantidadTotalUbicaciones FROM Ubicacion")
//    fun mitadCantUbicaciones():Int
//

}