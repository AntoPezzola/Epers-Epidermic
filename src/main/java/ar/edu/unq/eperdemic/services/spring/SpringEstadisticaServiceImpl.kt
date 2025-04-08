package ar.edu.unq.eperdemic.services.spring

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring.SpringEspecieDAO
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.persistencia.dao.spring.SpringUbicacionDAO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorEspecieNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorUbicacionNoExiste
import ar.edu.unq.eperdemic.persistencia.dao.spring.SpringVectorDAO
import ar.edu.unq.eperdemic.services.interfaces.EstadisticaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SpringEstadisticaServiceImpl() :EstadisticaService {

    @Autowired
    lateinit var vectorDAO: SpringVectorDAO
    @Autowired
    lateinit var ubicacionDAO: SpringUbicacionDAO
    @Autowired
    lateinit var especieDAO: SpringEspecieDAO

    override fun especieLider(): Especie {
        return especieDAO.especieLider() ?: throw  ErrorEspecieNoExiste()

    }

    override fun lideres(): List<Especie> {
        return especieDAO.lideres()
    }

    override fun reporteDeContagios(nombreDeLaUbicacion: String): ReporteDeContagios {

        val ubi = ubicacionDAO.recuperarUbicacionCon(nombreDeLaUbicacion) ?: throw  ErrorUbicacionNoExiste()

        return ReporteDeContagios(
                vectorDAO.cantEn(nombreDeLaUbicacion),
                ubicacionDAO.obtenerVectoresInfectadosDeUbicacion(ubi.id!!).size,
                especieDAO.infectadorProfesionalEn(nombreDeLaUbicacion)
            )
    }
}