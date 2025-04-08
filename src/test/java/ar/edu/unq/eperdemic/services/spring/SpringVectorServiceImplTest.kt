package ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringEspecieServiceImpl
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringUbicacionServiceImpl
import org.junit.jupiter.api.Test
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.services.interfaces.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringVectorServiceImplTest {
    @Autowired
    lateinit var service: VectorService
    @Autowired
    lateinit var ubicacionService: UbicacionService
    @Autowired
    lateinit var especieService: EspecieService
    @Autowired
    lateinit var mutacionService: MutacionService


    lateinit var hungria: Ubicacion
    lateinit var argentina: Ubicacion

    lateinit var especie : Especie
    lateinit var especie2 : Especie


    lateinit var patogeno: Patogeno
    lateinit var patogenoCreado: Patogeno

    @Autowired
    lateinit var patogenoService: PatogenoService

    lateinit var vectorHumano: Vector
    lateinit var vectorInsecto: Vector

    lateinit var ubicacion1: Ubicacion
    lateinit var ubicacion2: Ubicacion


    @BeforeEach
    fun prepare() {



        ubicacion1 = Ubicacion("Hungria")
        ubicacion2 = Ubicacion("Argentina")
        hungria = ubicacionService.crearUbicacion(ubicacion1)
        argentina = ubicacionService.crearUbicacion(ubicacion2)

        patogeno = Patogeno("TPato", 70, 70, 70, 70, 70)
        patogenoCreado = patogenoService.crearPatogeno(patogeno)

        especie2 = Especie(patogeno,"Especiecita",hungria)
        especie = Especie(patogeno,"UnNombreDeEspecie", hungria)


        vectorHumano = Vector(TipoVector.HUMANO, hungria)
        vectorInsecto = Vector(TipoVector.INSECTO, argentina)
    }

    @Test
    fun seCreaUnNuevoVector() {
        var vectorCreado = service.crearVector(vectorHumano)

        Assertions.assertNotNull(vectorCreado.id)
    }

    @Test
    fun seActualizaUnVector() {
        val vectorCreado = service.crearVector(vectorHumano)
        var vectorRecuperado = service.recuperarVector(vectorCreado.id!!)

        Assertions.assertEquals(vectorRecuperado.tipoVector, TipoVector.HUMANO)

        vectorRecuperado.tipoVector = TipoVector.ANIMAL
        service.actualizarVector(vectorRecuperado)
        vectorRecuperado = service.recuperarVector(vectorCreado.id!!)

        Assertions.assertEquals(vectorRecuperado.tipoVector, TipoVector.ANIMAL)

    }

    @Test
    fun seRecuperaUnVectorCreado() {
        var vectorCreado = service.crearVector(vectorHumano)
        var vectorRecuperado = service.recuperarVector(vectorCreado.id!!)

        Assertions.assertEquals(vectorCreado.id, vectorRecuperado.id)
        Assertions.assertEquals(vectorCreado.tipoVector, vectorRecuperado.tipoVector)
    }

    @Test
    fun traeUnaListaVaciaSiNoHayVectoresQueTraer() {
        Assertions.assertTrue(service.recuperarTodosLosVectores().isEmpty())
    }

    @Test
    fun vectorServiceTraeTodosLosVectores() {
        service.crearVector(vectorHumano)
        service.crearVector(vectorInsecto)

        var vectoresRecuperados = service.recuperarTodosLosVectores()

        Assertions.assertEquals(vectoresRecuperados.size, 2)
    }

    @Test
    fun unVectorEsInfectadoPorLaEspecie() {
        var vectorCreado =  service.crearVector(vectorHumano)
        var especieCreada = especieService.crearEspecie(especie)
        service.infectar(vectorCreado.id!!, especieCreada.id!!)
        var vectorActualizado = service.recuperarVector(vectorCreado.id!!)
        var especiesVectorRecuperado = vectorActualizado.infecciones

        Assertions.assertTrue(especiesVectorRecuperado.any { e -> (e.id == especieCreada.id && e.nombre == especieCreada.nombre) })

    }

    @Test
    fun unVectorPuedeSerInfectadoPorMasDeUnaEspecie() {
        var vectorCreado =  service.crearVector(vectorHumano)
        var especieCreada = especieService.crearEspecie(especie)
        var especieCreada2 = especieService.crearEspecie(especie2)

        service.infectar(vectorCreado.id!!, especieCreada.id!!)
        service.infectar(vectorCreado.id!!, especieCreada2.id!!)

        var enfermedadesVector = service.enfermedades(vectorCreado.id!!)

        var vectorRecuperado = service.recuperarVector(vectorCreado.id!!)

        Assertions.assertEquals(enfermedadesVector.size, 2)
        Assertions.assertFalse(vectorRecuperado.estaSano())
    }

    @Test
    fun siUnVectorNoTieneInfeccionesEstaSano() {
        var vectorCreado =  service.crearVector(vectorHumano)

        Assertions.assertTrue(vectorCreado.estaSano())
    }

    @Test
    fun siUnVectorEstaMutadoConBioAltModificaSuLogicaDeContagio(){
        var vectorCreadoCucaracha =  service.crearVector(Vector(TipoVector.INSECTO, argentina))
        var vectorCreadoMantis =  service.crearVector(vectorInsecto)

        var especie = especieService.crearEspecie(especie2)
        var mutacion = MutacionV(TipoVector.INSECTO)


        val mutacionCreada = mutacionService.agregarMutacion(especie.id!!, mutacion)
        mutacionCreada.mutarAVector(vectorCreadoCucaracha)

        vectorCreadoCucaracha.contagiar(vectorCreadoMantis, especie)

        Assertions.assertFalse(vectorCreadoMantis.estaSano())
    }

    @Test
    fun siUnVectorEstaMutadoConBioAltPeroInfectaConOtraEspecieMantieneSuLogica(){
        // no cumple
        var vectorCreadoCucaracha =  service.crearVector(Vector(TipoVector.INSECTO, argentina))
        var vectorCreadoMantis =  service.crearVector(vectorInsecto)

        var especie1 = especieService.crearEspecie(especie2)
        var especie2 = especieService.crearEspecie(especie)

        var mutacion = MutacionV(TipoVector.INSECTO)
        var mutaCreada = mutacionService.agregarMutacion(especie1.id!!, mutacion)

        mutaCreada.mutarAVector(vectorCreadoCucaracha)

        vectorCreadoCucaracha.contagiar(vectorCreadoMantis, especie2)

        Assertions.assertTrue(vectorCreadoMantis.estaSano())
    }

    @Test
    fun siUnVectorEstaMutadoConBioAltConEInfectaAOtroTipoVectorEspecieMantieneSuLogica(){
        var vectorCreadoCucaracha =  service.crearVector(Vector(TipoVector.INSECTO, argentina))
        var vectorCreadoMantis =  service.crearVector(vectorInsecto)

        var especie1 = especieService.crearEspecie(especie2)
        var especie2 = especieService.crearEspecie(especie)
        /* se muta al vector con una mutacion que no corresponde al tipoVector que
         se intentara contagiar*/
        var mutacion = MutacionV(TipoVector.ANIMAL)

        var mutacionCreada = mutacionService.agregarMutacion(especie1.id!!, mutacion)

        mutacionCreada.mutarAVector(vectorCreadoCucaracha)

        vectorCreadoCucaracha.contagiar(vectorCreadoMantis, especie2)

        Assertions.assertTrue(vectorCreadoMantis.estaSano())
    }

    @Test
    fun siUnVectorMutaConSupresionEliminaSusInfeccionesDebiles(){
        var vectorCreadoCucaracha =  service.crearVector(Vector(TipoVector.INSECTO, argentina))

        var patogenoDebil = Patogeno("Parasito", 70, 60, 70, 30, 30)

        val patoRecu = patogenoService.crearPatogeno(patogenoDebil)

        var especie1 = Especie(patoRecu, "MechaPlaga" ,argentina)


        val especieCreada = especieService.crearEspecie(especie1)

        val especieCreada1 = especieService.crearEspecie(especie2)

        var mutacion = MutacionV(70)

        var mutacionCreada = mutacionService.agregarMutacion(especieCreada.id!!, mutacion)

        vectorCreadoCucaracha.infectar(especieCreada)
        vectorCreadoCucaracha.infectar(especieCreada1)

        mutacionCreada.mutarAVector(vectorCreadoCucaracha)

        Assertions.assertEquals(1, vectorCreadoCucaracha.infecciones.size)
    }

    @Test
    fun siUnVectorMutaConSupresionNoEliminaLaInfeccionQueLoHizoMutar(){
        var vectorCreadoCucaracha =  service.crearVector(Vector(TipoVector.INSECTO, argentina))

        var patogenoDebil = Patogeno("Parasito", 70, 60, 70, 30, 30)

        var patoRecuperado = patogenoService.crearPatogeno(patogenoDebil)

        var especie1 = Especie(patoRecuperado, "MechaPlaga" ,argentina)

        var especieCreada = especieService.crearEspecie(especie1)


        var mutacion = MutacionV(70)

        var mutacionCreada = mutacionService.agregarMutacion(especie1.id!!, mutacion)


        vectorCreadoCucaracha.infectar(especieCreada)

        mutacionCreada.mutarAVector(vectorCreadoCucaracha)



        Assertions.assertEquals(1, vectorCreadoCucaracha.infecciones.size)
    }

    @Test
    fun siUnVectorMutaConSupresionNoEliminaEspeciesMasFuertesQueSuMutacion(){
        var vectorCreadoCucaracha =  service.crearVector(Vector(TipoVector.INSECTO, argentina))

        var patogenoFuerte = Patogeno("Parasito", 70, 60, 70, 80, 30)
        var patoRecu = patogenoService.crearPatogeno(patogenoFuerte)

        var especie1 = Especie(patoRecu, "MechaPlaga" ,argentina)

        var especieCreada = especieService.crearEspecie(especie1)

        var especieCreada1 = especieService.crearEspecie(especie2)

        var mutacion = MutacionV(70)

        var mutacionCreada = mutacionService.agregarMutacion(especieCreada1.id!!, mutacion)

        vectorCreadoCucaracha.infectar(especieCreada1)
        vectorCreadoCucaracha.infectar(especieCreada)

        mutacionCreada.mutarAVector(vectorCreadoCucaracha)

        Assertions.assertEquals(2, vectorCreadoCucaracha.infecciones.size)
    }

    @Test
    fun siUnVectorMutaConSupresionNoSePuedeContagiarDeEspeciesDebiles(){
        var vectorCreadoCucaracha =  service.crearVector(Vector(TipoVector.INSECTO, argentina))

        var vectorHumano = service.crearVector(vectorHumano)

        var patogenoDebil = Patogeno("Parasito", 70, 60, 70, 20, 30)

        var especie1 = Especie(patogenoDebil, "MechaPlaga" ,argentina)

        var especie2 = especieService.crearEspecie(especie)

        var mutacion = MutacionV( 70)

        mutacion.mutarAVector(vectorCreadoCucaracha)

        vectorHumano.contagiar(vectorCreadoCucaracha, especie1)

        Assertions.assertEquals(0, vectorCreadoCucaracha.infecciones.size)
    }

    @Test
    fun siUnVectorMutaConSupresionSePuedeContagiarDeEspeciesFuertes(){
        var vectorCreadoCucaracha =  service.crearVector(Vector(TipoVector.INSECTO, argentina))

        var vectorHumano = service.crearVector(vectorHumano)

        var patogenoFuerte = Patogeno("Parasito", 70, 60, 70, 80, 30)

        var especie1 = Especie(patogenoFuerte, "MechaPlaga" ,argentina)

        var especie2 = especieService.crearEspecie(especie)

        var mutacion = MutacionV(70)

        mutacion.mutarAVector(vectorCreadoCucaracha)

        vectorHumano.contagiar(vectorCreadoCucaracha, especie1)

        Assertions.assertEquals(1, vectorCreadoCucaracha.infecciones.size)
    }


    @AfterEach
    fun cleanup() {
        patogenoService.clearAll()
        especieService.clearAll()
        service.clearAll()
        ubicacionService.clearAll()
    }
}


