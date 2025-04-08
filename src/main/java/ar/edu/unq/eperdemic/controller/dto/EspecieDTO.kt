package ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.controller.dto
import ar.edu.unq.eperdemic.controller.dto.UbicacionDTO
import ar.edu.unq.eperdemic.modelo.Especie

class EspecieDTO(
    val id: Long?,
    val nombre: String?,
    val paisDeOrigen: UbicacionDTO?,
    val patogeno: PatogenoDTO?
) {
    companion object {
        fun desdeModelo(especie: Especie) =
            EspecieDTO(
                id = especie.id,
                nombre = especie.nombre,
                paisDeOrigen = UbicacionDTO.desdeModelo(especie.paisDeOrigen!!),
                patogeno = PatogenoDTO.desdeModelo(especie.patogeno!!)
            )
    }

    fun aModelo(): Especie {
        val especie = Especie()
        especie.id = this.id
        especie.nombre = this.nombre
        especie.paisDeOrigen = this.paisDeOrigen?.aModelo()
        especie.patogeno = this.patogeno?.aModelo()
        return especie
    }
}