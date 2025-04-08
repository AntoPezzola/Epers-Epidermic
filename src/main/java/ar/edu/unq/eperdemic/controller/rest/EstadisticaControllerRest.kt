
package ar.edu.unq.eperdemic.controller.rest

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.controller.dto.EspecieDTO
import ar.edu.unq.eperdemic.controller.dto.ReporteDeContagiosDTO
import ar.edu.unq.eperdemic.services.interfaces.EstadisticaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("/estadistica")
class EstadisticaControllerREST(@Autowired private var estadisticaService: EstadisticaService) {

    @GetMapping("/especieLider")
    fun especieLider(): EspecieDTO {
        val especieLider = estadisticaService.especieLider()
        return EspecieDTO.desdeModelo(especieLider)
    }

    @GetMapping("/lideres")
    fun lideres(): List<EspecieDTO> {
        return estadisticaService.lideres().map { e -> EspecieDTO.desdeModelo(e) }
    }

    @GetMapping("/reporteDeContagios/{nombreUbicacion}")
    fun reporteDeContagios(@PathVariable nombreUbicacion: String) = ReporteDeContagiosDTO.desdeModelo(estadisticaService.reporteDeContagios(nombreUbicacion))

}

