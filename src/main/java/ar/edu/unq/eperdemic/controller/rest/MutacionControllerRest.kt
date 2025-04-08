package ar.edu.unq.eperdemic.controller.rest

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.controller.dto.EspecieDTO
import ar.edu.unq.eperdemic.controller.dto.MutacionDTO
import ar.edu.unq.eperdemic.services.interfaces.MutacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/mutacion")
class MutacionControllerRest {
    @Autowired
    lateinit var mutacionService: MutacionService

    @PostMapping("/agregarMutacionBioAltGenetica")
    fun agregarMutacion(@RequestBody mutacion: MutacionDTO) {
         mutacionService.agregarMutacion(mutacion.especie.id!!, mutacion.aModelo())
    }
}

