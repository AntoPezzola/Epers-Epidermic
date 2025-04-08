package ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.modelo.Patogeno

class PatogenoDTO (
    val id: Long?,
    val tipo : String?,
    val capacidadDeContagioHumano : Int,
    val capacidadDeContagioAnimal : Int,
    val capacidadDeContagioInsecto : Int,
    val cantidadDeEspecies: Int,
    val porcentajeDefensa: Int,
    val porcentajeBiomecanizacion : Int
){
    companion object {
        fun desdeModelo(patogeno: Patogeno) =
            PatogenoDTO(
                id = patogeno.id,
                tipo = patogeno.tipo,
                capacidadDeContagioHumano = patogeno.capacidadDeContagioHumano!!,
                capacidadDeContagioAnimal = patogeno.capacidadDeContagioAnimal!!,
                capacidadDeContagioInsecto = patogeno.capacidadDeContagioInsecto!!,
                cantidadDeEspecies = patogeno.cantidadDeEspecies,
                porcentajeDefensa = patogeno.defensaContraMicroorganismos!!,
                porcentajeBiomecanizacion = patogeno.capacidadDeBiomecanizacion!!
            )
    }
    fun aModelo(): Patogeno {
        val patogeno = Patogeno()
        patogeno.id = this.id
        patogeno.tipo = this.tipo
        patogeno.capacidadDeContagioHumano = this.capacidadDeContagioHumano
        patogeno.capacidadDeContagioAnimal = this.capacidadDeContagioAnimal
        patogeno.capacidadDeContagioInsecto = this.capacidadDeContagioInsecto
        patogeno.cantidadDeEspecies = this.cantidadDeEspecies
        patogeno.capacidadDeBiomecanizacion = this.porcentajeBiomecanizacion
        patogeno.defensaContraMicroorganismos = this.porcentajeDefensa
        return patogeno
    }

}