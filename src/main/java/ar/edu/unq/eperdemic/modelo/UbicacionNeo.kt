package ar.edu.unq.eperdemic.modelo

import org.springframework.data.neo4j.core.schema.*

@Node
class UbicacionNeo(var nombre: String) {

    @Id
    @GeneratedValue
    var id: Long? = null


   @Relationship(type ="caminos",direction = Relationship.Direction.INCOMING )
    var caminos : MutableList<Camino> = mutableListOf()

    fun agregarCamino(camino: Camino) {
        this.caminos.add(camino)
    }
}

