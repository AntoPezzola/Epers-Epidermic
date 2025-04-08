package ar.edu.unq.eperdemic.controller.rest

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.controller.dto.EspecieDTO
import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.controller.dto.PatogenoDTO
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorEspecieNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorPatogenoExistente
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorPatogenoNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorUbicacionNoExiste
import ar.edu.unq.eperdemic.services.interfaces.PatogenoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@CrossOrigin
@RequestMapping("/patogeno")
class PatogenoControllerREST(@Autowired private val patoService: PatogenoService) {
    @PostMapping("/patogeno")
    fun crearPatogeno(@RequestBody patogeno: PatogenoDTO) = patoService.crearPatogeno(patogeno.aModelo())

    @GetMapping("/{patogenoId}")
    fun recuperarPatogeno(@PathVariable patogenoId: Long) =
        PatogenoDTO.desdeModelo(patoService.recuperarPatogeno(patogenoId)!!)

    @GetMapping("/allPatogenos")
    fun recuperarATodosLosPatogenos() =
        patoService.recuperarATodosLosPatogenos().map { pato -> PatogenoDTO.desdeModelo(pato) }

    @PostMapping("/newEspecie/{idPatogeno}/{nombreEspecie}/{idUbicacion}")
    fun agregarEspecie(
        @PathVariable idPatogeno: Long,
        @PathVariable nombreEspecie: String,
        @PathVariable idUbicacion: Long
    ): PatogenoDTO {
        patoService.agregarEspecie(idPatogeno, nombreEspecie, idUbicacion)
        return PatogenoDTO.desdeModelo(patoService.recuperarPatogeno(idPatogeno))
    }


    @PostMapping("/updatePatogeno")
    fun actualizarPatogeno(@RequestBody patogeno: PatogenoDTO): PatogenoDTO {
        patoService.actualizarPatogeno(patogeno.aModelo())
        return PatogenoDTO.desdeModelo(patoService.recuperarPatogeno(patogeno.id))
    }

    @GetMapping("/allEspecies/{patogenoId}")
    fun especiesDePatogeno(@PathVariable patogenoId: Long): List<EspecieDTO> {
        return patoService.especiesDePatogeno(patogenoId).map { especie -> EspecieDTO.desdeModelo(especie) }
    }

    @GetMapping("/esPandemia/{especieId}")
    fun esPandemia(@PathVariable especieId: Long) = patoService.esPandemia(especieId)


    @ExceptionHandler(ErrorPatogenoNoExiste::class)
    fun handleNotFoundException(ex: ErrorPatogenoNoExiste): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
    }

    @ExceptionHandler(ErrorPatogenoExistente::class)
    fun handleNotFoundException(ex: ErrorPatogenoExistente): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
    }

    @ExceptionHandler(ErrorEspecieNoExiste::class)
    fun handleNotFoundException(ex: ErrorEspecieNoExiste): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
    }

    @ExceptionHandler(ErrorUbicacionNoExiste::class)
    fun handleNotFoundException(ex: ErrorUbicacionNoExiste): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
    }


}
