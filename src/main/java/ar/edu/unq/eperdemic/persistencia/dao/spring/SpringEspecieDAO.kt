package ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring

import ar.edu.unq.eperdemic.modelo.Especie
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface SpringEspecieDAO: CrudRepository <Especie, Long> {
    @Query(
        "SELECT count(*) FROM Vector v " +
                "JOIN v.infecciones e " +
                "WHERE e.id = :especieId"
    )
    fun contarVectoresPorEspecie(especieId: Long): Long

    @Query(
        """
    SELECT e.nombre FROM Vector v
    JOIN v.infecciones e 
    WHERE v.ubicacion.nombre = ?1
    GROUP BY e
    ORDER BY count(v) DESC
    """
    )
    fun infectadorProfesionalEn(nombreDeLaUbicacion: String): String

    @Query(
        "SELECT e FROM Vector v " +
                "JOIN v.infecciones e " +
                "WHERE v.tipoVector = 'HUMANO' " +
                "GROUP BY e " +
                "ORDER BY COUNT(v) DESC"
    )
    fun especieLider(): Especie?

    @Query(
        "SELECT e FROM Vector v " +
                "JOIN v.infecciones e " +
                "WHERE v.tipoVector = 'HUMANO' OR v.tipoVector = 'ANIMAL' " +
                "GROUP BY e " +
                "ORDER BY COUNT(v) DESC "
    )
    fun lideres(): List<Especie>

    // TODO (y el tipo insecto?)

    @Query(
        """
           SELECT e 
           FROM Especie e JOIN e.patogeno p
           WHERE p.id = :patogenoId
        """
    )
    fun findAllByPatogenoId(patogenoId:Long):List<Especie>

    @Query(
        """
            SELECT count(*) FROM Vector v
            JOIN  v.infecciones e
            WHERE e.id = :especieId
        """
    ) fun cantidadDeInfectados(especieId: Long): Int

    @Query("""
        SELECT COUNT(DISTINCT v.ubicacion.nombre)
        FROM Vector v JOIN v.infecciones e
        WHERE e.id = :especieId
        """)
    fun ubicacionesDeEspecie(especieId: Long): Int

}