package ar.edu.unq.eperdemic.modelo

class ReporteDeContagios(){

     var id:Long? = null

     var cantVectoresPresentes: Int? = null
     var cantVectoresInfectados: Int? = null
     var nombreEspQueInfectaMasVectores: String? = null

     constructor(cantVectoresPresentes: Int, cantVectoresInfectados: Int,
                 nombreEspQueInfectaMasVectores: String) : this() {
          this.cantVectoresPresentes = cantVectoresPresentes
          this.cantVectoresInfectados = cantVectoresInfectados
          this.nombreEspQueInfectaMasVectores = nombreEspQueInfectaMasVectores
     }



}