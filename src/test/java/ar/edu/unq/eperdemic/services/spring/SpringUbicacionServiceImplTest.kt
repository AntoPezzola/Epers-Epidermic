package ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringEspecieServiceImpl
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring.SpringUbicacionServiceImpl
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorLaUbicacionExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorUbicacionNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorVectorNoExiste
import ar.edu.unq.eperdemic.services.interfaces.MutacionService
import ar.edu.unq.eperdemic.modelo.exceptions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringUbicacionServiceImplTest {

    @Autowired
    lateinit var ubicacionService: SpringUbicacionServiceImpl

    @Autowired
    lateinit var vectorService: SpringVectorServiceImpl

    @Autowired
    lateinit var especieService: SpringEspecieServiceImpl

    @Autowired
    lateinit var patogenoService: PatogenoService

    @Autowired
    lateinit var mutacionService: MutacionService

    lateinit var vectorParaTest: Vector
    lateinit var hungria: Ubicacion
    lateinit var china: Ubicacion
    lateinit var patogenoParaTest: Patogeno
    lateinit var especieTest: Especie


    @BeforeEach
    fun setUp() {
        hungria = Ubicacion("Hungria")
        china = Ubicacion("China")
        vectorParaTest = Vector(TipoVector.HUMANO, hungria)

    }

    @Test
    fun crearUbicacion() {
        val ubicacionCreda = ubicacionService.crearUbicacion(Ubicacion("Japon"))
        assertNotNull(ubicacionCreda.id)
        assertEquals(ubicacionCreda.nombre, "Japon")
    }

    @Test
    fun crearDosVecesLaMismaUbicacionDaError() {
        ubicacionService.crearUbicacion(hungria)
        val errorEsperado = ErrorLaUbicacionExiste::class.java
        val mensajeDeError = "La ubicacion ya existe en la base de datos"
        val errorGenereado = assertThrows(errorEsperado) { ubicacionService.crearUbicacion(hungria) }
        assertEquals(mensajeDeError, errorGenereado.message)
    }

    // ACTUALIZAR UBICACION
    @Test
    fun unaUbicacionSeActualiza() {
        ubicacionService.crearUbicacion(hungria)
        val nuevoNombreUbicacion = "Panama"

        hungria.nombre = nuevoNombreUbicacion
        ubicacionService.actualizarUbicacion(hungria)
        val ubicacionActualizada = ubicacionService.recuperarUbicacion(hungria.id!!)

        assertEquals(nuevoNombreUbicacion, ubicacionActualizada.nombre)
    }


    @Test
    fun seActualizaUnaUbicacionNoCreada() {
        val ubicacion2: Ubicacion = Ubicacion("Rusia")

        val errorEsperado = ErrorUbicacionNoExiste::class.java
        val mensajeDeErrorEsperado = "La Ubicacion no existe en la base de datos"

        val errorGenerado = assertThrows(errorEsperado) { ubicacionService.actualizarUbicacion(ubicacion2) }

        assertEquals(mensajeDeErrorEsperado, errorGenerado.message)
    }

    // RECUPERAR UBICACION

    @Test
    fun seRecuperaUnaUbicacion() {
        val ubicacionCreada = ubicacionService.crearUbicacion(hungria)
        val ubicacionRecuperada = ubicacionService.recuperarUbicacion(ubicacionCreada.id!!.toLong())
        assertEquals(ubicacionCreada.id, ubicacionRecuperada.id)
        assertEquals(ubicacionCreada.nombre, ubicacionRecuperada.nombre)
    }

    @Test
    fun seRecuperaUnaUbicacionConUnIdErroneo() {
        val errorEsperado = ErrorUbicacionNoExiste()::class.java
        val errorGenerado = assertThrows(errorEsperado) { ubicacionService.recuperarUbicacion(42) }
        val mensajeErrorEsperado = "La Ubicacion no existe en la base de datos"
        assertEquals(mensajeErrorEsperado, errorGenerado.message)

    }

    // RECUPERAR TODAS LAS UBICACIONES

    @Test
    fun seRecuperanTodasLasUbicacionesPersistidas() {
        ubicacionService.crearUbicacion(hungria)
        val ubicaciones = ubicacionService.recuperarTodasLasUbicaciones()

        assertEquals(ubicaciones.size, 1)
    }

    //MOVER UN VECTOR A OTRA UBICACION




    @Test
    fun seMueveUnVectorInfectadoDeUnaUbicacionAOtra() {
        val recu1 =  ubicacionService.crearUbicacion( Ubicacion("Costa Rica"))
        val recu2= ubicacionService.crearUbicacion(Ubicacion("Panama"))

        ubicacionService.conectar(recu1.nombre, recu2.nombre, TipoCamino.CAMINOMARITIMO)

        var vectorAMover = Vector(TipoVector.HUMANO, recu1)
        vectorService.crearVector(vectorAMover)

        var vectorSano = Vector(TipoVector.HUMANO, recu2)
        vectorService.crearVector(vectorSano)

        val pato =  Patogeno("Virus", 60, 15, 12, 0, 0)
        val recuperado =   patogenoService.crearPatogeno(pato)
        val especieInfeccion = Especie(recuperado, "Ecoli", recu1)
        especieService.crearEspecie(especieInfeccion)

        ubicacionService.mover(vectorAMover.id!!, recu1.id!!)

        vectorAMover = vectorService.recuperarVector(vectorAMover.id!!)
        vectorSano = vectorService.recuperarVector(vectorSano.id!!)

        assertEquals(vectorAMover.ubicacion.id, recu1.id)
        assertTrue(vectorSano.estaSano())
    }



    @Test
    fun unaUbicacionServiceMueveUnVectorContagiadoAUnaUbicacionSinVectores() {
        val ubicacionBrasil = ubicacionService.crearUbicacion(Ubicacion("Brasil"))
        val ubicacionChile = ubicacionService.crearUbicacion(Ubicacion("Chile"))

        ubicacionService.conectar(ubicacionBrasil.nombre, ubicacionBrasil.nombre, TipoCamino.CAMINOMARITIMO)


        val vectorAnimal = vectorService.crearVector(Vector(TipoVector.ANIMAL, ubicacionBrasil))

        val vectorContagiado = vectorAnimal.id!!
        val ubicacionAMover = ubicacionChile.id!!
        val ubicacionAnterior = ubicacionBrasil.id!!

        ubicacionService.mover(vectorContagiado, ubicacionAMover)


        val cantidadDeVectoresEnBrasil = ubicacionService.obtenerVectoresDeUbicacion(ubicacionAnterior).size
        val cantidadDeVectoresEnChile = ubicacionService.obtenerVectoresDeUbicacion(ubicacionAMover).size

        assertEquals(0, cantidadDeVectoresEnBrasil)
        assertEquals(1, cantidadDeVectoresEnChile)

    }

    @Test
    fun seMueveUnVectorSanoDeUnaUbicacionAOtra() {
        val ubicacion2 = Ubicacion("Argentina")

        ubicacionService.crearUbicacion(hungria)
        ubicacionService.crearUbicacion(ubicacion2)


        ubicacionService.conectar(hungria.nombre, ubicacion2.nombre, TipoCamino.CAMINOMARITIMO)



        var vectorAMover = Vector(TipoVector.HUMANO, hungria)
        vectorService.crearVector(vectorAMover)

        var vectorSano = Vector(TipoVector.HUMANO, ubicacion2)
        vectorService.crearVector(vectorSano)

        val patogenoInfeccion = Patogeno("Virus", 60, 15, 12, 0, 0)
        patogenoService.crearPatogeno(patogenoInfeccion)

        val especieInfeccion = Especie(patogenoInfeccion, "Ecoli", hungria)
        especieService.crearEspecie(especieInfeccion)

        ubicacionService.mover(vectorAMover.id!!.toLong(), ubicacion2.id!!.toLong())

        vectorAMover = vectorService.recuperarVector(vectorAMover.id!!.toLong())
        vectorSano = vectorService.recuperarVector(vectorSano.id!!.toLong())

        assertEquals(vectorAMover.ubicacion.id, ubicacion2.id)
        assertTrue(vectorSano.estaSano())

    }

    @Test
    fun seIntentaMoverUnVectorAUnaUbicacionNoCreada() {

        ubicacionService.crearUbicacion(hungria)

        val vectorAMover = Vector(TipoVector.HUMANO, hungria)
        vectorService.crearVector(vectorAMover)

        val errorEsperado = ErrorUbicacionNoExiste::class.java

        assertThrows(errorEsperado) { ubicacionService.mover(vectorAMover.id!!.toLong(), 42) }
    }

    @Test
    fun seIntentaMoverUnVectorQueNoExisteAUnaUbicacion() {

        var recu = ubicacionService.crearUbicacion(hungria)
        val errorEsperado = ErrorVectorNoExiste::class.java

        assertThrows(errorEsperado) { ubicacionService.mover(42, recu.id!!) }
    }

    @Test
    fun unaUbicacionServiceMueveUnVectorSanoAUnaUbicacionSinVectores(){
        val ubicacionAnterior = ubicacionService.crearUbicacion(Ubicacion("Mexico"))
        val ubicacionAMover   = ubicacionService.crearUbicacion(Ubicacion("Uruguay"))

        ubicacionService.conectar(ubicacionAnterior.nombre, ubicacionAMover.nombre, TipoCamino.CAMINOMARITIMO)

        val vectorSano        = vectorService.crearVector(Vector(TipoVector.HUMANO, ubicacionAnterior))

        ubicacionService.mover(vectorSano.id!!, ubicacionAMover.id!!)

        val cantidadDeVectoresEnMexico  = ubicacionService.cantidadDeVectores(ubicacionAnterior.id!!)
        val cantidadDeVectoresEnUruguay = ubicacionService.cantidadDeVectores(ubicacionAMover.id!!)

        assertEquals(0, cantidadDeVectoresEnMexico)
        assertEquals(1, cantidadDeVectoresEnUruguay)
    }

    @Test
    fun unaUbicacionServiceMueveUnVectorSanoAUnaUbicacionConVectores(){
        val ubicacionAnterior = ubicacionService.crearUbicacion(Ubicacion("Mexico"))
        val ubicacionAMover   = ubicacionService.crearUbicacion(Ubicacion("Uruguay"))

        ubicacionService.conectar(ubicacionAnterior.nombre, ubicacionAMover.nombre, TipoCamino.CAMINOMARITIMO)

        val vectorAContagiar  = vectorService.crearVector(Vector(TipoVector.HUMANO, ubicacionAMover))
        val vectorSano        = vectorService.crearVector(Vector(TipoVector.HUMANO, ubicacionAnterior))


        ubicacionService.mover(vectorSano.id!!, ubicacionAMover.id!!)


        val cantidadDeVectoresEnMexico  = ubicacionService.cantidadDeVectores(ubicacionAnterior.id!!)
        val cantidadDeVectoresEnUruguay = ubicacionService.cantidadDeVectores(ubicacionAMover.id!!)

        assertEquals(0, cantidadDeVectoresEnMexico)
        assertEquals(2, cantidadDeVectoresEnUruguay)
        assertTrue(vectorAContagiar.estaSano())
    }

    @Test
    fun unaUbicacionServiceMueveUnVectorContagiadoAUnaUbicacionConVectores(){
        val ubicacionAnterior = ubicacionService.crearUbicacion(Ubicacion("Mexico"))
        val ubicacionAMover   = ubicacionService.crearUbicacion(Ubicacion("Uruguay") )

                ubicacionService.conectar(ubicacionAnterior.nombre, ubicacionAMover.nombre, TipoCamino.CAMINOMARITIMO)


        val vectorContagiado  = vectorService.crearVector(Vector(TipoVector.HUMANO, ubicacionAnterior))
        val patogenoHongo     = patogenoService.crearPatogeno(Patogeno("Hongo", 70, 70, 70, 70, 70))
        patogenoService.agregarEspecie(patogenoHongo.id!!, "Ameba", ubicacionAnterior.id!!)
        val vectorAContagiar  = vectorService.crearVector(Vector(TipoVector.HUMANO, ubicacionAMover))

        ubicacionService.mover(vectorContagiado.id!!, ubicacionAMover.id!!)


        val cantidadDeVectoresEnMexico  = ubicacionService.cantidadDeVectores(ubicacionAnterior.id!!)
        val cantidadDeVectoresEnUruguay = ubicacionService.cantidadDeVectores(ubicacionAMover.id!!)

        val vectoresDeUbicacion  = ubicacionService.obtenerVectoresDeUbicacion(ubicacionAMover.id!!)
        val unVectorAContagiar   = vectoresDeUbicacion.find {it.id == vectorAContagiar.id}

        assertEquals(0, cantidadDeVectoresEnMexico)
        assertEquals(2, cantidadDeVectoresEnUruguay)
        assertFalse(unVectorAContagiar!!.estaSano())
    }

    // EXPANDIR ENFERMEDADES EN UBICACION
    @Test
    fun seExpandeEnfermedadesDeUnVectorEnLaUbicacion() {
        val patogenoBacteria = Patogeno("hongo", 70, 70, 70, 70, 70)
        val patoRecu = patogenoService.crearPatogeno(patogenoBacteria)
        var ubicacionCreada = ubicacionService.crearUbicacion(hungria)

        val vectorHumano1 = vectorService.crearVector(Vector(TipoVector.ANIMAL, ubicacionCreada))

        var esp = patogenoService.agregarEspecie(patoRecu.id!!, "unNoombre", ubicacionCreada.id!!)

        val vectorHumano2 = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionCreada))
        val vectorHumano3 = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionCreada))
        assertTrue(vectorHumano2.estaSano())
        assertTrue(vectorHumano3.estaSano())

        ubicacionService.expandir(ubicacionCreada.id!!)


        val infectadoLuegoExpandir1 = vectorService.recuperarVector(vectorHumano2.id!!)
        val infectadoLuegoExpandir2 = vectorService.recuperarVector(vectorHumano3.id!!)


        assertFalse(infectadoLuegoExpandir1.estaSano())
        assertFalse(infectadoLuegoExpandir2.estaSano())
    }

    @Test
    fun seIntentaExpandirAUnaUbicacionQueNoExiste() {

        val errorEsperado = ErrorUbicacionNoExiste::class.java

        assertThrows(errorEsperado) { ubicacionService.expandir(5) }
    }

    @Test
    fun siEnLaUbicacionNoHayVectoresInfectadosNoHaceNada() {

        var ubicacionCreada = ubicacionService.crearUbicacion(china)
        val vectorSano = vectorService.crearVector(Vector(TipoVector.HUMANO, ubicacionCreada))
        val vectorMuySano = vectorService.crearVector(Vector(TipoVector.HUMANO, ubicacionCreada))
        ubicacionService.expandir(ubicacionCreada.id!!)
        val vectorSanoRecuperado = vectorService.recuperarVector(vectorSano.id!!)
        val vectorMuySanoRecuperado = vectorService.recuperarVector(vectorMuySano.id!!)

        assertTrue(vectorSanoRecuperado.estaSano())
        assertTrue(vectorMuySanoRecuperado.estaSano())
    }

    @Test
    fun elVectorInfectadoNoSeVuelveAInfectar() {
        var ubiCreada = ubicacionService.crearUbicacion(hungria)
        var ubiCreada2 = ubicacionService.crearUbicacion(china)

        val vectorInfectado = Vector(TipoVector.HUMANO, ubiCreada)
        vectorService.crearVector(vectorInfectado)

        val vectorSano = Vector(TipoVector.HUMANO, ubiCreada2)
        vectorService.crearVector(vectorSano)

        val patogenoBacteria = Patogeno("Bacteria", 12, 23, 12, 23, 42)
        patogenoService.crearPatogeno(patogenoBacteria)

        val especieInfeccion = Especie(patogenoBacteria, "Ecoli", hungria)
        especieService.crearEspecie(especieInfeccion)

        vectorService.infectar(vectorInfectado.id!!.toLong(), especieInfeccion.id!!.toLong())

        ubicacionService.expandir(hungria.id!!.toLong())

        vectorService.recuperarVector(vectorInfectado.id!!.toLong())

        val enfermedades = vectorService.enfermedades(vectorInfectado.id!!.toLong())

        assertEquals(1, enfermedades.size)
    }


    @Test
    fun elVectorAInfectarSeInfectaConMasDeUnaEspecie() {
        var ubiHungriaCreada = ubicacionService.crearUbicacion(hungria)

        val vectorInfectado = Vector(TipoVector.HUMANO, ubiHungriaCreada)
        var vectorHumanoInfectado = vectorService.crearVector(vectorInfectado)

        val patogenoBacteria = Patogeno("Bacteria", 12, 23, 12, 23, 42)
        var patogenoBacteriaCreado = patogenoService.crearPatogeno(patogenoBacteria)

        val especieInfeccion = Especie(patogenoBacteriaCreado, "Ecoli", ubiHungriaCreada)
        val especieInfeccionRecuperada = especieService.crearEspecie(especieInfeccion)

        val especieInfeccion2 = Especie(patogenoBacteriaCreado, "ditto", ubiHungriaCreada)
        var especieInfeccionRecuperada2 = especieService.crearEspecie(especieInfeccion2)

        vectorService.infectar(vectorHumanoInfectado.id!!, especieInfeccionRecuperada2.id!!)
        vectorService.infectar(vectorHumanoInfectado.id!!, especieInfeccionRecuperada.id!!)

        var vectorRecuperadoInfectado = vectorService.recuperarVector(vectorHumanoInfectado.id!!)

        val vectorContagiado = vectorService.recuperarVector(vectorRecuperadoInfectado.id!!)
        val enfermedades = vectorService.enfermedades(vectorRecuperadoInfectado.id!!)

        assertFalse(vectorContagiado.estaSano())
        assertEquals(2, enfermedades.size)
    }


    ///IMPLEMENTACION NEO4J
    @Test
    fun recuperoUnaUbicacionDeNEO() {

        var ubicacionCreada = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var recuperadaConNeo = ubicacionService.recuperarUbicacionConNEO("Jordania")

        Assertions.assertEquals(recuperadaConNeo.nombre, "Jordania")
        Assertions.assertTrue(recuperadaConNeo.caminos.isEmpty())

    }

    @Test
    fun recuperoUnaUbicacionQueNoExsiteEnNEO() {

        val errorEsperado = ErrorUbicacionNoExiste::class.java
        assertThrows(errorEsperado) { ubicacionService.recuperarUbicacionConNEO("Guadalajara") }
    }


    @Test
    fun concectarDosUbicaciones() {
        var ubicacionCreada = ubicacionService.crearUbicacion(Ubicacion("Hungria"))
        var ubicacionCreadaArg = ubicacionService.crearUbicacion(Ubicacion("Argentina"))
        ubicacionService.conectar(ubicacionCreada.nombre, ubicacionCreadaArg.nombre, TipoCamino.CAMINOMARITIMO)

        var recuperadaConNeo1 = ubicacionService.recuperarUbicacionConNEO(ubicacionCreada.nombre)

        assertEquals(recuperadaConNeo1.caminos.size, 1)
    }


    @Test
    fun seConectanDosUbicaciones() {
        var chile = ubicacionService.crearUbicacion(Ubicacion("Chile"))
        var japon = ubicacionService.crearUbicacion(Ubicacion("Japon"))

        ubicacionService.conectar(chile.nombre, japon.nombre, TipoCamino.CAMINOAEREO)

        var recuperadaChile = ubicacionService.recuperarUbicacionConNEO(chile.nombre)
        Assertions.assertEquals(recuperadaChile.caminos.size, 1)
    }

    @Test
    fun seConectanDosUbicacionesIdaYVuelta() {
        var chile = ubicacionService.crearUbicacion(Ubicacion("Chile"))
        var japon = ubicacionService.crearUbicacion(Ubicacion("Japon"))

        ubicacionService.conectar(chile.nombre, japon.nombre, TipoCamino.CAMINOAEREO)
        ubicacionService.conectar(japon.nombre, chile.nombre, TipoCamino.CAMINOAEREO)


        var recuperadaChile = ubicacionService.recuperarUbicacionConNEO(chile.nombre)
        var recuperadaJapon = ubicacionService.recuperarUbicacionConNEO(japon.nombre)

        Assertions.assertEquals(recuperadaChile.caminos.size, 1)
        Assertions.assertEquals(recuperadaJapon.caminos.size, 1)
    }

    @Test
    fun noSePuedenConectarUbicacionesCuandoNoHayUbicaionesCreadas() {

        val errorEsperado = ErrorUbicacionNoExiste::class.java

        assertThrows(errorEsperado) { ubicacionService.conectar("Chile", "Japon", TipoCamino.CAMINOAEREO) }
    }

    @Test
    fun unaUbicacionPuedeTenerMuchosCaminosConectados() {
        var chile = ubicacionService.crearUbicacion(Ubicacion("Chile"))
        var japon = ubicacionService.crearUbicacion(Ubicacion("Japon"))
        var uruguay = ubicacionService.crearUbicacion(Ubicacion("Uruguay"))
        var china = ubicacionService.crearUbicacion(Ubicacion("China"))

        ubicacionService.conectar(japon.nombre, chile.nombre, TipoCamino.CAMINOAEREO)
        ubicacionService.conectar(uruguay.nombre, chile.nombre, TipoCamino.CAMINOAEREO)
        ubicacionService.conectar(china.nombre, chile.nombre, TipoCamino.CAMINOAEREO)

        val chileRecu = ubicacionService.recuperarUbicacionConNEO(chile.nombre)

        var ubicacionesConectadas = ubicacionService.conectados(chileRecu.nombre)

        Assertions.assertEquals(ubicacionesConectadas.size, 3)
    }

    @Test
    fun seVerificaQueSiNoHayCaminosConPosiblesTiposDeRecorridosSeLanzaUbicacionMuyLejana() {
        var ubicacionOrigen = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var ubicacionDestino = ubicacionService.crearUbicacion(Ubicacion("PacificoSur"))

        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionOrigen))

        val errorEsperado = UbicacacionNoAlcanzable::class.java
        assertThrows(errorEsperado) { ubicacionService.puedeMover("Jordania", "PacificoSur", vectorDeTipoInsecto) }

    }

    @Test
    fun seVerificaQueSiHayCaminosConPosiblesTiposDeRecorridosSePuedeMover() {

        var ubicacionOrigen = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var ubicacionDestino = ubicacionService.crearUbicacion(Ubicacion("PacificoSur"))
        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionOrigen))

        ubicacionService.conectar(ubicacionOrigen.nombre, ubicacionDestino.nombre, TipoCamino.CAMINOTERRESTRE)

        assertTrue(ubicacionService.puedeMover("Jordania", "PacificoSur", vectorDeTipoInsecto))

    }

//Verificaciones nuevas sobre mover


    @Test
    fun seVerificaQueNoEsUnaUbicacionMuyLejanaPorqueSeEncuentraAlgunTipoDeCamino() {
        var ubicacionOrigen = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var ubicacionDestino = ubicacionService.crearUbicacion(Ubicacion("PacificoSur"))

        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionOrigen))

        ubicacionService.conectar( ubicacionDestino.nombre, ubicacionOrigen.nombre,TipoCamino.CAMINOAEREO)


        var tipoDeCaminoPorqueNoEsMuyLejana =  ubicacionService.esUbicacionMuyLejana(ubicacionDestino.nombre, vectorDeTipoInsecto.ubicacion.nombre)
        Assertions.assertEquals(tipoDeCaminoPorqueNoEsMuyLejana, TipoCamino.CAMINOAEREO.toString() )
        Assertions.assertEquals(
            ubicacionService.esUbicacionMuyLejana(
                ubicacionDestino.nombre,
                vectorDeTipoInsecto.ubicacion.nombre
            ), TipoCamino.CAMINOAEREO.toString()
        )
    }

    @Test
    fun seVerificaQueEsUnaUbicacionMuyLejana() {
        var ubicacionOrigen = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionOrigen))
        var ubicacion2 = ubicacionService.crearUbicacion(Ubicacion("Corea"))

        val errorEsperado = UbicacionMuyLejana::class.java


        assertThrows(errorEsperado) {
            ubicacionService.esUbicacionMuyLejana(
                vectorDeTipoInsecto.ubicacion.nombre,
                ubicacion2.nombre
            )
        }
    }


    @Test
    fun seVerificaQueNoSePuedeMoverPorqueEsUnaUbicacionNoAlcanzable() {
        var ubicacionOrigen = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var ubicacionDestino = ubicacionService.crearUbicacion(Ubicacion("PacificoSur"))

        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionOrigen))

        val errorEsperado = UbicacacionNoAlcanzable::class.java
        assertThrows(errorEsperado) { ubicacionService.mover(vectorDeTipoInsecto.id!!, ubicacionDestino.id!!) }

    }

    @Test
    fun seVerificaQueNoSePuedeMoverPorqueEsUbicacionMuyLejana() {
        var ubicacionOrigen = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var ubicacionDestino = ubicacionService.crearUbicacion(Ubicacion("PacificoSur"))

        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionOrigen))
        var ubicacion2 = ubicacionService.crearUbicacion(Ubicacion("Corea"))

        ubicacionService.conectar(ubicacionOrigen.nombre, ubicacionDestino.nombre, TipoCamino.CAMINOMARITIMO)
        ubicacionService.conectar(ubicacionDestino.nombre, ubicacion2.nombre, TipoCamino.CAMINOTERRESTRE)

        assertFalse(ubicacionService.puedeMover("Jordania", "PacificoSur", vectorDeTipoInsecto))

        val errorEsperado = UbicacionMuyLejana::class.java

        assertThrows(errorEsperado) { ubicacionService.mover(vectorDeTipoInsecto.id!!, ubicacion2.id!!) }
    }

    @Test
    fun seVerificaQueSeMueveHaciaOtraUbicacion() {

        var ubicacionOrigen = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var ubicacionDestino = ubicacionService.crearUbicacion(Ubicacion("PacificoSur"))
        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionOrigen))

        ubicacionService.conectar(ubicacionOrigen.nombre, ubicacionDestino.nombre,TipoCamino.CAMINOAEREO)

        var vectorRecu = vectorService.recuperarVector(vectorDeTipoInsecto.id!!)

        assertTrue(ubicacionService.puedeMover("Jordania", "PacificoSur", vectorDeTipoInsecto))
        ubicacionService.mover(vectorDeTipoInsecto.id!!, ubicacionDestino.id!!)


        var recuperado = vectorService.recuperarVector(vectorDeTipoInsecto.id!!)

        assertEquals( recuperado.ubicacion.nombre, ubicacionDestino.nombre )
        
        assertEquals(recuperado.ubicacion.nombre, "PacificoSur")
    }

    // moverPorCaminoMsCorto
    @Test
    fun muevePorCaminoMasCorto() {
        // se arman las ubicaciones
        var ubicacionJordania = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var ubicacionPacificoSur = ubicacionService.crearUbicacion(Ubicacion("PacificoSur"))
        var ubicacionHolanda = ubicacionService.crearUbicacion(Ubicacion("Holanda"))
        var ubicacionCamerun = ubicacionService.crearUbicacion(Ubicacion("Camerun"))
        var ubicacionNigeria = ubicacionService.crearUbicacion(Ubicacion("Nigeria"))

        // se crea la enfermedad
        patogenoParaTest = Patogeno("virus", 80, 80, 80, 70, 70)
        especieTest = Especie(patogenoParaTest, "a", ubicacionJordania)
        patogenoService.crearPatogeno(patogenoParaTest)
        var especie = especieService.crearEspecie(especieTest)

        // se crean los sujetos de prueba y se infecta al que se va a mover
        var vectorDeTipoAnimal = vectorService.crearVector(Vector(TipoVector.ANIMAL, ubicacionJordania))

        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionNigeria))

        vectorService.infectar(vectorDeTipoAnimal.id!!, especie.id!!)

        // se conectan las ubicaciones
        ubicacionService.conectar(ubicacionPacificoSur.nombre, ubicacionJordania.nombre, TipoCamino.CAMINOAEREO)
        ubicacionService.conectar(ubicacionHolanda.nombre, ubicacionPacificoSur.nombre, TipoCamino.CAMINOAEREO)
        ubicacionService.conectar(ubicacionCamerun.nombre, ubicacionHolanda.nombre, TipoCamino.CAMINOAEREO)
        ubicacionService.conectar(ubicacionNigeria.nombre, ubicacionJordania.nombre, TipoCamino.CAMINOTERRESTRE)
        ubicacionService.conectar(ubicacionCamerun.nombre, ubicacionNigeria.nombre, TipoCamino.CAMINOTERRESTRE)


        // se ejecuta el metodo
        ubicacionService.moverPorCaminoMasCorto(vectorDeTipoAnimal.id!!, "Camerun")

        val vectorRecuInsecto = vectorService.recuperarVector(vectorDeTipoInsecto.id!!)

        val vectorRecuAnimal = vectorService.recuperarVector(vectorDeTipoAnimal.id!!)

        Assertions.assertEquals(ubicacionCamerun.nombre, vectorRecuAnimal.ubicacion.nombre)

        Assertions.assertFalse(vectorRecuInsecto.estaSano())
    }

    @Test
    fun muevePorCaminoLargoSiElCortoEsInvalido() {
        // se arman las ubicaciones
        var ubicacionJordania = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var ubicacionPacificoSur = ubicacionService.crearUbicacion(Ubicacion("PacificoSur"))
        var ubicacionHolanda = ubicacionService.crearUbicacion(Ubicacion("Holanda"))
        var ubicacionCamerun = ubicacionService.crearUbicacion(Ubicacion("Camerun"))
        var ubicacionNigeria = ubicacionService.crearUbicacion(Ubicacion("Nigeria"))

        // se crea la enfermedad
        patogenoParaTest = Patogeno("virus", 80, 80, 80, 70, 70)
        especieTest = Especie(patogenoParaTest, "a", ubicacionJordania)
        patogenoService.crearPatogeno(patogenoParaTest)
        var especie = especieService.crearEspecie(especieTest)

        // se crean los sujetos de prueba y se infecta al que se va a mover
        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionJordania))

        var vectorDeTipoHumano = vectorService.crearVector(Vector(TipoVector.HUMANO, ubicacionNigeria))

        vectorService.infectar(vectorDeTipoInsecto.id!!, especie.id!!)

        // se conectan las ubicaciones
        ubicacionService.conectar(ubicacionPacificoSur.nombre, ubicacionJordania.nombre, TipoCamino.CAMINOTERRESTRE)
        ubicacionService.conectar(ubicacionHolanda.nombre, ubicacionPacificoSur.nombre, TipoCamino.CAMINOTERRESTRE)
        ubicacionService.conectar(ubicacionCamerun.nombre, ubicacionHolanda.nombre, TipoCamino.CAMINOTERRESTRE)
        ubicacionService.conectar(ubicacionNigeria.nombre, ubicacionJordania.nombre, TipoCamino.CAMINOMARITIMO)
        ubicacionService.conectar(ubicacionCamerun.nombre, ubicacionNigeria.nombre, TipoCamino.CAMINOMARITIMO)


        // se ejecuta el metodo
        ubicacionService.moverPorCaminoMasCorto(vectorDeTipoInsecto.id!!, "Camerun")

        val vectorRecuInsecto = vectorService.recuperarVector(vectorDeTipoInsecto.id!!)

        val vectorRecuHumano = vectorService.recuperarVector(vectorDeTipoHumano.id!!)

        Assertions.assertEquals(ubicacionCamerun.nombre, vectorRecuInsecto.ubicacion.nombre)

        Assertions.assertTrue(vectorRecuHumano.estaSano())
    }

    @Test
    fun siNoHayCaminosValidosLanzaExepcion(){
        // se arman las ubicaciones
        var ubicacionJordania = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var ubicacionPacificoSur = ubicacionService.crearUbicacion(Ubicacion("PacificoSur"))
        var ubicacionHolanda = ubicacionService.crearUbicacion(Ubicacion("Holanda"))
        var ubicacionCamerun = ubicacionService.crearUbicacion(Ubicacion("Camerun"))
        var ubicacionNigeria = ubicacionService.crearUbicacion(Ubicacion("Nigeria"))

        // se crea la enfermedad
        patogenoParaTest = Patogeno("virus", 80, 80, 80, 70, 70)
        especieTest = Especie(patogenoParaTest, "a", ubicacionJordania)
        patogenoService.crearPatogeno(patogenoParaTest)
        var especie = especieService.crearEspecie(especieTest)

        // se crean los sujetos de prueba y se infecta al que se va a mover
        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionJordania))

        vectorService.infectar(vectorDeTipoInsecto.id!!, especie.id!!)

        // se conectan las ubicaciones
        ubicacionService.conectar(ubicacionPacificoSur.nombre, ubicacionJordania.nombre, TipoCamino.CAMINOMARITIMO)
        ubicacionService.conectar(ubicacionHolanda.nombre, ubicacionPacificoSur.nombre, TipoCamino.CAMINOMARITIMO)
        ubicacionService.conectar(ubicacionCamerun.nombre, ubicacionHolanda.nombre, TipoCamino.CAMINOMARITIMO)
        ubicacionService.conectar(ubicacionNigeria.nombre, ubicacionJordania.nombre, TipoCamino.CAMINOMARITIMO)
        ubicacionService.conectar(ubicacionCamerun.nombre, ubicacionNigeria.nombre, TipoCamino.CAMINOMARITIMO)


        // se ejecuta el metodo
        val errorEsperado = UbicacacionNoAlcanzable()::class.java

        assertThrows(errorEsperado){ubicacionService.moverPorCaminoMasCorto(vectorDeTipoInsecto.id!!, "Camerun")}

    }

    @Test
    fun siLaUbicacionNoExisteLanzaExcepcion(){
        // se arman las ubicaciones
        var ubicacionJordania = ubicacionService.crearUbicacion(Ubicacion("Jordania"))
        var ubicacionPacificoSur = ubicacionService.crearUbicacion(Ubicacion("PacificoSur"))
        var ubicacionHolanda = ubicacionService.crearUbicacion(Ubicacion("Holanda"))
        var ubicacionCamerun = ubicacionService.crearUbicacion(Ubicacion("Camerun"))
        var ubicacionNigeria = ubicacionService.crearUbicacion(Ubicacion("Nigeria"))

        // se crea la enfermedad
        patogenoParaTest = Patogeno("virus", 80, 80, 80, 70, 70)
        especieTest = Especie(patogenoParaTest, "a", ubicacionJordania)
        patogenoService.crearPatogeno(patogenoParaTest)
        var especie = especieService.crearEspecie(especieTest)

        // se crean los sujetos de prueba y se infecta al que se va a mover
        var vectorDeTipoInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, ubicacionJordania))

        vectorService.infectar(vectorDeTipoInsecto.id!!, especie.id!!)

        // se conectan las ubicaciones
        ubicacionService.conectar(ubicacionPacificoSur.nombre, ubicacionJordania.nombre, TipoCamino.CAMINOMARITIMO)
        ubicacionService.conectar(ubicacionHolanda.nombre, ubicacionPacificoSur.nombre, TipoCamino.CAMINOMARITIMO)
        ubicacionService.conectar(ubicacionCamerun.nombre, ubicacionHolanda.nombre, TipoCamino.CAMINOMARITIMO)
        ubicacionService.conectar(ubicacionNigeria.nombre, ubicacionJordania.nombre, TipoCamino.CAMINOMARITIMO)
        ubicacionService.conectar(ubicacionCamerun.nombre, ubicacionNigeria.nombre, TipoCamino.CAMINOMARITIMO)


        // se ejecuta el metodo
        val errorEsperado = UbicacacionNoAlcanzable()::class.java

        assertThrows(errorEsperado){ubicacionService.moverPorCaminoMasCorto(vectorDeTipoInsecto.id!!, "Ghana")}

    }



// capacidad de expacion
    @Test
    fun seVerificaQueSeTieneEnCuentaLaUbicacionDePartidaComoCasoDeLaCapacidadDeExpancion(){

        val unaUbicacion = ubicacionService.crearUbicacion(Ubicacion("Ing"))
        val vectorHumano = vectorService.crearVector(Vector(TipoVector.HUMANO, unaUbicacion))
        val vectorAnimal = vectorService.crearVector(Vector(TipoVector.ANIMAL, unaUbicacion))
        val vectorInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, unaUbicacion))

        ubicacionService.crearUbicacionNeo(UbicacionNeo("Mexico"))

        val cantidadDeMovimientos = 0
        val ubicacionPartida      = "Mexico"
        assertEquals(1, ubicacionService.capacidadDeExpansion(vectorHumano.id!!, ubicacionPartida, cantidadDeMovimientos))
    }

    @Test
    fun cuandoSeIndicaMasMovimientosQueUbicacionesSoloAbarcaLaCantidadDeUbicionesDisponibles(){
        val unaUbicacion = ubicacionService.crearUbicacion(Ubicacion("Ing"))
        val vectorHumano = vectorService.crearVector(Vector(TipoVector.HUMANO, unaUbicacion))
        val vectorAnimal = vectorService.crearVector(Vector(TipoVector.ANIMAL, unaUbicacion))
        val vectorInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, unaUbicacion))

        ubicacionService.crearUbicacionNeo(UbicacionNeo("Mexico"))

        val cantidadDeMovimientos = 99
        val ubicacionPartida      = "Mexico"
        assertEquals(1, ubicacionService.capacidadDeExpansion(vectorHumano.id!!, ubicacionPartida, cantidadDeMovimientos))
    }

    @Test
    fun ubicacionServiceRetornaLaCapacidadDeExpancionDeUnVectorNoInfectado(){
        val unaUbicacion = ubicacionService.crearUbicacion(Ubicacion("Ing"))
        val vectorHumano = vectorService.crearVector(Vector(TipoVector.HUMANO, unaUbicacion))
        val vectorAnimal = vectorService.crearVector(Vector(TipoVector.ANIMAL, unaUbicacion))
        val vectorInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, unaUbicacion))


        var ubicacionMexico = ubicacionService.crearUbicacionNeo(UbicacionNeo("Mexico"))

        var ubicacionPeru = ubicacionService.crearUbicacionNeo(UbicacionNeo("Peru"))
        ubicacionService.conectar(ubicacionPeru.nombre, ubicacionMexico.nombre, TipoCamino.CAMINOTERRESTRE)

        ubicacionPeru = ubicacionService.recuperarUbicacionConNEO(ubicacionPeru.nombre)

        var ubicacionChile = ubicacionService.crearUbicacionNeo(UbicacionNeo("Chile"))
        ubicacionService.conectar(ubicacionChile.nombre, ubicacionPeru.nombre, TipoCamino.CAMINOAEREO)

        ubicacionChile = ubicacionService.recuperarUbicacionConNEO(ubicacionChile.nombre)

        var ubicacionEspaña = ubicacionService.crearUbicacionNeo(UbicacionNeo("España"))
        ubicacionService.conectar(ubicacionEspaña.nombre, ubicacionChile.nombre, TipoCamino.CAMINOMARITIMO)

        ubicacionEspaña = ubicacionService.recuperarUbicacionConNEO(ubicacionEspaña.nombre)

        var ubicacionIndonesia = ubicacionService.crearUbicacionNeo(UbicacionNeo("Indonesia"))
        ubicacionService.conectar(ubicacionIndonesia.nombre, ubicacionChile.nombre, TipoCamino.CAMINOMARITIMO)

        ubicacionIndonesia = ubicacionService.recuperarUbicacionConNEO(ubicacionIndonesia.nombre)

        val cantidadDeMovimientos = 3
        val ubicacionPartida      = "Mexico"
        assertEquals(2, ubicacionService.capacidadDeExpansion(vectorHumano.id!!, ubicacionPartida, cantidadDeMovimientos))
        assertEquals(5, ubicacionService.capacidadDeExpansion(vectorAnimal.id!!, ubicacionPartida, cantidadDeMovimientos))
        assertEquals(3, ubicacionService.capacidadDeExpansion(vectorInsecto.id!!, ubicacionPartida, cantidadDeMovimientos))
    }

    @Test
    fun CuandoUnaUbicacionServiceRetornaLaCapacidadDeExpancionDeUnVectorConMutacionElectrobranqueasEstePuedeAbarcarTodasLasUbicaciones(){
        val unPatogeno = patogenoService.crearPatogeno(Patogeno("Virus", 70,70,70,70,70))
        val unaUbicacion = ubicacionService.crearUbicacion(Ubicacion("Ing"))

        var unaEspecie  = Especie(unPatogeno, "Estafilococo", unaUbicacion)
        unaEspecie = especieService.crearEspecie(unaEspecie)

        var unaMutacion = MutacionV(TipoVector.INSECTO, TipoMutacion.ELECTROBRANQUEAS)//<--- Tipo de mutacion
        unaMutacion = mutacionService.agregarMutacion(unaEspecie.id!!, unaMutacion)

        val vectorInsecto = vectorService.crearVector(Vector(TipoVector.INSECTO, unaUbicacion))

        vectorInsecto.agregarMutacion(unaMutacion)
        vectorService.actualizarVector(vectorInsecto)

        var ubicacionMexico = ubicacionService.crearUbicacionNeo(UbicacionNeo("Mexico"))

        var ubicacionPeru = ubicacionService.crearUbicacionNeo(UbicacionNeo("Peru"))
        ubicacionService.conectar(ubicacionPeru.nombre, ubicacionMexico.nombre, TipoCamino.CAMINOTERRESTRE)

        ubicacionPeru = ubicacionService.recuperarUbicacionConNEO(ubicacionPeru.nombre)

        var ubicacionChile = ubicacionService.crearUbicacionNeo(UbicacionNeo("Chile"))
        ubicacionService.conectar(ubicacionChile.nombre, ubicacionPeru.nombre, TipoCamino.CAMINOAEREO)

        ubicacionChile = ubicacionService.recuperarUbicacionConNEO(ubicacionChile.nombre)

        var ubicacionEspaña = ubicacionService.crearUbicacionNeo(UbicacionNeo("España"))
        ubicacionService.conectar(ubicacionEspaña.nombre, ubicacionChile.nombre, TipoCamino.CAMINOMARITIMO)

        ubicacionEspaña = ubicacionService.recuperarUbicacionConNEO(ubicacionEspaña.nombre)

        var ubicacionIndonesia = ubicacionService.crearUbicacionNeo(UbicacionNeo("Indonesia"))
        ubicacionService.conectar(ubicacionIndonesia.nombre, ubicacionChile.nombre, TipoCamino.CAMINOMARITIMO)

        ubicacionIndonesia = ubicacionService.recuperarUbicacionConNEO(ubicacionIndonesia.nombre)

        val cantidadDeMovimientos = 3
        val ubicacionPartida      = "Mexico"

        assertEquals(5, ubicacionService.capacidadDeExpansion(vectorInsecto.id!!, ubicacionPartida, cantidadDeMovimientos))
    }

    @Test
    fun CuandoUnaUbicacionServiceRetornaLaCapacidadDeExpancionDeUnVectorConMutacionPropulsionMotoraEstePuedeAbarcarTodasLasUbicaciones(){

        val unPatogeno = patogenoService.crearPatogeno(Patogeno("Virus", 70,70,70,70,70))
        val unaUbicacion = ubicacionService.crearUbicacion(Ubicacion("Ing"))

        var unaEspecie  = Especie(unPatogeno, "Estafilococo", unaUbicacion)
        unaEspecie = especieService.crearEspecie(unaEspecie)

        var unaMutacion = MutacionV(TipoVector.HUMANO, TipoMutacion.PROPULSIONMOTORA)//<--- Tipo de mutacion
        unaMutacion = mutacionService.agregarMutacion(unaEspecie.id!!, unaMutacion)

        val vectorHumano = vectorService.crearVector(Vector(TipoVector.HUMANO, unaUbicacion))

        vectorHumano.agregarMutacion(unaMutacion)
        vectorService.actualizarVector(vectorHumano)

        var ubicacionMexico = ubicacionService.crearUbicacionNeo(UbicacionNeo("Mexico"))

        var ubicacionPeru = ubicacionService.crearUbicacionNeo(UbicacionNeo("Peru"))
        ubicacionService.conectar(ubicacionPeru.nombre, ubicacionMexico.nombre, TipoCamino.CAMINOTERRESTRE)

        ubicacionPeru = ubicacionService.recuperarUbicacionConNEO(ubicacionPeru.nombre)

        var ubicacionChile = ubicacionService.crearUbicacionNeo(UbicacionNeo("Chile"))
        ubicacionService.conectar(ubicacionChile.nombre, ubicacionPeru.nombre, TipoCamino.CAMINOAEREO)

        ubicacionChile = ubicacionService.recuperarUbicacionConNEO(ubicacionChile.nombre)

        var ubicacionEspaña = ubicacionService.crearUbicacionNeo(UbicacionNeo("España"))
        ubicacionService.conectar(ubicacionEspaña.nombre, ubicacionChile.nombre, TipoCamino.CAMINOMARITIMO)

        ubicacionEspaña = ubicacionService.recuperarUbicacionConNEO(ubicacionEspaña.nombre)

        var ubicacionIndonesia = ubicacionService.crearUbicacionNeo(UbicacionNeo("Indonesia"))
        ubicacionService.conectar(ubicacionIndonesia.nombre, ubicacionChile.nombre, TipoCamino.CAMINOMARITIMO)

        ubicacionIndonesia = ubicacionService.recuperarUbicacionConNEO(ubicacionIndonesia.nombre)

        val cantidadDeMovimientos = 3
        val ubicacionPartida      = "Mexico"

        assertEquals(5, ubicacionService.capacidadDeExpansion(vectorHumano.id!!, ubicacionPartida, cantidadDeMovimientos))
    }

    @Test
    fun CuandoUnaUbicacionServiceRetornaLaCapacidadDeExpancionDeUnVectorConMutacionBioalteracionGeneticaEsteNoHaceEfecto(){

        val unPatogeno = patogenoService.crearPatogeno(Patogeno("Virus", 70,70,70,70,70))
        val unaUbicacion = ubicacionService.crearUbicacion(Ubicacion("Ing"))

        var unaEspecie  = Especie(unPatogeno, "Estafilococo", unaUbicacion)
        unaEspecie = especieService.crearEspecie(unaEspecie)

        var unaMutacion = MutacionV(TipoVector.HUMANO, TipoMutacion.BIOALTGENETICA)//<--- Tipo de mutacion
        unaMutacion = mutacionService.agregarMutacion(unaEspecie.id!!, unaMutacion)

        val vectorHumano = vectorService.crearVector(Vector(TipoVector.HUMANO, unaUbicacion))

        vectorHumano.agregarMutacion(unaMutacion)
        vectorService.actualizarVector(vectorHumano)

        var ubicacionMexico = ubicacionService.crearUbicacionNeo(UbicacionNeo("Mexico"))

        var ubicacionPeru = ubicacionService.crearUbicacionNeo(UbicacionNeo("Peru"))
        ubicacionService.conectar(ubicacionPeru.nombre, ubicacionMexico.nombre, TipoCamino.CAMINOTERRESTRE)

        ubicacionPeru = ubicacionService.recuperarUbicacionConNEO(ubicacionPeru.nombre)

        var ubicacionChile = ubicacionService.crearUbicacionNeo(UbicacionNeo("Chile"))
        ubicacionService.conectar(ubicacionChile.nombre, ubicacionPeru.nombre, TipoCamino.CAMINOAEREO)

        ubicacionChile = ubicacionService.recuperarUbicacionConNEO(ubicacionChile.nombre)

        var ubicacionEspaña = ubicacionService.crearUbicacionNeo(UbicacionNeo("España"))
        ubicacionService.conectar(ubicacionEspaña.nombre, ubicacionChile.nombre, TipoCamino.CAMINOMARITIMO)

        ubicacionEspaña = ubicacionService.recuperarUbicacionConNEO(ubicacionEspaña.nombre)

        var ubicacionIndonesia = ubicacionService.crearUbicacionNeo(UbicacionNeo("Indonesia"))
        ubicacionService.conectar(ubicacionIndonesia.nombre, ubicacionChile.nombre, TipoCamino.CAMINOMARITIMO)

        ubicacionIndonesia = ubicacionService.recuperarUbicacionConNEO(ubicacionIndonesia.nombre)

        val cantidadDeMovimientos = 3
        val ubicacionPartida      = "Mexico"

        assertEquals(2, ubicacionService.capacidadDeExpansion(vectorHumano.id!!, ubicacionPartida, cantidadDeMovimientos))
    }

    @Test
    fun CuandoUnaUbicacionServiceRetornaLaCapacidadDeExpancionDeUnVectorConMutacionSupresionBiomecanicaGeneticaEsteNoHaceEfecto(){
        val unPatogeno = patogenoService.crearPatogeno(Patogeno("Virus", 70,70,70,70,70))
        val unaUbicacion = ubicacionService.crearUbicacion(Ubicacion("Ing"))

        var unaEspecie  = Especie(unPatogeno, "Estafilococo", unaUbicacion)
        unaEspecie = especieService.crearEspecie(unaEspecie)

        var unaMutacion = MutacionV(TipoVector.HUMANO, TipoMutacion.SUPBIOMECANICA)//<--- Tipo de mutacion
        unaMutacion = mutacionService.agregarMutacion(unaEspecie.id!!, unaMutacion)

        val vectorHumano = vectorService.crearVector(Vector(TipoVector.HUMANO, unaUbicacion))

        vectorHumano.agregarMutacion(unaMutacion)
        vectorService.actualizarVector(vectorHumano)

        var ubicacionMexico = ubicacionService.crearUbicacionNeo(UbicacionNeo("Mexico"))

        var ubicacionPeru = ubicacionService.crearUbicacionNeo(UbicacionNeo("Peru"))
        ubicacionService.conectar(ubicacionPeru.nombre, ubicacionMexico.nombre, TipoCamino.CAMINOTERRESTRE)

        ubicacionPeru = ubicacionService.recuperarUbicacionConNEO(ubicacionPeru.nombre)

        var ubicacionChile = ubicacionService.crearUbicacionNeo(UbicacionNeo("Chile"))
        ubicacionService.conectar(ubicacionChile.nombre, ubicacionPeru.nombre, TipoCamino.CAMINOAEREO)

        ubicacionChile = ubicacionService.recuperarUbicacionConNEO(ubicacionChile.nombre)

        var ubicacionEspaña = ubicacionService.crearUbicacionNeo(UbicacionNeo("España"))
        ubicacionService.conectar(ubicacionEspaña.nombre, ubicacionChile.nombre, TipoCamino.CAMINOMARITIMO)

        ubicacionEspaña = ubicacionService.recuperarUbicacionConNEO(ubicacionEspaña.nombre)

        var ubicacionIndonesia = ubicacionService.crearUbicacionNeo(UbicacionNeo("Indonesia"))
        ubicacionService.conectar(ubicacionIndonesia.nombre, ubicacionChile.nombre, TipoCamino.CAMINOMARITIMO)

        ubicacionIndonesia = ubicacionService.recuperarUbicacionConNEO(ubicacionIndonesia.nombre)

        val cantidadDeMovimientos = 3
        val ubicacionPartida      = "Mexico"

        assertEquals(2, ubicacionService.capacidadDeExpansion(vectorHumano.id!!, ubicacionPartida, cantidadDeMovimientos))
    }

    @AfterEach
    fun clearAll() {
        vectorService.clearAll()
        patogenoService.clearAll()
        especieService.clearAll()
        ubicacionService.clearAll()
        mutacionService.clearAll()
    }

}