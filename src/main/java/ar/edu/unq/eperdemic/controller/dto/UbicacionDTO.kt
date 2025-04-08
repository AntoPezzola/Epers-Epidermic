package ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.modelo.Ubicacion

class UbicacionDTO(
    val id: Long?,
    val nombre: String
) {
    companion object {
        fun desdeModelo(ubicacion: Ubicacion) =
            UbicacionDTO(
                id = ubicacion.id,
                nombre = ubicacion.nombre
            )
    }

    fun aModelo(): Ubicacion {
        val ubicacion = Ubicacion(nombre)
        ubicacion.id = this.id
        return ubicacion
    }
}
