package ar.edu.unq.eperdemic.services.spring
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringEspecieServiceImpl
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringUbicacionServiceImpl
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorDatosIngresadosIncorrectos
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorEspecieNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorUbicacionNoExiste
import ar.edu.unq.eperdemic.services.interfaces.EspecieService
import ar.edu.unq.eperdemic.services.interfaces.MutacionService
import ar.edu.unq.eperdemic.services.interfaces.PatogenoService
import ar.edu.unq.eperdemic.services.interfaces.UbicacionService
import org.hibernate.Hibernate
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringMutacionServiceTest {

    @Autowired
    lateinit var serviceMutacion: MutacionService
    @Autowired
    lateinit var serviceEspecie : EspecieService
    @Autowired
    lateinit var servicePatogeno : PatogenoService
    @Autowired
    lateinit var serviceUbicacion: UbicacionService

    lateinit var especieCreada : Especie
    lateinit var patoCreado: Patogeno
    lateinit var ubiCreada:Ubicacion
    @BeforeEach
    fun prepare() {
        var patoCreado = servicePatogeno.crearPatogeno(Patogeno("Virus", 70, 70, 70, 70, 70))
        var ubiCreada =  serviceUbicacion.crearUbicacion(Ubicacion("hungria"))
        var especie = Especie(patoCreado,"UnNombreDeEspecie", ubiCreada)


        especieCreada = serviceEspecie.crearEspecie(especie)

    }

    @Test
    fun seCreaUnaMutacionDeTipoSupresionBioMecanicaConMasDe100Potencia(){
        var mutacion = MutacionV(5000000)
        Assertions.assertEquals("La potencia de mutación debe ser menor o igual a 100",
            (assertThrows<ErrorDatosIngresadosIncorrectos>{  serviceMutacion.crearMutacion(mutacion)  } ).message)

    }
    @Test
    fun seCreaUnaMutacionDeTipoSupresionBioMecanicaConMenosDe1Potencia(){
        var mutacion = MutacionV( 0)
        Assertions.assertEquals("La potencia de mutación debe ser mayor o igual a 1",
            (assertThrows<ErrorDatosIngresadosIncorrectos>{  serviceMutacion.crearMutacion(mutacion)  } ).message)
    }

    @Test
    fun seCreaUnaMutacionDeTipoSupresionBioMecanicaConPotenciaCorrecta(){
        var mutacion = MutacionV( 99)
        var mutacionCreada = serviceMutacion.crearMutacion(mutacion)

        Assertions.assertNotNull(mutacionCreada.id)
        Assertions.assertEquals(mutacionCreada.tipoMutacion, TipoMutacion.SUPBIOMECANICA )
        Assertions.assertEquals(mutacionCreada.potenciaDeMutacion, 99)
        Assertions.assertNull(mutacionCreada.tipoDeVector)

    }

    @Test
    fun seCreaUnaMutacionDeTipoBioAlteracionGeneticaConUnTipoDeVector(){
        var mutacion = MutacionV(TipoVector.ANIMAL)

        var mutacionCreada = serviceMutacion.crearMutacion(mutacion)

        Assertions.assertNotNull(mutacionCreada.id)
        Assertions.assertEquals(mutacionCreada.tipoMutacion, TipoMutacion.BIOALTGENETICA )
        Assertions.assertEquals(mutacionCreada.tipoDeVector, TipoVector.ANIMAL)
        Assertions.assertNull(mutacionCreada.potenciaDeMutacion)
    }

    @Test
    fun seAgregarMutacionAUnaEspecieQueNoExisteLanzaUnError(){
        var mutacion = MutacionV(TipoVector.ANIMAL)
        Assertions.assertEquals("La especie no existe",
            (assertThrows<ErrorEspecieNoExiste>{
                serviceMutacion.agregarMutacion(9999, mutacion)
            } ).message)

    }

    @Test
    fun seAgregaUnaPosibleMutacionAUnaEspecieQueExiste(){
        var mutacion = MutacionV(TipoVector.ANIMAL)

        var mutacionCreada =serviceMutacion.agregarMutacion(especieCreada.id!!, mutacion)

        Assertions.assertNotNull(mutacionCreada.id)
        Assertions.assertEquals(mutacionCreada.tipoDeVector, TipoVector.ANIMAL)
        Assertions.assertEquals(mutacionCreada.especie.id , especieCreada.id)
    }


    @AfterEach
    fun cleanup() {
        servicePatogeno.clearAll()
        serviceMutacion.clearAll()
        serviceEspecie.clearAll()
        serviceUbicacion.clearAll()
    }
}