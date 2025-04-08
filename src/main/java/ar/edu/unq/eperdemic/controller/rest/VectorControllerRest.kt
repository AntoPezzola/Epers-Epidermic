package ar.edu.unq.eperdemic.controller.rest

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.controller.dto.EspecieDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ar.edu.unq.eperdemic.controller.dto.VectorDTO
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorEspecieNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorPatogenoNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorUbicacionNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorVectorNoExiste
import ar.edu.unq.eperdemic.services.interfaces.VectorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestController
@CrossOrigin
@RequestMapping("/vector")
class VectorControllerRest(@Autowired private val vectorService: VectorService) {

    @PostMapping
    fun crearVector(@RequestBody vector: VectorDTO) = vectorService.crearVector(vector.aModelo())


    @PostMapping("/updateVector")
    fun actualizarVector(@RequestBody vectorDTO: VectorDTO): VectorDTO {
        vectorService.actualizarVector(vectorDTO.aModelo())
        val vetorRecuperado = vectorService.recuperarVector(vectorDTO.id!!)
        return VectorDTO.desdeModelo(vetorRecuperado)
    }


    @GetMapping("/{id}")
    fun recuperarVector(@PathVariable id: Long): VectorDTO {
        val vector = vectorService.recuperarVector(id)
        return VectorDTO.desdeModelo(vector)
    }

    @GetMapping()
    fun recuperarTodosLosVectores(): List<VectorDTO> {
        return vectorService.recuperarTodosLosVectores().map { vector -> VectorDTO.desdeModelo(vector) }
    }

    @PostMapping("infectar/{idVector}/{idEspecie}")
    fun infectar(@PathVariable idVector: Long, @PathVariable idEspecie: Long): VectorDTO {
        vectorService.infectar(idVector, idEspecie)
        val vector = vectorService.recuperarVector(idVector)
        return VectorDTO.desdeModelo(vector)
    }

    @GetMapping("/enfermedades/{idVector}")
    fun enfermedades(@PathVariable idVector: Long): List<EspecieDTO> {
        return vectorService.enfermedades(idVector).map { e ->
            EspecieDTO.desdeModelo(e)

        }

    }
    @GetMapping("/cantidadDeVectores/{nombreUbicacion}")
    fun cantEn(@PathVariable nombreUbicacion: String ) : Int {
        return vectorService.cantEn(nombreUbicacion)
    }

    @ExceptionHandler(ErrorVectorNoExiste::class)
    fun handleNotFoundException(ex: ErrorVectorNoExiste): ResponseEntity<String> {
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