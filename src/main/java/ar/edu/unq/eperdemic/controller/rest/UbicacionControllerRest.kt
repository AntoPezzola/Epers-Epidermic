package ar.edu.unq.eperdemic.controller.rest

import ar.edu.unq.eperdemic.controller.dto.UbicacionDTO
import ar.edu.unq.eperdemic.controller.dto.VectorDTO
import ar.edu.unq.eperdemic.modelo.Camino
import ar.edu.unq.eperdemic.modelo.TipoCamino
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorUbicacionNoExiste
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorVectorNoExiste
import ar.edu.unq.eperdemic.services.interfaces.UbicacionService
import ar.edu.unq.eperdemic.services.interfaces.VectorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/ubicacion")
class UbicacionControllerRest {

    @Autowired
    lateinit var ubicacionService: UbicacionService

    @Autowired
    lateinit var vectorService: VectorService

    @PostMapping("/crearUbicacion")
    fun crearUbicacion(@RequestBody ubicacion: UbicacionDTO): UbicacionDTO {
        return UbicacionDTO.desdeModelo(ubicacionService.crearUbicacion(ubicacion.aModelo()))
    }

    @GetMapping("/{id}")
    fun recuperar(@PathVariable("id") id: Long): UbicacionDTO {
        return UbicacionDTO.desdeModelo(ubicacionService.recuperarUbicacion(id));
    }

    @GetMapping()
    fun recuperarTodos(): List<UbicacionDTO> {
        return ubicacionService.recuperarTodasLasUbicaciones().map { ubicacion -> UbicacionDTO.desdeModelo(ubicacion) }
    }

    @PutMapping("/mover/{vectorId}/{ubicacionid}")
    fun mover(@PathVariable vectorId: Long, @PathVariable ubicacionid: Long): VectorDTO {
        ubicacionService.mover(vectorId, ubicacionid)
        val vectorRecuperado = vectorService.recuperarVector(vectorId)
        return VectorDTO.desdeModelo(vectorRecuperado)
    }

    @PutMapping("/conectar/{nombreDeUbicacion1}/{nombreDeUbicacion2}")
    fun conectar(
        @PathVariable("nombreDeUbicacion1") nombreDeUbicacion1: String,
        @PathVariable("nombreDeUbicacion2") nombreDeUbicacion2: String,
        @RequestBody tipoCamino: TipoCamino
    ) {
        ubicacionService.conectar(nombreDeUbicacion1, nombreDeUbicacion2, tipoCamino)
    }

    @GetMapping("/conectados/{nombreDeUbicacion}")
    fun conectados(@PathVariable("nombreDeUbicacion") nombreDeUbicacion: String): List<UbicacionDTO> {
        return ubicacionService.conectados(nombreDeUbicacion).map { u -> UbicacionDTO.desdeModelo(u) }
    }

    @PutMapping("/moverPorCaminoMasCorto/{vectorId}/{nombreDeUbicacion}")
    fun moverMasCorto(
        @PathVariable("vectorId") vectorId: Long,
        @PathVariable("nombreDeUbicacion") nombreDeUbicacion: String
    ): VectorDTO {
        ubicacionService.moverPorCaminoMasCorto(vectorId, nombreDeUbicacion)
        val vectorRecu = vectorService.recuperarVector(vectorId)
        return VectorDTO.desdeModelo(vectorRecu)
    }


    @ExceptionHandler(ErrorUbicacionNoExiste::class)
    fun handleNotFoundException(ex: ErrorUbicacionNoExiste): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
    }

    @ExceptionHandler(ErrorVectorNoExiste::class)
    fun handleNotFoundException(ex: ErrorVectorNoExiste): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
    }

}


