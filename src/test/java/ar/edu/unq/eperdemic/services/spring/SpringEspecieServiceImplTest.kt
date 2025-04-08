package ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringEspecieServiceImpl
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringUbicacionServiceImpl
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorPatogenoNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorUbicacionNoExiste
import ar.edu.unq.eperdemic.services.interfaces.EspecieService
import ar.edu.unq.eperdemic.services.interfaces.PatogenoService
import ar.edu.unq.eperdemic.services.interfaces.UbicacionService
import ar.edu.unq.eperdemic.services.interfaces.VectorService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringEspecieServiceImplTest {

    @Autowired
    lateinit var serviceEspecie : EspecieService
    @Autowired
    lateinit var servicePatogeno : PatogenoService
    @Autowired
    lateinit var serviceUbicacion: UbicacionService
    @Autowired
    lateinit var serviceVector: VectorService

    lateinit var especie: Especie
    lateinit var hungria: Ubicacion
    lateinit var china : Ubicacion
    lateinit var patogenoVirus : Patogeno


    @BeforeEach
    fun prepare() {

        hungria =  serviceUbicacion.crearUbicacion( Ubicacion("Hungria"))
        china = serviceUbicacion.crearUbicacion( Ubicacion("China"))

        patogenoVirus = Patogeno("Virus", 70, 70, 70, 70, 70)


    }


    @Test
    fun comprobarQueSeCrearUnaEspecie(){
        val patoCreado = servicePatogeno.crearPatogeno(patogenoVirus)
        serviceVector.crearVector(Vector(TipoVector.HUMANO, china))
        val especieCreada = servicePatogeno.agregarEspecie(patoCreado.id!!, "T", china.id!!)

        Assertions.assertNotNull(especieCreada)
    }

    @Test
    fun comprobarQueSiNoExisteElPatogenoEnDBNoSeCreaLaEspecie(){
        var patogenoNoPersistido =  Patogeno("Virus", 12, 23, 12, 23,42)

        especie = Especie(patogenoNoPersistido,"UnNombreDeEspecie",china)

        Assertions.assertEquals("El patogeno no está creado en DB",
            (assertThrows<ErrorPatogenoNoExiste>{ serviceEspecie.crearEspecie(especie) } ).message)

    }

    @Test
    fun comprobarQueSeCreaConUnaUbicacionExistente() {
        var patoCreado = servicePatogeno.crearPatogeno(Patogeno("Hongo", 70, 70, 70, 70,70))
        especie = Especie(patoCreado,"UnNombreDeEspecie",hungria)

        var especieCreada = serviceEspecie.crearEspecie(especie)

        Assertions.assertEquals(especieCreada.paisDeOrigen!!.nombre, hungria.nombre)
        Assertions.assertEquals(especieCreada.paisDeOrigen!!.id, hungria.id)
    }

    @Test
    fun comprobarQueLaEspecieNoSeCreaSinUnaUbicacionExistente() {
        var ubicacion = Ubicacion("Peru")
        var patoCreado = servicePatogeno.crearPatogeno(Patogeno("Virus", 12, 23, 12, 23,42))

        especie = Especie(patoCreado,"UnNombreDeEspecie", ubicacion)

        Assertions.assertEquals("La Ubicacion no existe en la base de datos",
            (assertThrows<ErrorUbicacionNoExiste>{ serviceEspecie.crearEspecie(especie)  } ).message)

    }

    @Test
    fun comprobarQueSeActualizaUnaEspecie(){

        serviceVector.crearVector(Vector(TipoVector.HUMANO, china))
        val patoCreado = servicePatogeno.crearPatogeno(Patogeno("Virus", 12, 23, 12, 23,42))
        val especieCreada = servicePatogeno.agregarEspecie(patoCreado.id!!, "Estafilococo", china.id!!)

        patoCreado.tipo = "TPato2"
        servicePatogeno.actualizarPatogeno(patoCreado)
        val patoActualizado = servicePatogeno.recuperarPatogeno(patoCreado.id)

        especieCreada.patogeno = patoActualizado
        especieCreada.nombre = "OtroNombreActualizar"
        especieCreada.paisDeOrigen = hungria


        serviceEspecie.actualizar(especieCreada)
        var espActualizado = serviceEspecie.recuperar(especieCreada.id!!)


        Assertions.assertEquals(espActualizado.patogeno!!.tipo, patoActualizado.tipo)
        Assertions.assertEquals(espActualizado.nombre, "OtroNombreActualizar")
        Assertions.assertEquals(espActualizado.paisDeOrigen!!.nombre, hungria.nombre)


    }

    @Test
    fun seCompruebaQueSeRecuperanTodasLasEspecies(){
        var patoCreado = servicePatogeno.crearPatogeno(Patogeno("Virus", 12, 23, 12, 23,42))
        especie = Especie(patoCreado,"especieEnLista",china)
        var especieCreada = serviceEspecie.crearEspecie(especie)

        var recuperados = serviceEspecie.recuperarTodasLasEspecies()

        Assertions.assertTrue(recuperados.any { e -> (e.id == especieCreada.id && e.nombre == especieCreada.nombre) })

    }
    @Test
    fun traeUnaListaVaciaSiNoHayEspeciesQueTraer() {
        Assertions.assertTrue(serviceEspecie.recuperarTodasLasEspecies().isEmpty())
    }

    @Test
    fun seCorrboraQueLaCantidadDeRecuperadosParaUnaEspecieQueNoInfestoAVectoresEsCero(){
        var patoCreado = servicePatogeno.crearPatogeno(Patogeno("Virus", 12, 23, 12, 23,42))
        especie = Especie(patoCreado,"especieEnLista",china)
        var especieCreada = serviceEspecie.crearEspecie(especie)

        val esperado = serviceEspecie.cantidadDeInfectados(especieCreada.id!!)
        Assertions.assertEquals(esperado, 0)
    }
    @Test
    fun seCorrboraQueLaCantidadDeRecuperadosParaUnaEspecieQueInfectaUnVectorEs1() {
        var sudan =  serviceUbicacion.crearUbicacion(Ubicacion("Sudan"))
        var vectorCreado =  serviceVector.crearVector(Vector(TipoVector.ANIMAL, sudan))
        var patoCreado = servicePatogeno.crearPatogeno(Patogeno("Virus", 12, 23, 12, 23,42))
        var especieCreada = serviceEspecie.crearEspecie( Especie(patoCreado,"especieEnLista",sudan))

        serviceVector.infectar(vectorCreado.id!!, especieCreada.id!!)
        val esperado = serviceEspecie.cantidadDeInfectados(especieCreada.id!!)
        Assertions.assertEquals(esperado, 1)

    }
    @AfterEach
    fun cleanup() {
        servicePatogeno.clearAll()
        serviceEspecie.clearAll()
        serviceVector.clearAll()
        serviceUbicacion.clearAll()
    }

}


