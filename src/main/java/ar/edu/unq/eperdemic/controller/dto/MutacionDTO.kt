package ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.modelo.*

class MutacionDTO(val especie:Especie, val tipoDeVector: TipoVector?, val potenciaDeMutacion: Long?, val tipoMutacion: TipoMutacion) {
    companion object {
        fun desdeModelo(mutacion: MutacionV) =
            MutacionDTO(
                especie = mutacion.especie,
                tipoDeVector = mutacion.tipoDeVector,
                potenciaDeMutacion = mutacion.potenciaDeMutacion,
                tipoMutacion = mutacion.tipoMutacion!!
            )
    }
    fun aModelo(): MutacionV {
        val mutacion = MutacionV()
        mutacion.especie = this.especie
        if (this.tipoMutacion == TipoMutacion.SUPBIOMECANICA) {
            mutacion.tipoMutacion = TipoMutacion.SUPBIOMECANICA
            mutacion.potenciaDeMutacion = this.potenciaDeMutacion
            return mutacion
        } else {
            mutacion.tipoDeVector = this.tipoDeVector
            mutacion.tipoMutacion = TipoMutacion.BIOALTGENETICA
            return  mutacion
        }
    }

}
