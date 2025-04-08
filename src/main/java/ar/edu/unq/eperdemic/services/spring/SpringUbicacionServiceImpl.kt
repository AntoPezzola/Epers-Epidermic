package ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring.SpringUbicacionDAO
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exceptions.*
import ar.edu.unq.eperdemic.persistencia.dao.neo4J.UbicacionNeoDAO
import ar.edu.unq.eperdemic.persistencia.dao.spring.SpringVectorDAO
import ar.edu.unq.eperdemic.services.interfaces.UbicacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional

class SpringUbicacionServiceImpl() : UbicacionService {
    @Autowired
    private lateinit var ubicacionDAO: SpringUbicacionDAO

    @Autowired
    private lateinit var vectorDAO: SpringVectorDAO

    //Implementacion neo4j
    @Autowired
    private lateinit var ubicacionNEO: UbicacionNeoDAO

    override fun crearUbicacion(ubicacion: Ubicacion): Ubicacion {
        try {
            recuperarUbicacionCon(ubicacion.nombre)
            throw ErrorLaUbicacionExiste()
        } catch (e: EmptyResultDataAccessException) {
            var recu = ubicacionDAO.save(ubicacion)
            ubicacionNEO.save(UbicacionNeo(recu.nombre))
            return recu
        }

    }

    override fun crearUbicacionNeo(ubicacion: UbicacionNeo): UbicacionNeo {
        var recu = ubicacionNEO.save(ubicacion)
        return recu
    }


    override fun actualizarUbicacion(ubicacion: Ubicacion) {
        if (ubicacion.id == null) {
            throw ErrorUbicacionNoExiste()
        }
        val ubicacionAActualizar: Ubicacion =
            ubicacionDAO.findByIdOrNull(ubicacion.id!!) ?: throw ErrorUbicacionNoExiste()
        ubicacionDAO.save(ubicacion)
    }

    override fun recuperarUbicacion(ubicacionId: Long): Ubicacion {
        val ubicacionARecuperar = ubicacionDAO.findByIdOrNull(ubicacionId) ?: throw ErrorUbicacionNoExiste()

        return ubicacionARecuperar
    }

    override fun recuperarTodasLasUbicaciones(): List<Ubicacion> {
        return ubicacionDAO.findAll().toList()
    }


    override fun mover(vectorId: Long, ubicacionId: Long) {
        val vecMover = vectorDAO.findByIdOrNull(vectorId) ?: throw ErrorVectorNoExiste()
        val ubiOrigen =  recuperarUbicacion(vecMover.ubicacion.id!!)
        val ubiDestino=  recuperarUbicacion(ubicacionId)

        if(puedeMover(ubiOrigen.nombre,ubiDestino.nombre ,vecMover)) {
            vecMover.moverEInfectar(ubiDestino, ubicacionDAO.obtenerVectoresDeUbicacion(ubiDestino.id!!))
            vectorDAO.save(vecMover)
            ubicacionDAO.save(ubiDestino)

            for (vectorAct in ubicacionDAO.obtenerVectoresDeUbicacion(ubiDestino.id!!)) {
                vectorDAO.save(vectorAct)
            }
        }
        else{
            esUbicacionMuyLejana(ubiOrigen.nombre, ubiDestino.nombre)
        }

    }

   override fun puedeMover(nombreUbiOrigen:String, nombreDestino:String, vector:Vector):Boolean{
      var recorridos = vector.posiblesRecorridosSegunTipoVector()
       if ( (ubicacionNEO.puedeLlegarAlDestino(nombreUbiOrigen, nombreDestino, recorridos)).isPresent ){
           return (ubicacionNEO.puedeLlegarAlDestino(nombreUbiOrigen, nombreDestino, recorridos)).get()
       }else{
           throw UbicacacionNoAlcanzable()
       }
   }

    fun esUbicacionMuyLejana(nombreDeOrigen:String, nombreDestino:String): String {
        if ( ubicacionNEO.hayAlgunTipoDeCaminoDisponibleDesdeHasta(nombreDestino, nombreDeOrigen ).isPresent ){
            return ubicacionNEO.hayAlgunTipoDeCaminoDisponibleDesdeHasta(nombreDestino, nombreDeOrigen).get()
        }else {
            throw UbicacionMuyLejana()
        }
    }




    override fun obtenerVectoresDeUbicacion(ubicacionId: Long): List<Vector> {
        return ubicacionDAO.obtenerVectoresDeUbicacion(ubicacionId)
    }

    override fun cantidadDeVectores(ubicacionId: Long): Int {
        return ubicacionDAO.cantidadDeVectores(ubicacionId)
    }

    override fun expandir(ubicacionId: Long) {
        val ubiDestino = ubicacionDAO.findByIdOrNull(ubicacionId) ?: throw ErrorUbicacionNoExiste()
        val vectorInfectado: Vector? = ubicacionDAO.obtenerRandomInfectado(ubicacionId)
        if (vectorInfectado != null) {
            var vectoresUbicacion: List<Vector> = ubicacionDAO.obtenerVectoresDeUbicacion(ubicacionId)
            val ubicacion = ubicacionDAO.findByIdOrNull(ubicacionId)
            vectorInfectado.intentarContagiarAVectores(vectoresUbicacion)
            ubicacionDAO.save(ubicacion!!)
        }
        // QUEDA VER LOS ERRORES DE LOS OPCIONALES COMO LO RESOLVEMOS CON MOVER Y EXPANDIR
    }

    override fun obtenerVectoresInfectadosDeUbicacion(ubicacionId: Long): List<Vector> {
        return ubicacionDAO.obtenerVectoresInfectadosDeUbicacion(ubicacionId)
    }


    override fun recuperarUbicacionCon(nombreDeLaUbicacion: String): Ubicacion {

        return ubicacionDAO.recuperarUbicacionCon(nombreDeLaUbicacion) ?: throw ErrorUbicacionNoExiste()

    }

    override fun recuperarUbicacionConNEO(nombreUbi: String): UbicacionNeo {
        return ubicacionNEO.findByNombre(nombreUbi) ?: throw ErrorUbicacionNoExiste()
    }


    override fun conectar(nombreDeUbicacion1: String, nombreDeUbicacion2: String, tipoCamino: TipoCamino) {
        /*Conecta desde la ubi2 hacia la ubi1*/
        try {
            val ubicacion1 = recuperarUbicacionConNEO(nombreDeUbicacion1)
            val ubicacion2 = recuperarUbicacionConNEO(nombreDeUbicacion2)

            val camino = Camino(ubicacion2, tipoCamino)
            ubicacion1.agregarCamino(camino)
            ubicacionNEO.save(ubicacion1)

        } catch (e: Exception) {
            throw ErrorUbicacionNoExiste()
        }
    }


    override fun conectados(nombreDeUbicacion:String): List<Ubicacion>{
        return ubicacionNEO.conectados(nombreDeUbicacion).map { uNeo -> Ubicacion(uNeo.nombre)}

    }

    override fun moverPorCaminoMasCorto(vectorId: Long, nombreUbicacion:String) {
        var vecMover = vectorDAO.findByIdOrNull(vectorId) ?: throw ErrorVectorNoExiste()
        val caminoMasCorto = ubicacionNEO.encontrarCaminoMasCorto(vecMover.ubicacion.nombre, nombreUbicacion, vecMover.posiblesRecorridosSegunTipoVector())
        if(caminoMasCorto.isNotEmpty()){
            moverAlVectorPorCamino(vecMover, caminoMasCorto)
        } else {
            throw UbicacacionNoAlcanzable()
        }
    }

    override fun moverAlVectorPorCamino(vector: Vector, camino: List<UbicacionNeo>){
        for (u in camino){
            var ubicacion = ubicacionDAO.findByNombre(u.nombre)
            vector.moverEInfectar(ubicacion, ubicacionDAO.obtenerVectoresDeUbicacion(ubicacion.id!!))
            ubicacionDAO.save(ubicacion)
            actualizarVectoresEnUbicacion(ubicacion.id!!)
        }
    }

    override fun actualizarVectoresEnUbicacion(ubiId: Long){
        for (vector in ubicacionDAO.obtenerVectoresDeUbicacion(ubiId)){
            vectorDAO.save(vector)
        }
    }
    override fun capacidadDeExpansion(vectorId: Long, nombreDeUbicacion: String, movimientos: Int): Int {
        vectorDAO.findByIdOrNull(vectorId).let {
            return ubicacionNEO.cantidadDeUbicacionesParaExpansion(it!!.caminosPermitidos(), nombreDeUbicacion, movimientos)
        }
    }

    fun esHumano(unVector: Vector): Boolean{
        return unVector.tipoVector == TipoVector.HUMANO
    }

    fun esAnimal(unVector: Vector): Boolean{
        return unVector.tipoVector == TipoVector.ANIMAL
    }

    fun esInsecto(unVector: Vector): Boolean{
        return unVector.tipoVector == TipoVector.INSECTO
    }

    override fun clearAll() {

        ubicacionDAO.deleteAll()
        ubicacionNEO.deleteAll()
    }


}


