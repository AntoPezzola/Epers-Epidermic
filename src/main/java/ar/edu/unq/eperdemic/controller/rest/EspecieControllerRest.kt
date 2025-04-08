package ar.edu.unq.eperdemic.controller.rest

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.controller.dto.EspecieDTO
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorEspecieExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorEspecieNoExiste
import ar.edu.unq.eperdemic.services.interfaces.EspecieService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/especie")
class EspecieControllerRest {

    @Autowired
    lateinit var especieService : EspecieService

    @PostMapping("/crearEspecie")
    fun crearUbicacion(@Validated @RequestBody especie: EspecieDTO) : EspecieDTO {
        return EspecieDTO.desdeModelo(especieService.crearEspecie(especie.aModelo()))
    }

    @GetMapping("/{id}")
    fun recuperarEspecie(@PathVariable id: Long): EspecieDTO {
        val especie = especieService.recuperar(id)
        return EspecieDTO.desdeModelo(especie)
    }
    @GetMapping() // si mando especie solo, me da todas las especies
    fun recuperarTodos() : List<EspecieDTO> {
        return especieService.recuperarTodasLasEspecies().map { especie -> EspecieDTO.desdeModelo(especie) }
    }

    @GetMapping("/cantidadInfectados/{id}")
    fun cantidadDeInfectados(@PathVariable especieId: Long): Int {
        return especieService.cantidadDeInfectados(especieId)
    }

    @ExceptionHandler(ErrorEspecieNoExiste::class)
    fun handleNotFoundException(ex: ErrorEspecieNoExiste): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
    }

    @ExceptionHandler(ErrorEspecieExiste::class)
    fun handleNotFoundException(ex: ErrorEspecieExiste): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
    }

}
