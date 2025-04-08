package ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.controller.dto.EspecieDTO
import ar.edu.unq.eperdemic.modelo.TipoVector
import ar.edu.unq.eperdemic.modelo.Vector

class VectorDTO(
    val id: Long?,
    val tipoVector: TipoVector,
    val ubicacion: UbicacionDTO,
    val infecciones: List<EspecieDTO>
    ) {
        companion object {
            fun desdeModelo(vector: Vector) =
                VectorDTO(
                    id = vector.id,
                    tipoVector = vector.tipoVector,
                    ubicacion = UbicacionDTO.desdeModelo(vector.ubicacion),
                    infecciones = vector.infecciones.map { EspecieDTO.desdeModelo(it) }
                )
        }

        fun aModelo(): Vector {
            val vector = Vector(tipoVector, ubicacion.aModelo())
            vector.id = this.id
            vector.tipoVector = this.tipoVector
            vector.infecciones = this.infecciones.map { it.aModelo() }.toMutableList()
            return vector
        }
    }
