package ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.modelo.exceptions.ExceptionGenericNotFoundID
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring.SpringEspecieDAO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorEspecieExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorEspecieNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorPatogenoNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorUbicacionNoExiste
import ar.edu.unq.eperdemic.services.interfaces.EspecieService
import ar.edu.unq.eperdemic.services.interfaces.PatogenoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SpringEspecieServiceImpl() : EspecieService {

    @Autowired
    private lateinit var especieDAO: SpringEspecieDAO

      override fun crearEspecie(especie: Especie): Especie {
          if (especie.patogeno?.id == null) {
              throw ErrorPatogenoNoExiste()
          }
          if (especie.paisDeOrigen?.id == null) {
              throw ErrorUbicacionNoExiste()
          }
          return especieDAO.save(especie)
      }

    override fun actualizar(especie: Especie) {
        if(especie.id != null && especieDAO.existsById(especie.id!!)) {
            especieDAO.save(especie)
        } else {
            throw ErrorEspecieNoExiste()
        }

    }
    override fun recuperar(especieId: Long): Especie {
       return especieDAO.findByIdOrNull(especieId) ?: throw ErrorEspecieNoExiste()
    }
    override fun recuperarTodasLasEspecies(): List<Especie> {
      return especieDAO.findAll().toList()
    }

    override fun cantidadDeInfectados(especieId: Long): Int {
        try {
            return especieDAO.cantidadDeInfectados(especieId)
        } catch (e: DataIntegrityViolationException) {
            throw ErrorEspecieNoExiste()
        }
    }

    override fun infectadorProfesionalEn(nombreDeLaUbicacion: String): String {
          return especieDAO.infectadorProfesionalEn(nombreDeLaUbicacion)
    }

    override fun clearAll() {
        especieDAO.deleteAll()
    }
}