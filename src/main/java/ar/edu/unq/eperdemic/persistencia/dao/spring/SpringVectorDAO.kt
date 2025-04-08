package ar.edu.unq.eperdemic.persistencia.dao.spring

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface SpringVectorDAO: CrudRepository<Vector, Long> {

    @Query(
        """
         SELECT e FROM Vector v
         JOIN  v.infecciones e
         WHERE v.id = :vectorId
        """
    )
    fun enfermedades(vectorId: Long): List<Especie>

    @Query(
        """
        SELECT count(*) 
        FROM Vector v 
        WHERE v.ubicacion.nombre = ?1"""
    )
    fun cantEn(ubicacionNombre: String):Int
}