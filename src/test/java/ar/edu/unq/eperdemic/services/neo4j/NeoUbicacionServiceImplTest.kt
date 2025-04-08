package ar.edu.unq.eperdemic.services.neo4j

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.persistencia.dao.interfaces.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4J.UbicacionNeoDAO
import ar.edu.unq.eperdemic.services.interfaces.UbicacionService
import ar.edu.unq.eperdemic.services.interfaces.VectorService
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring.SpringUbicacionDAO
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.Camino
import ar.edu.unq.eperdemic.modelo.TipoCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.UbicacionNeo
import ar.edu.unq.eperdemic.services.interfaces.*

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NeoUbicacionServiceImplTest {

    @Autowired
    lateinit var neoDAO: UbicacionNeoDAO

    @Autowired
    lateinit var neoService: UbicacionService


    @BeforeEach
    fun save() {
    }

    @Test
    fun crearUbicacionYrecuperarPorSuNombre() {
        val ubicacionCreada = neoDAO.save(UbicacionNeo("Hungria"))
        val recuperada = neoDAO.findByNombre(ubicacionCreada.nombre)

        Assertions.assertNotNull(recuperada) // Asegúrate de que no sea nulo
        Assertions.assertEquals(ubicacionCreada.nombre, recuperada?.nombre)
    }

    @Test
    fun seActualizaUnaUbicacion() {
        var ubicacionCreada = neoDAO.save(UbicacionNeo("Shapan"))
        var ubiDestino = neoDAO.save(UbicacionNeo("Shapan"))

        ubicacionCreada.nombre = "NuevoShapan"
        val camino = Camino(ubiDestino, TipoCamino.CAMINOMARITIMO)
        ubicacionCreada.agregarCamino(camino)

        neoDAO.save(ubicacionCreada)


        val recuperada = neoDAO.findByNombre("NuevoShapan")


        Assertions.assertEquals(ubicacionCreada.nombre, "NuevoShapan")
        Assertions.assertTrue(ubicacionCreada.caminos.contains(camino))
        Assertions.assertEquals(ubicacionCreada.id, recuperada!!.id)
    }

    fun seEliminaUnaUbicacionQuExiste() {
        var ubicacionCreada = neoDAO.save(UbicacionNeo("Shapan"))
        neoDAO.delete(ubicacionCreada)
        val recuperada = neoDAO.findByNombre("NuevoShapan")

        Assertions.assertNull(recuperada) // Asegúrate de que no sea nulo

    }

    @Test
    fun recuperoUnaUbicacionDeNEO() {
        var ubiNeo2 = UbicacionNeo("Uruguay")
        neoDAO.save(ubiNeo2)

        var recuperadaConNeo = neoService.recuperarUbicacionConNEO("Uruguay")


        Assertions.assertEquals(recuperadaConNeo.nombre, "Uruguay")
        Assertions.assertEquals(recuperadaConNeo.caminos.size, 0)

    }


    @AfterEach
    fun clearAll() {
        neoService.clearAll()
        neoDAO.deleteAll()
    }

}