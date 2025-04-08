package ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring.SpringEspecieDAO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorUbicacionNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorVectorNoExiste
import ar.edu.unq.eperdemic.persistencia.dao.spring.SpringVectorDAO
import ar.edu.unq.eperdemic.services.interfaces.EspecieService
import ar.edu.unq.eperdemic.services.interfaces.UbicacionService
import ar.edu.unq.eperdemic.services.interfaces.VectorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SpringVectorServiceImpl(): VectorService {

    @Autowired
    private lateinit var vectorDao: SpringVectorDAO
    @Autowired
    private lateinit var  especieDAO: SpringEspecieDAO
    @Autowired
    private  lateinit var ubicacionService: UbicacionService

    override fun crearVector(vector: Vector): Vector {
        ubicacionService.recuperarUbicacionCon(vector.ubicacion.nombre) ?: throw ErrorUbicacionNoExiste()
        return  vectorDao.save(vector)
    }

    override fun actualizarVector(vector: Vector) {
        if (vector.id != null) {
            vectorDao.save(vector)
        } else {
            throw ErrorVectorNoExiste()
        }
    }

    override fun recuperarVector(vectorId: Long): Vector {
        return vectorDao.findByIdOrNull(vectorId) ?: throw ErrorVectorNoExiste()
    }

    override fun recuperarTodosLosVectores(): List<Vector> {
        return vectorDao.findAll().toList()
    }

    override fun infectar(vectorId: Long, especieId: Long) {
        val especieRecuperada = especieDAO.findByIdOrNull(especieId)
        val vectorRecuperado  = vectorDao.findByIdOrNull(vectorId)
        vectorRecuperado!!.infectar(especieRecuperada!!)
        especieDAO.save(especieRecuperada)
        vectorDao.save(vectorRecuperado)
    }


    override fun enfermedades(vectorId: Long): List<Especie> {
        val vectorRecuperado = vectorDao.findByIdOrNull(vectorId)
        return vectorRecuperado!!.infecciones
    }


    override fun cantEn(nombreDeLaUbicacion: String): Int {
        return vectorDao.cantEn(nombreDeLaUbicacion)
    }

    override fun contagiarAVectores(vectorInfectado: Vector, vectores: List<Vector>) {
        /* val especies = enfermedades(vectorInfectado.id!!.toLong())
         for (e in especies){
             contagiarAVectoresConEspecie(vectorInfectado, e, vectores)
         }*/
    }


    override fun clearAll(){
        vectorDao.deleteAll()
    }
}
