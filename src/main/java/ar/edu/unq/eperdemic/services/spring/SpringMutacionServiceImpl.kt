package ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring.SpringEspecieDAO
import ar.edu.unq.eperdemic.modelo.MutacionV
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorDatosIngresadosIncorrectos
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorEspecieNoExiste
import ar.edu.unq.eperdemic.persistencia.dao.spring.SpringMutacionDAO

import ar.edu.unq.eperdemic.services.interfaces.MutacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.TransactionSystemException
import org.springframework.transaction.annotation.Transactional
import javax.validation.ConstraintViolationException

@Service
@Transactional(rollbackFor = [ErrorDatosIngresadosIncorrectos::class])
class SpringMutacionServiceImpl() : MutacionService {
    @Autowired
    private lateinit var  especieDAO: SpringEspecieDAO
    @Autowired
    private lateinit var  mutacionDAO : SpringMutacionDAO

    override fun agregarMutacion(especieId: Long, mutacion: MutacionV): MutacionV {
        var especieRecuperada = especieDAO.findByIdOrNull(especieId)  ?: throw  ErrorEspecieNoExiste()
        mutacion.addEspecie(especieRecuperada)
        mutacionDAO.save(mutacion)
        return mutacion
    }
    override fun crearMutacion(mutacion: MutacionV):MutacionV{
        try {
            return mutacionDAO.save(mutacion)
        } catch (e: ConstraintViolationException){
            val errores = e.constraintViolations.map { it.message }
            throw  ErrorDatosIngresadosIncorrectos( errores)
        }
    }


    override fun clearAll() {
        mutacionDAO.deleteAll()
    }

}