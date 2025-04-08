package ar.edu.unq.eperdemic.persistencia.dao.spring

import ar.edu.unq.eperdemic.modelo.MutacionV
import org.springframework.data.repository.CrudRepository

interface SpringMutacionDAO: CrudRepository<MutacionV, Long> {


}