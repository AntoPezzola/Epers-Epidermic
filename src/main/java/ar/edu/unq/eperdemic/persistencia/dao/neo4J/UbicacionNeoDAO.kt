package ar.edu.unq.eperdemic.persistencia.dao.neo4J


import ar.edu.unq.eperdemic.modelo.Camino
import ar.edu.unq.eperdemic.modelo.TipoCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.UbicacionNeo
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UbicacionNeoDAO : Neo4jRepository<UbicacionNeo, Long?> {

    fun findByNombre(nombreUbi: String): UbicacionNeo?

    @Query(
        """
    MATCH (upartida:UbicacionNeo {nombre: ${'$'}nombreDeUbicacion })
    MATCH (upartida)-[caminos]->(u:UbicacionNeo)
    RETURN u
    """
    )
    fun conectados(nombreDeUbicacion: String): List<UbicacionNeo>

    @Query(
        """ 
        MATCH (destino:UbicacionNeo {nombre:${'$'}origen})
        OPTIONAL MATCH (origen)-[caminos]->(destino)
        RETURN caminos.tipo IN ${'$'}caminosValidos
        """
    )
    fun puedeLlegarAlDestino(
        origen: String,
        destino: String,
        caminosValidos: MutableList<String>
    ): Optional<Boolean>


    @Query(
        """MATCH (u1:UbicacionNeo {nombre: ${'$'}ubiOrigen})-[c:caminos]->(u2:UbicacionNeo {nombre: ${'$'}ubiDestino}) 
           RETURN c.tipo
        """
    )
    fun hayAlgunTipoDeCaminoDisponibleDesdeHasta(ubiOrigen:String, ubiDestino:String): Optional<String>


    @Query(
        """
     MATCH (inicio: UbicacionNeo {nombre: ${'$'}nombreUbicacionOrigen})
     MATCH (fin: UbicacionNeo {nombre: ${'$'}nombreUbicacionDestino})
     MATCH p = (inicio)-[:caminos*]->(fin)
     WHERE ALL(relationship in relationships(p) WHERE relationship.tipo IN ${'$'}posiblesRecorridosSegunVector)
     WITH nodes(p) AS ubicaciones
     ORDER BY length(p) ASC
     LIMIT 1
     UNWIND tail(ubicaciones) as ubicacionesSinOrigen
     RETURN ubicacionesSinOrigen
    """
    )
    fun encontrarCaminoMasCorto(
        nombreUbicacionOrigen: String,
        nombreUbicacionDestino: String,
        posiblesRecorridosSegunVector: MutableList<String>
    ): List<UbicacionNeo>

    @Query(
        """  
            MATCH (u:UbicacionNeo{nombre: ${'$'}nombreDeUbicacion})
            MATCH p = (u)-[r*0..]->(u2) 
            WHERE ALL(lr in r WHERE lr.tipo in ${'$'}caminosPermitidos)
            WITH length(p) - ${'$'}movimientos as prof
            WHERE prof <= 0
            RETURN count(prof)
        """
    )
    fun cantidadDeUbicacionesParaExpansion(caminosPermitidos: List<TipoCamino>, nombreDeUbicacion:String, movimientos:Int): Int
}