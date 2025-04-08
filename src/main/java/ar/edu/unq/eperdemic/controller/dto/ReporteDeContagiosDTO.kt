package ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.modelo.ReporteDeContagios

class ReporteDeContagiosDTO(
    val id: Long?,
    val cantVectoresPresentes: Int?,
    val cantVectoresInfectados: Int?,
    val nombreEspQueInfectaMasVectores: String?
) {
    companion object {
        fun desdeModelo(reporte: ReporteDeContagios) = ReporteDeContagiosDTO(
            id = reporte.id,
            cantVectoresPresentes = reporte.cantVectoresPresentes,
            cantVectoresInfectados = reporte.cantVectoresInfectados,
            nombreEspQueInfectaMasVectores = reporte.nombreEspQueInfectaMasVectores
        )
    }

    fun aModelo(): ReporteDeContagios {
        val reporte = ReporteDeContagios()
        reporte.id = this.id
        reporte.cantVectoresPresentes = this.cantVectoresPresentes
        reporte.cantVectoresInfectados = this.cantVectoresInfectados
        reporte.nombreEspQueInfectaMasVectores = this.nombreEspQueInfectaMasVectores
        return reporte
    }

}