package ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.modelo.exceptions.ExceptionGenericNotFoundID
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring.SpringEspecieDAO
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring.SpringUbicacionDAO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorNoHayVectoresEnLaUbicacion
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorPatogenoNoExiste
import ar.edu.unq.eperdemic.persistencia.dao.spring.SpringPatogenoDAO
import ar.edu.unq.eperdemic.services.interfaces.PatogenoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SpringPatogenoServiceImpl() : PatogenoService {
    @Autowired
    private lateinit var ubicacionDAO : SpringUbicacionDAO
    @Autowired
    private lateinit var patogenoDAO: SpringPatogenoDAO
    @Autowired
    private lateinit var especieDAO: SpringEspecieDAO


    override fun crearPatogeno(patogeno: Patogeno): Patogeno {
           return patogenoDAO.save(patogeno)
    }

    @Throws
    override fun recuperarPatogeno(id: Long?): Patogeno {
       return patogenoDAO.findByIdOrNull(id!!) ?: throw ErrorPatogenoNoExiste()
    }

    override fun recuperarATodosLosPatogenos(): List<Patogeno> {
        return patogenoDAO.findAll().toList()
    }

    override fun agregarEspecie(idDePatogeno: Long, nombreEspecie: String, idUbicacion: Long): Especie {
        val patoRecu            = patogenoDAO.findByIdOrNull(idDePatogeno) ?: throw ErrorPatogenoNoExiste()
        val ubicacionRecuperada = ubicacionDAO.findByIdOrNull(idUbicacion)
        val vectoresDeUbicacion = ubicacionDAO.obtenerVectoresDeUbicacion(ubicacionRecuperada!!.id!!)

        val unaEspecie = patoRecu.crearEspecie(nombreEspecie, ubicacionRecuperada)

        unaEspecie.infectarUnVectorAlAzar(vectoresDeUbicacion) ?: throw ErrorNoHayVectoresEnLaUbicacion()

        val especieActualizada = especieDAO.save(unaEspecie)

        return especieActualizada
    }



    override fun actualizarPatogeno(patogeno: Patogeno) {
        try {
             var pato = recuperarPatogeno(patogeno.id)
             patogenoDAO.save(patogeno)
         } catch(e: Exception){
             throw  ExceptionGenericNotFoundID(patogeno.id!!)
         }
    }

    override fun especiesDePatogeno(patogenoId: Long): List<Especie> {
        return patogenoDAO.especiesDePatogeno(patogenoId)
    }

    override fun esPandemia(especieId: Long): Boolean {
        return especieDAO.ubicacionesDeEspecie(especieId) > ubicacionDAO.cantDeUbicaciones() / 2
    }
    override fun clearAll() {
        patogenoDAO.deleteAll()
    }



}