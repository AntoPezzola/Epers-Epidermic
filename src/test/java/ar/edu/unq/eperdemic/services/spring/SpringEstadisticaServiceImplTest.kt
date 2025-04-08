package ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringEspecieServiceImpl
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringUbicacionServiceImpl
import ar.edu.unq.eperdemic.modelo.*

import ar.edu.unq.eperdemic.modelo.exceptions.ErrorEspecieNoExiste
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringEstadisticaServiceImplTest {
    @Autowired
    lateinit var  serviceUbi: UbicacionService
    @Autowired
    lateinit var servicePato: PatogenoService
    @Autowired
    lateinit var servicesEstadistica:EstadisticaService
    @Autowired
    lateinit var vectorService: VectorService
    @Autowired
    lateinit var serviceEspecie: EspecieService

    lateinit var vectorHumano : Vector
    lateinit var vectorHumano2 : Vector

    lateinit var vectorInsecto : Vector

    lateinit var vectorAnimal: Vector

    lateinit var patogenoCreado :Patogeno
    lateinit var bacteria : Patogeno
    lateinit var patogeno: Patogeno


    lateinit var hungria : Ubicacion

    lateinit var ubicacion1: Ubicacion
    lateinit var  especie2: Especie
    lateinit var especie: Especie


    @BeforeEach
    fun prepare() {

        ubicacion1 = Ubicacion("Hungria")
        hungria = serviceUbi.crearUbicacion(Ubicacion("Hungria"))
        bacteria = Patogeno("Unpatogeno",70, 70, 70, 70, 70)

        patogeno = Patogeno("TPato", 70, 70, 70, 70, 70)
        patogenoCreado = servicePato.crearPatogeno(patogeno)
        especie2 = Especie(patogeno,"Especiecita",hungria)
        especie  = Especie(patogeno, "Especiesota", hungria)


        vectorHumano = Vector(TipoVector.HUMANO, hungria)
        vectorInsecto = Vector(TipoVector.INSECTO, hungria)
        vectorHumano2 = Vector(TipoVector.HUMANO, hungria)
        vectorAnimal = Vector(TipoVector.ANIMAL, hungria)

    }

    @Test

    fun seEncuentraLaEspecieLider(){
        var chile = serviceUbi.crearUbicacion(Ubicacion("Chile"))
        var bacteria = Patogeno("Bacteria",70, 70, 70, 70, 70)
        servicePato.crearPatogeno(bacteria)
        var bianca = Vector(TipoVector.HUMANO, chile)
        bianca = vectorService.crearVector(bianca)
        val especieLider = serviceEspecie.crearEspecie(especie)

        vectorService.infectar(bianca.id!!, especieLider.id!!)
       // var especieLider = servicePato.agregarEspecie(bacteria.id!!, "Bacterius", chile.id!!)
        // Esto se tiene que ver con lo de elian
        Assertions.assertEquals(servicesEstadistica.especieLider().id!!, especieLider.id!!)
    }

    @Test
    fun noSeEncuentraLaEspecieLider(){
        Assertions.assertEquals("La especie no existe",
            (assertThrows<ErrorEspecieNoExiste>{ servicesEstadistica.especieLider()  } ).message)
    }


    @Test
    fun seRetornaUnaListaVaciaSiNoHayEspeciesInfectadas(){

        val listaEspeciesLideres = servicesEstadistica.lideres()

        Assertions.assertEquals(0, listaEspeciesLideres.size)
    }

    @Test
    fun seRetornaEnOrdenDescendienteLosLideresHumanosYAnimales(){
        val chile = serviceUbi.crearUbicacion(Ubicacion("Chile"))
        val vectorCreado =  vectorService.crearVector(vectorHumano)
        val vectorCreado2 =  vectorService.crearVector(vectorAnimal)
        val vectorCreado3 = vectorService.crearVector(Vector(TipoVector.HUMANO, chile))

        val especieCreada = serviceEspecie.crearEspecie(especie2)
        val especieCreada1 = serviceEspecie.crearEspecie(especie)

        vectorService.infectar(vectorCreado.id!!, especieCreada.id!!)
        vectorService.infectar(vectorCreado2.id!!, especieCreada1.id!!)
        vectorService.infectar(vectorCreado3.id!!, especieCreada1.id!!)

        val especiesLideres = servicesEstadistica.lideres()

        Assertions.assertEquals(2, especiesLideres.size)
        Assertions.assertEquals(especieCreada.id, especiesLideres.get(0).id)
    }

    @Test
    fun elReporteDeContagioRetornaUnErrorSiNoExistELaUbicacionPersistida(){
        var vectorCreado =  vectorService.crearVector(vectorHumano)
        var vectorCreado1 = vectorService.crearVector(vectorInsecto)
        var vectorCreado2 = vectorService.crearVector(vectorHumano2)

        var especieCreada = serviceEspecie.crearEspecie(especie2)

        vectorService.infectar(vectorCreado.id!!, especieCreada.id!!)

        var vectorActualizado = vectorService.recuperarVector(vectorCreado.id!!)

        val cantidadDeVectores           = servicesEstadistica.reporteDeContagios("Hungria").cantVectoresPresentes
        val cantidadDeVectoresInfectados = servicesEstadistica.reporteDeContagios("Hungria").cantVectoresInfectados
        val nombreEspecieConMasContagios = servicesEstadistica.reporteDeContagios("Hungria").nombreEspQueInfectaMasVectores

        Assertions.assertEquals(3, cantidadDeVectores)
        Assertions.assertEquals(cantidadDeVectoresInfectados, 1)
        Assertions.assertEquals(nombreEspecieConMasContagios, especie2.nombre)

    }

    @AfterEach
    fun cleanup() {
        servicePato.clearAll()
        serviceEspecie.clearAll()
        vectorService.clearAll()
        serviceUbi.clearAll()
    }
}