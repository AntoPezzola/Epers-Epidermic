package ar.edu.unq.eperdemic.persistencia.dao.neo4J

import ar.edu.unq.eperdemic.modelo.Camino
import ar.edu.unq.eperdemic.modelo.UbicacionNeo
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository

@Repository

interface CaminoNeoDAO: Neo4jRepository<Camino, Long?> {
}