package ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface SpringUbicacionDAO: CrudRepository<Ubicacion, Long> {

  fun findByNombre(nombre: String) : Ubicacion

    @Query(
        "SELECT  u " +
                " from Ubicacion u " +
                "WHERE u.nombre = ?1 "
    )
    fun recuperarUbicacionCon(nombre:String): Ubicacion


    @Query(
        """
            SELECT DISTINCT v
            FROM Vector v
            JOIN v.ubicacion u
            JOIN v.infecciones e
            WHERE u.id = :ubicacionId
        """
    )
    fun obtenerVectoresInfectadosDeUbicacion(ubicacionId: Long): List<Vector>

    @Query(
        "SELECT v " +
                "FROM Vector v " +
                "JOIN v.ubicacion u " +
                "WHERE u.id = ?1"
    )
    fun obtenerVectoresDeUbicacion(ubicacionId: Long): List<Vector>

    @Query ("""
        SELECT DISTINCT v FROM Vector v JOIN v.ubicacion u 
        WHERE u.id = ?1 AND EXISTS (SELECT 1 FROM v.infecciones i ) 
        ORDER BY RAND() 
        """)
    fun obtenerRandomInfectado(ubicacionId: Long) : Vector?

    @Query("""
            
                SELECT count(*) 
                FROM Vector v
                JOIN  v.ubicacion u
                WHERE u.id = :ubicacionId
                    
        """)
    fun cantidadDeVectores(ubicacionId: Long): Int

   @Query(
     """
         SELECT count(*)
         FROM Ubicacion
     """
    )
    fun cantDeUbicaciones():Int
}