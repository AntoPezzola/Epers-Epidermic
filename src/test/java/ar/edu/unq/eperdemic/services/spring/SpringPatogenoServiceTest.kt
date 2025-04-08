package ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringEspecieServiceImpl
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringUbicacionServiceImpl
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.TipoVector
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorNoHayVectoresEnLaUbicacion
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorPatogenoNoExiste
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
class SpringPatogenoServiceTest {

    //services
     @Autowired
     lateinit var servicePatogeno: PatogenoService
     @Autowired
     lateinit var serviceUbi: UbicacionService
     @Autowired
     lateinit var  serviceEspecie : EspecieService
     @Autowired
     lateinit var  serviceVector: VectorService

    lateinit var patogenoBacteria : Patogeno
    lateinit var patogenoVirus : Patogeno
    lateinit var patogenoBacteriano : Patogeno
    lateinit var patogenoVirusito : Patogeno

    lateinit var canada: Ubicacion
    lateinit var brasil: Ubicacion
    lateinit var argentina: Ubicacion
    lateinit var australia: Ubicacion


    lateinit var vectorArgentino:Vector
    lateinit var vectorBrasilero:Vector
    lateinit var vectorCanadiense:Vector


    @BeforeEach
    fun prepare() {
        patogenoBacteria = Patogeno("Bacteria", 70, 70, 70, 70, 70)
        patogenoVirus = Patogeno("Virus", 70, 70, 70, 70, 70)
        patogenoBacteriano = Patogeno("Bacteriano", 70, 70, 70, 70, 70)
        patogenoVirusito = Patogeno("Virusito", 70, 70, 70, 70, 70)

        canada               = serviceUbi.crearUbicacion(Ubicacion("Canada"))
        brasil               = serviceUbi.crearUbicacion(Ubicacion("Brasil"))
        argentina            = serviceUbi.crearUbicacion(Ubicacion("Argentina"))
        australia            = serviceUbi.crearUbicacion(Ubicacion("Australia"))

        vectorArgentino = serviceVector.crearVector(Vector(TipoVector.HUMANO, argentina))
        vectorBrasilero = serviceVector.crearVector(Vector(TipoVector.ANIMAL, brasil))
        vectorCanadiense = serviceVector.crearVector(Vector(TipoVector.HUMANO, canada))

    }

    @Test
    fun patogenoServiceCreaUnPatogeno() {
        val unPatogeno = servicePatogeno.crearPatogeno(patogenoBacteria)

        Assertions.assertNotNull(unPatogeno.id)
        Assertions.assertEquals(unPatogeno.tipo, patogenoBacteria.tipo)
        Assertions.assertEquals(unPatogeno.cantidadDeEspecies, patogenoBacteria.cantidadDeEspecies)

    }

    @Test
    fun seVerificaQueSePuedeRecuperarElPatogenoConElId() {
        val unPatogenoPersistido = servicePatogeno.crearPatogeno(patogenoBacteria)
        val unPatogenoRecuperado = servicePatogeno.recuperarPatogeno(unPatogenoPersistido.id!!)

        Assertions.assertNotNull(unPatogenoRecuperado)
        Assertions.assertEquals(unPatogenoPersistido.id, unPatogenoRecuperado.id)
    }

    @Test
    fun cuandoUnPatogenoServiceQuiereRecuperarUnPatogenoQueNoExisteLevantaUnaExcepcion() {
        //supongo que el id no existe en la base de datos, lo verifico con el assert recuperar todos
        servicePatogeno.clearAll()
        serviceEspecie.clearAll()
        serviceVector.clearAll()
        serviceUbi.clearAll()

        Assertions.assertEquals("El patogeno no está creado en DB",
            (assertThrows<ErrorPatogenoNoExiste> { servicePatogeno.recuperarPatogeno(1) }).message
        )
        Assertions.assertTrue(servicePatogeno.recuperarATodosLosPatogenos().isEmpty())

    }

    @Test
    fun seVerificaQueSePuedeRecuperarTodosLosPatogenosPersistidos() {
        servicePatogeno.crearPatogeno(patogenoBacteria)
        servicePatogeno.crearPatogeno(patogenoVirus)

        Assertions.assertEquals(2, servicePatogeno.recuperarATodosLosPatogenos().size)
    }

    @Test
    fun patogenoServiceTraeUnaListaVaciaSiNoHayPatogenosQueTraer() {
        val recuperados = servicePatogeno.recuperarATodosLosPatogenos()

        Assertions.assertTrue(recuperados.isEmpty())
        Assertions.assertFalse(recuperados.contains(patogenoBacteria))
    }

    @Test
    fun patogenoServiceActualizaUnPatogenoCreado() {
        val patogenoCreado = servicePatogeno.crearPatogeno(patogenoBacteria)
        var recuperado = servicePatogeno.recuperarPatogeno(patogenoCreado.id!!)

        Assertions.assertEquals(recuperado.cantidadDeEspecies, 0)

        recuperado.cantidadDeEspecies = 30
        servicePatogeno.actualizarPatogeno(recuperado)
        recuperado = servicePatogeno.recuperarPatogeno(patogenoCreado.id!!)

        Assertions.assertEquals(recuperado.cantidadDeEspecies, 30)
    }

    @Test
    fun patogenoServiceAgregarUnaEspecie() {
        val hungria = serviceUbi.crearUbicacion(Ubicacion("Hungria"))
        serviceVector.crearVector(Vector(TipoVector.HUMANO, hungria))
        val patogenoCreado = servicePatogeno.crearPatogeno(patogenoBacteria)
        val especieCreada = servicePatogeno.agregarEspecie(patogenoCreado.id!!, "T", hungria.id!!)

        Assertions.assertEquals(especieCreada.nombre, "T")
        Assertions.assertEquals(especieCreada.paisDeOrigen!!.nombre, hungria.nombre)

    }

    @Test
    fun patogenoServiceAgregarUnaEspecieEInfectaUnVector() {
        val hungria = serviceUbi.crearUbicacion(Ubicacion("Hungria"))
        val vectorAnimal = serviceVector.crearVector(Vector(TipoVector.ANIMAL, hungria))

        val patogenoCreado = servicePatogeno.crearPatogeno(patogenoBacteria)
        val especieCreada = servicePatogeno.agregarEspecie(patogenoCreado.id!!, "T", hungria.id!!)

        val enfermedadesVector = serviceVector.enfermedades(vectorAnimal.id!!)


        Assertions.assertEquals(1, enfermedadesVector.size)
        Assertions.assertEquals(especieCreada.id , enfermedadesVector.get(0).id!!)

    }

    @Test
    fun unPatogenoServicePuedeRecuperarTodasLasEspeciesDeUnPatogeno(){
        val patogenoCreado      = servicePatogeno.crearPatogeno(Patogeno("Hongo", 70, 70, 70, 70, 70))
        val especieEucariota    = servicePatogeno.agregarEspecie(patogenoCreado.id!!, "Eucariota", argentina.id!!)
        val especieEstafilococo = servicePatogeno.agregarEspecie(patogenoCreado.id!!, "Estafilococo", brasil.id!!)
        val especiesDePatogeno  = servicePatogeno.especiesDePatogeno(patogenoCreado.id!!)

        Assertions.assertEquals(2, especiesDePatogeno.size)
        Assertions.assertTrue(especiesDePatogeno.any() { it.nombre == especieEucariota.nombre })
        Assertions.assertTrue(especiesDePatogeno.any() { it.nombre == especieEstafilococo.nombre })

    }

    @Test
    fun unPatogenoServiceQueCuandoSeCreaNoTieneEspecies(){
        val patogenoCreado = servicePatogeno.crearPatogeno(patogenoVirus)

        val especiesDePatogeno = servicePatogeno.especiesDePatogeno(patogenoCreado.id!!)

        Assertions.assertTrue(especiesDePatogeno.isEmpty())

    }


    @Test
    fun seVerificaQueAlIntentarAgregarUnaEspecieDePatogenoInexistenteRompe() {
        val hungria = serviceUbi.crearUbicacion(Ubicacion("Hungria"))

        Assertions.assertEquals("El patogeno no está creado en DB",
            (assertThrows<ErrorPatogenoNoExiste> {
                servicePatogeno.agregarEspecie(-1, "T", hungria.id!!)
            }).message
        )

        Assertions.assertTrue(servicePatogeno.recuperarATodosLosPatogenos().isEmpty())
    }

    @Test
    fun cuandoUnaEspecieNoSeEncuentraEnMasDeLaMitadDeLasUbicacionesNoEsPandemia() {
        val virus = servicePatogeno.crearPatogeno(patogenoBacteria)
        val bacteria = servicePatogeno.crearPatogeno(patogenoVirus)
        val bacteriano = servicePatogeno.crearPatogeno(patogenoBacteriano)

        var unaEspecie = servicePatogeno.agregarEspecie(virus.id!!, "Estafilococo", argentina.id!!)
        servicePatogeno.agregarEspecie(bacteria.id!!, "Estafilococo", brasil.id!!)
        servicePatogeno.agregarEspecie(bacteriano.id!!, "Estafilococo", brasil.id!!)

        Assertions.assertFalse(servicePatogeno.esPandemia(unaEspecie.id!!))
    }

    @Test
    fun cuandoUnaEspecieSeEncuentraEnMasDeLaMitadDeLasUbicacionesEsPandemia(){
        val patogenoCreado = servicePatogeno.crearPatogeno(patogenoVirus)
        val idDelPatogeno   = patogenoCreado.id

        val especieEucariota = servicePatogeno.agregarEspecie(idDelPatogeno!!, "Eucariota", argentina.id!!)
        serviceVector.infectar(vectorBrasilero.id!!, especieEucariota.id!!)
        serviceVector.infectar(vectorCanadiense.id!!, especieEucariota.id!!)

        Assertions.assertTrue(servicePatogeno.esPandemia(especieEucariota.id!!))
    }


    @AfterEach
    fun tearDown() {
        servicePatogeno.clearAll()
        serviceEspecie.clearAll()
        serviceVector.clearAll()
        serviceUbi.clearAll()
    }

}